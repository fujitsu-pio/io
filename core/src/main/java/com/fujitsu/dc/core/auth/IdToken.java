package com.fujitsu.dc.core.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import org.apache.commons.lang.CharSet;
import org.apache.commons.lang.CharSetUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.util.ajax.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.common.utils.DcCoreUtils;
import com.fujitsu.dc.core.DcCoreAuthnException;
import com.fujitsu.dc.core.DcCoreException;

public class IdToken {

    static Logger log = LoggerFactory.getLogger(IdToken.class);

	public String header;	
	public String payload;	
	public String signature;
	
	public String kid;
	
	public String email;
	public String issuer;
	public String audience;
	public Long exp;
	
	private static String GOOGLE_DISCOVERY_DOCUMENT_URL = "https://accounts.google.com/.well-known/openid-configuration";
	private static String ALG = "SHA256withRSA";
	
	public IdToken() {
	}
	
	public IdToken(JSONObject json) {
		this.email = (String) json.get("email");
		this.issuer = (String) json.get("issuer");
		this.audience = (String) json.get("audience");
		this.exp = (Long) json.get("exp");
	}
	
	/**
     * 最終検証結果を返す
     * @param null
     * @return boolean 
     */
	public boolean isValid() {	
		boolean ret = true;
		// expireしていないかチェック
		ret = ret & exp * 1000 > System.currentTimeMillis();
		// 署名検証
		ret = ret & verifySignature();
		return ret;
	}
	
	/**
     * 署名検証
     * @param null
     * @return boolean 
     */
	private boolean verifySignature() {	
		RSAPublicKey rsaPubKey = this.getKey();
		try {
			Signature sig = Signature.getInstance(ALG);
			sig.initVerify(rsaPubKey);
			sig.update((this.header + "." + this.payload).getBytes());
			return sig.verify(DcCoreUtils.decodeBase64Url(this.signature));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
     * 公開鍵情報から、IDTokenのkidにマッチする方で公開鍵を生成
     * @param null
     * @return RSAPublicKey 公開鍵
     */
	private RSAPublicKey getKey() {
		JSONArray jsonAry = getKeys();
		for (int i = 0; i < jsonAry.size(); i++) {
			JSONObject k = (JSONObject) jsonAry.get(i);
			String kid = (String) k.get("kid");
			if (kid.equals(this.kid)) {
			 	BigInteger n = new BigInteger(1, DcCoreUtils.decodeBase64Url((String) k.get("n")));
			 	BigInteger e = new BigInteger(1, DcCoreUtils.decodeBase64Url((String) k.get("e")));			 	
				RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(n,e);
				try {
					KeyFactory kf = KeyFactory.getInstance((String) k.get("kty"));
					return (RSAPublicKey) kf.generatePublic(rsaPubKey);
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
     * IdToken の検証のためのパース処理
     * @param String idTokenStr
     * @return IdToken idToken
     */
	public static IdToken parse(String idTokenStr) {
	 	IdToken ret = new IdToken();
		String[] splitIdToken = idTokenStr.split("\\.");
	 	ret.header = splitIdToken[0]; 
	 	ret.payload = splitIdToken[1]; 	
	 	ret.signature = splitIdToken[2]; 	
		
	 	String headerDecoded = new String(DcCoreUtils.decodeBase64Url(ret.header), StandardCharsets.UTF_8);
	 	String payloadDecoded = new String(DcCoreUtils.decodeBase64Url(ret.payload), StandardCharsets.UTF_8);
	 	try {
			JSONObject header = (JSONObject) new JSONParser().parse(headerDecoded);
			JSONObject payload = (JSONObject) new JSONParser().parse(payloadDecoded);
		 	ret.kid = (String) header.get("kid");
		 	ret.issuer = (String) payload.get("iss");
		 	ret.email = (String) payload.get("email");
		 	ret.audience = (String) payload.get("aud");
		 	ret.exp = (Long) payload.get("exp");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	private static String getJwksUri(String endpoint) {
		return (String) getHttpJSON(endpoint).get("jwks_uri");	
	}
	
	private static JSONArray getKeys() {
		//TODO キャッシュ
		return (JSONArray) getHttpJSON(getJwksUri(GOOGLE_DISCOVERY_DOCUMENT_URL)).get("keys");
	}
	
	/**
     * HTTPでJSONオブジェクトを取得する処理
     * @param String URL
     * @return JSONObject
     */
	public static JSONObject getHttpJSON(String url) {
        HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse res = client.execute(get);
			int status = res.getStatusLine().getStatusCode();
			InputStream is = res.getEntity().getContent();
			String resString = DcCoreUtils.readInputStreamAsString(is);
			JSONObject jsonObj = (JSONObject) new JSONParser().parse(resString);
			return jsonObj;
		} catch (ParseException e) {
			// GoogleがJSONでないものを返してきた
			throw new RuntimeException("Google responded with non JSON", e);
		} catch (ClientProtocolException e) {
			// ？？
			// TODO 適切なエラーメッセージに
			throw new RuntimeException(e);
		} catch (IOException e) {
			// Googleのサーバーに接続できない場合に発生
			// TODO 適切なエラーメッセージに
			throw new RuntimeException(e);
		}
	}
	
//	public static void main(String[] args) {
//		IdToken idt = IdToken.parse("eyJhbGciOiJSUzI1NiIsImtpZCI6ImNmMzE3ZGI0MzgxYzM0MDhhNWIyYTQzMWRjYTQ2OTU1NzBkMzNiMTMifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXRfaGFzaCI6Im9yVVBkYnRpMHV6SVMwc1lfbU5WMUEiLCJhdWQiOiI3NTkyNjQ5MjE2MzItNHZqYzc3czc4aDZzajA5cHJmYWIyMmc4OHRtMnBwMDEuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDY5NDg2Nzg3NzIwMDcwNTk2MjUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXpwIjoiNzU5MjY0OTIxNjMyLTR2amM3N3M3OGg2c2owOXByZmFiMjJnODh0bTJwcDAxLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiZW1haWwiOiJoa29uZG82MTdAZ21haWwuY29tIiwiaWF0IjoxNDUzMzYyMTQ5LCJleHAiOjE0NTMzNjU3NDksIm5hbWUiOiJIaWRlYWtpIEtvbmRvIiwicGljdHVyZSI6Imh0dHBzOi8vbGg0Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tdHRTQ09QZDJhZkkvQUFBQUFBQUFBQUkvQUFBQUFBQUFNRUUvcHdTUlhscGg3YWsvczk2LWMvcGhvdG8uanBnIiwiZ2l2ZW5fbmFtZSI6IkhpZGVha2kiLCJmYW1pbHlfbmFtZSI6IktvbmRvIiwibG9jYWxlIjoiZW4ifQ.BvCfr3FXhxF3fEMsZNW_i_YSIkXs_5ctDbSGo7hAGgstM7EUv5auXFizybCBRMoqOzDKIdD3SdRScBOfpqjlgHC7g0hRzMqHAxbLn9lO-NfiWTq7BWjVXwyVhKJxMzhbUgWVuAgDTgyE0gypOCTT7UKLvwY45lnFu7TP7vJllc_t7HraCPS-B9ihfRTHN5KdwWbG8AzPHHmhksb37FAEIycaYdlY9g9KX1TwR_E8XzbDahhI8QEctNAlnSMiZJN4C-OufnhEyPtlU-d_q2Dm3cvf-qbmzjerc6O8aSQIEq4WjdvoszEOc5BZMk4LSLgzaa388_PITUCFRRlSOvFzzz");
//		//IdToken idt = IdToken.parse("eyJhbGciOiJSUzI1NiIsImtpZCI6ImNmMzE3ZGI0MzgxYzM0MDhhNWIyYTQzMWRjYTQ2OTU1NzBkMzNiMTMifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXRfaGFzaCI6InJpYS02Q2tfdG9ialo4S2JtSmFyR2ciLCJhdWQiOiI3NTkyNjQ5MjE2MzItNHZqYzc3czc4aDZzajA5cHJmYWIyMmc4OHRtMnBwMDEuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDY5NDg2Nzg3NzIwMDcwNTk2MjUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXpwIjoiNzU5MjY0OTIxNjMyLTR2amM3N3M3OGg2c2owOXByZmFiMjJnODh0bTJwcDAxLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiZW1haWwiOiJoa29uZG82MTdAZ21haWwuY29tIiwiaWF0IjoxNDUzMzY1NDUwLCJleHAiOjE0NTMzNjkwNTAsIm5hbWUiOiJIaWRlYWtpIEtvbmRvIiwicGljdHVyZSI6Imh0dHBzOi8vbGg0Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tdHRTQ09QZDJhZkkvQUFBQUFBQUFBQUkvQUFBQUFBQUFNRUUvcHdTUlhscGg3YWsvczk2LWMvcGhvdG8uanBnIiwiZ2l2ZW5fbmFtZSI6IkhpZGVha2kiLCJmYW1pbHlfbmFtZSI6IktvbmRvIiwibG9jYWxlIjoiZW4ifQ.G5rJw5Exb_fpIm1XUdPLs9kNG8eNMg1Ef0p_6fc3Yo02PFSURpTS-zGiKOlIdU9wZ8aqu2yi9f7FsdBVaK9SSWpRc_-6kQa5VJF6MD7HSXkZq-4u9p6_aGrH9hrjftEIbszmp0mYXSyegDQHFacuUUGZbCt6OMEsQbN9oX6YeNhhCJ5aCIPU4cBSQxi_f291R6iXECyU5Le0spG1ljWxUaGb-1EHHWDchSHEP4HHj8vPNGroBX9YR7BMGLZO5cSHB4939dF13qPUjaTxEPGlG4f5CtZ-C02bnPwV2euaOlr_04Ap1FA5KC22poFaL8F4dmTy-7_Ss5K8YxG2kEAOBg");
//		System.out.println(idt.isValid());
//	}
	
    /**
     * Google Identity Platform OpenID Connect  https://developers.google.com/identity/protocols/OpenIDConnect
     * @param String idTokenStr
     * @return IdToken idToken
     */
	public static IdToken validateGoogle(String idTokenStr) {
        HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("https://www.googleapis.com/oauth2/v1/tokeninfo?id_token="
			+ DcCoreUtils.encodeUrlComp(idTokenStr)) ;

		try {
			HttpResponse res = client.execute(get);
			int status = res.getStatusLine().getStatusCode();
			InputStream is = res.getEntity().getContent();
			String resString = DcCoreUtils.readInputStreamAsString(is);
			JSONObject jsonObj = (JSONObject) new JSONParser().parse(resString);

			if (status == 200) {
				return new IdToken(jsonObj);
			} else if (status == 400) {
				throw DcCoreAuthnException.OIDC_INVALID_ID_TOKEN;
			}
		} catch (ParseException e) {
			// GoogleがJSONでないものを返してきた
			throw new RuntimeException("Google responded with non JSON", e);
		} catch (ClientProtocolException e) {
			// ？？
			// TODO 適切なエラーメッセージに
			throw new RuntimeException(e);
		} catch (IOException e) {
			// Googleのサーバーに接続できない場合に発生
			// TODO 適切なエラーメッセージに
			throw new RuntimeException(e);
		}
		return null;
	}
}
