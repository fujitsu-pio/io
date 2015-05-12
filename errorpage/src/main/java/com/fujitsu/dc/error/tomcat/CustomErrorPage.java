/**
 * personium.io
 * Copyright 2014 FUJITSU LIMITED
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fujitsu.dc.error.tomcat;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.RequestUtil;
import org.apache.catalina.valves.ErrorReportValve;

/**
 * Tomcatのカスタムエラーページ生成クラス.
 */
public class CustomErrorPage extends ErrorReportValve {

    static final int HTTP_STATUS_BAD_REQUEST = 400;

    @Override
    protected void report(Request request, Response response, Throwable throwable) {

        HttpServletResponse hres = (HttpServletResponse) response;
        int statusCode = response.getStatus();

        if (statusCode < HTTP_STATUS_BAD_REQUEST || (response.getContentWritten() > 0)) {
            return;
        }

        String message = RequestUtil.filter(response.getMessage());
        if (message == null) {
            message = "";
        }

        String report = null;
        try {
            report = sm.getString("http." + statusCode, message);
        } catch (Throwable t) {
            return;
        }
        if (report == null) {
            return;
        }

        String errorPage = makeErrorPage(statusCode, message,
                report, hres);

        try {
            hres.setContentType("text/html");
        } catch (Throwable t) {
            return;
        }

        try {
            Writer writer = response.getReporter();
            if (writer != null) {
                writer.write(errorPage);
            }
        } catch (IOException e) {
            return;
        } catch (IllegalStateException e) {
            return;
        }
    }

    /**
     * エラーページのHTMLを生成する.
     * @param statusCode HTTPステータスコード
     * @param message HTTPレスポンスメッセージ
     * @param report エラーレポート
     * @param response .
     * @return 生成したエラーページHTML
     */
    public static String makeErrorPage(int statusCode,
            String message,
            String report,
            HttpServletResponse response) {

        StringBuffer sb = new StringBuffer();

        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"");
        sb.append(" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html><head><title>");
        sb.append(sm.getString("errorReportValve.errorReport"));
        sb.append("</title>");
        sb.append("<style type=\"text/css\"><!--");
        sb.append(org.apache.catalina.util.TomcatCSS.TOMCAT_CSS);
        sb.append("--></style> ");
        sb.append("</head><body>");
        sb.append("<h1>");
        sb.append("HTTP Status " + statusCode);
        sb.append("</h1>");
        sb.append("<hr/>");
        sb.append("<p><b>");
        sb.append("message");
        sb.append("</b> ");
        sb.append(message);
        sb.append("</p>");
        sb.append("<p><b>");
        sb.append("description");
        sb.append("</b> ");
        sb.append(report);
        sb.append("</p>");

        sb.append("<hr/>");
        sb.append("<h3>").append("Persona Container Server").append("</h3>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
