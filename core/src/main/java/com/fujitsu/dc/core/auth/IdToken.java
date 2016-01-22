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
import org.apache.http.impl.client.cache.CachingHttpClient;
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

	private static final String GOOGLE_DISCOV_DOC_URL = "https://accounts.google.com/.well-known/openid-configuration";
	private final String ALG = "SHA256withRSA";

	private static final String KID = "kid";

	private final String KTY = "kty";

	private static final String ISS = "iss";
	private static final String EML = "email";
	private static final String AUD = "aud";
	private static final String EXP = "exp";

	private final String N = "n";
	private final String E = "e";

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
	 * 
	 * @param null
	 * @return boolean
	 */
	public void verify() throws DcCoreAuthnException {
		// expireしていないかチェック(60秒くらいは過ぎても良い)
		boolean expired = (exp + 60) * 1000 < System.currentTimeMillis();
		if (expired) {
			throw DcCoreAuthnException.OIDC_EXPIRED_ID_TOKEN.params(exp);
		}
		// 署名検証
		verifySignature();
	}

	/**
	 * 署名検証
	 * 
	 * @param null
	 * @return boolean
	 */
	private void verifySignature() {
		RSAPublicKey rsaPubKey = this.getKey();
		try {
			Signature sig = Signature.getInstance(ALG);
			sig.initVerify(rsaPubKey);
			sig.update((this.header + "." + this.payload).getBytes());
			boolean verified = sig.verify(DcCoreUtils.decodeBase64Url(this.signature));
			if (!verified) {
				// 署名検証結果、署名が不正であると認定
				throw DcCoreAuthnException.OIDC_AUTHN_FAILED;
			}
		} catch (NoSuchAlgorithmException e) {
			// 環境がおかしい以外でここには来ない
			throw new RuntimeException(ALG + " not supported.", e);
		} catch (InvalidKeyException e) {
			// バグ以外でここには来ない
			throw new RuntimeException(e);
		} catch (SignatureException e) {
			// IdTokenのSignatureがおかしい
			// the passed-in signature is improperly encoded or of the wrong
			// type,
			// if this signature algorithm is unable to process the input data
			// provided, etc.
			throw DcCoreAuthnException.OIDC_INVALID_ID_TOKEN.params("ID Token sig value is invalid.");
		}
	}

	/**
	 * 公開鍵情報から、IDTokenのkidにマッチする方で公開鍵を生成.
	 * 
	 * @return RSAPublicKey 公開鍵
	 */
	private RSAPublicKey getKey() {
		JSONArray jsonAry = getKeys();
		for (int i = 0; i < jsonAry.size(); i++) {
			JSONObject k = (JSONObject) jsonAry.get(i);
			String kid = (String) k.get(KID);
			if (kid.equals(this.kid)) {
				BigInteger n = new BigInteger(1, DcCoreUtils.decodeBase64Url((String) k.get(N)));
				BigInteger e = new BigInteger(1, DcCoreUtils.decodeBase64Url((String) k.get(E)));
				RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(n, e);
				try {
					KeyFactory kf = KeyFactory.getInstance((String) k.get(KTY));
					return (RSAPublicKey) kf.generatePublic(rsaPubKey);
				} catch (NoSuchAlgorithmException e1) {
					// ktyの値がRSA以外はサポートしない
					throw DcCoreException.NetWork.UNEXPECTED_VALUE.params(KTY, "RSA").reason(e1);
				} catch (InvalidKeySpecException e1) {
					// バグ以外でここには来ない
					throw new RuntimeException(e1);
				}
			}
		}
		// 該当するkidを持つ鍵情報が取れなかった場合
		throw DcCoreAuthnException.OIDC_INVALID_ID_TOKEN.params("ID Token header value is invalid.");
	}

	/**
	 * IdToken の検証のためのパース処理.
	 * 
	 * @param String
	 *            idTokenStr
	 * @return IdToken idToken
	 */
	public static IdToken parse(String idTokenStr) {
		IdToken ret = new IdToken();
		String[] splitIdToken = idTokenStr.split("\\.");
		if (splitIdToken.length != 3) {
			throw DcCoreAuthnException.OIDC_INVALID_ID_TOKEN.params("2 periods required.");
		}
		ret.header = splitIdToken[0];
		ret.payload = splitIdToken[1];
		ret.signature = splitIdToken[2];

		try {
			String headerDecoded = new String(DcCoreUtils.decodeBase64Url(ret.header), StandardCharsets.UTF_8);
			String payloadDecoded = new String(DcCoreUtils.decodeBase64Url(ret.payload), StandardCharsets.UTF_8);

			JSONObject header = (JSONObject) new JSONParser().parse(headerDecoded);
			JSONObject payload = (JSONObject) new JSONParser().parse(payloadDecoded);
			ret.kid = (String) header.get(KID);
			ret.issuer = (String) payload.get(ISS);
			ret.email = (String) payload.get(EML);
			ret.audience = (String) payload.get(AUD);
			ret.exp = (Long) payload.get(EXP);
		} catch (ParseException e) {
			// BASE64はOk.JSONのパースに失敗.
			throw DcCoreAuthnException.OIDC_INVALID_ID_TOKEN
					.params("Header and payload should be Base64 encoded JSON.");
		} catch (Exception e) {
			// BASE64が失敗.
			throw DcCoreAuthnException.OIDC_INVALID_ID_TOKEN.params("Header and payload should be Base64 encoded.");
		}
		return ret;
	}

	private static String getJwksUri(String endpoint) {
		return (String) getHttpJSON(endpoint).get("jwks_uri");
	}

	private static JSONArray getKeys() {
		return (JSONArray) getHttpJSON(getJwksUri(GOOGLE_DISCOV_DOC_URL)).get("keys");
	}

	/**
	 * Cacheを聞かせるため、ClientをStaticとする. たかだか限定されたURLのbodyを保存するのみであり、
	 * 最大キャッシュサイズはCacheConfigクラスで定義された16kbyte程度である. そのため、Staticで持つこととした.
	 */
	private static HttpClient httpClient = new CachingHttpClient();

	/**
	 * HTTPでJSONオブジェクトを取得する処理. Cacheが利用可能であればその値を用いる.
	 * 
	 * @param String
	 *            URL
	 * @return JSONObject
	 */
	public static JSONObject getHttpJSON(String url) {
		HttpGet get = new HttpGet(url);
		int status = 0;
		try {
			HttpResponse res = httpClient.execute(get);
			InputStream is = res.getEntity().getContent();
			status = res.getStatusLine().getStatusCode();
			String body = DcCoreUtils.readInputStreamAsString(is);
			JSONObject jsonObj = (JSONObject) new JSONParser().parse(body);
			return jsonObj;
		} catch (ClientProtocolException e) {
			// HTTPのプロトコル違反
			throw DcCoreException.NetWork.UNEXPECTED_RESPONSE.params(url, "proper HTTP response", status).reason(e);
		} catch (IOException e) {
			// サーバーに接続できない場合に発生
			throw DcCoreException.NetWork.HTTP_REQUEST_FAILED.params(HttpGet.METHOD_NAME, url).reason(e);
		} catch (ParseException e) {
			// JSONでないものを返してきた
			throw DcCoreException.NetWork.UNEXPECTED_RESPONSE.params(url, "JSON", status).reason(e);
		}
	}
}
