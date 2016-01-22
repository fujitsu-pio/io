package com.fujitsu.dc.core.auth;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.common.utils.DcCoreUtils;
import com.fujitsu.dc.core.DcCoreAuthnException;

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
		// expireしていないかチェック(60秒くらいは過ぎても良い)
		ret = ret & ( exp + 60 ) * 1000 > System.currentTimeMillis() ;
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
		//null チェック
		if (!(rsaPubKey == null)) {
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
			// JSONでないものを返してきた
			throw new RuntimeException("Responded with non JSON", e);
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
