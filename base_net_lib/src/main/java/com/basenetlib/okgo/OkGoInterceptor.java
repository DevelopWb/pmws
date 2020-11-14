/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.basenetlib.okgo;

import com.lzy.okgo.utils.IOUtils;
import com.lzy.okgo.utils.OkLogger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;


public class OkGoInterceptor implements Interceptor {

    private static final String TAG = "TokenInterceptor";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private java.util.logging.Level colorLevel;


    public OkGoInterceptor(String tag) {

    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

//        //请求日志拦截
//        logForRequest(request, chain.connection());

        //执行请求，计算请求时间
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        //响应日志拦截
        return logForResponse(response, tookMs);
    }
    public static void trustAllHosts() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    private void logForRequest(Request request, Connection connection) throws IOException {
//        RequestBody requestBody = request.body();
//        boolean hasRequestBody = requestBody != null;
//        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
//
//        try {
//            String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
//
//                if (hasRequestBody) {
//                    // Request body headers are only present when installed as a network interceptor. Force
//                    // them to be included (when available) so there values are known.
//                    if (requestBody.contentType() != null) {
//                    }
//                    if (requestBody.contentLength() != -1) {
//                    }
//                }
//                Headers headers = request.headers();
//                for (int i = 0, count = headers.size(); i < count; i++) {
//                    String name = headers.name(i);
//                    // Skip headers from the request body as they are explicitly logged above.
//                    if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
//                    }
//                }
//
//                if ( hasRequestBody) {
//                    if (isPlaintext(requestBody.contentType())) {
//                        bodyToString(request);
//                    } else {
//                    }
//                }
//        } catch (Exception e) {
//            OkLogger.printStackTrace(e);
//        } finally {
//        }
//    }

    private Response logForResponse(Response response, long tookMs) {
        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        ResponseBody responseBody = clone.body();

        try {
            Headers headers = clone.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
            }
            if ( HttpHeaders.hasBody(clone)) {
                if (responseBody == null) {
                    return response;
                }

                if (isPlaintext(responseBody.contentType())) {
                    byte[] bytes = IOUtils.toByteArray(responseBody.byteStream());
                    MediaType contentType = responseBody.contentType();
                    String body = new String(bytes, getCharset(contentType));
                    //根据和服务端的约定判断token过期
                    tokenExpired(body);
                    responseBody = ResponseBody.create(responseBody.contentType(), bytes);
                    return response.newBuilder().body(responseBody).build();
                } else {
                }
            }
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {

        }
        return response;
    }

    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null) charset = UTF8;
        return charset;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private static boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null) return false;
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        String subtype = mediaType.subtype();
        if (subtype != null) {
            subtype = subtype.toLowerCase();
            if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")) //
                return true;
        }
        return false;
    }

    private void bodyToString(Request request) {
        try {
            Request copy = request.newBuilder().build();
            RequestBody body = copy.body();
            if (body == null) return;
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            Charset charset = getCharset(body.contentType());
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        }
    }


    /**
     * 根据Response，判断Token是否失效
     *
     * @param content
     * @return
     */
    private void tokenExpired(String content) {
//        if (StrUtils.isStringValueOk(content)) {
//            try {
//                JSONObject object = new JSONObject(content);
//                int code = object.getInt("code");
//                if (code == 4000) {
//                    EventManager.sendStringMsg(PubUtil.EVENTBUS_TOCKEN_NOT_EXIST);
//                } else if (code == 4001) {
//                    EventManager.sendStringMsg(PubUtil.EVENTBUS_TOCKEN_EXPIRED);
//                }else if (code == 4002) {
//                    EventManager.sendStringMsg(PubUtil.EVENTBUS_TOCKEN_TEST_FAILED);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
    }
}
