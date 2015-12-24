package com.fujitsu.dc.core.auth;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.common.utils.DcCoreUtils;
import com.fujitsu.dc.core.DcCoreException;

public class IdToken {

    static Logger log = LoggerFactory.getLogger(IdToken.class);

	public String email;
	public String issuer;
	public String audience;

	public IdToken(JSONObject json) {
		this.email = (String) json.get("email");
		this.issuer = (String) json.get("issuer");
		this.audience = (String) json.get("audience");
	}

	public static IdToken parse(String idTokenStr) {
		return null;
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
				throw DcCoreException.Auth.REQUEST_PARAM_INVALID;
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
