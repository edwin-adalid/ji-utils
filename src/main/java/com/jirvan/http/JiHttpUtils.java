/*

Copyright (c) 2015,2016 Jirvan Pty Ltd
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Jirvan Pty Ltd nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.jirvan.http;

import com.jirvan.lang.HttpResponseRuntimeException;
import com.jirvan.util.Json;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class JiHttpUtils {

    public static String get(String url,
                             String username,
                             String password) {
        try {

            URI uri = new URIBuilder(url).build();
            return makeRequest(URIUtils.extractHost(uri),
                               Request.Get(uri),
                               username,
                               password);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String post(String url,
                              String username,
                              String password) {
        try {

            URI uri = new URIBuilder(url).build();
            return makeRequest(URIUtils.extractHost(uri),
                               Request.Post(uri),
                               username,
                               password);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String post(String url,
                              String username,
                              String password,
                              Object object) {
        try {

            URI uri = new URIBuilder(url).build();
            return makeRequest(URIUtils.extractHost(uri),
                               Request.Post(uri)
                                      .addHeader("Content-Type", "application/json")
                                      .bodyString(Json.toJsonString(object), ContentType.APPLICATION_JSON),
                               username,
                               password);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String put(String url,
                             String username,
                             String password) {
        try {

            URI uri = new URIBuilder(url).build();
            return makeRequest(URIUtils.extractHost(uri),
                               Request.Put(uri),
                               username,
                               password);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String put(String url,
                             String username,
                             String password,
                             Object object) {
        try {

            URI uri = new URIBuilder(url).build();
            return makeRequest(URIUtils.extractHost(uri),
                               Request.Put(uri)
                                      .addHeader("Content-Type", "application/json")
                                      .bodyString(Json.toJsonString(object), ContentType.APPLICATION_JSON),
                               username,
                               password);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getJsonObject(Class valueType,
                                      String url,
                                      String username,
                                      String password) {
        String jsonString = get(url, username, password);
        return "".equals(jsonString) ? null : (T) Json.<T>fromJsonString(jsonString,
                                                                         valueType);
    }

    public static <T> T getJsonObject(Class valueType,
                                      boolean ignoreUnknownProperties,
                                      String url,
                                      String username,
                                      String password) {
        String jsonString = get(url, username, password);
        return "".equals(jsonString) ? null : (T) Json.<T>fromJsonString(jsonString,
                                                                         valueType,
                                                                         ignoreUnknownProperties);
    }

    public static <T> T postReturningJsonObject(Class valueType,
                                                String url,
                                                String username,
                                                String password) {
        String jsonString = post(url, username, password);
        return "".equals(jsonString) ? null : (T) Json.<T>fromJsonString(jsonString,
                                                                         valueType);
    }

    public static <T> T postReturningJsonObject(Class valueType,
                                                boolean ignoreUnknownProperties,
                                                String url,
                                                String username,
                                                String password) {
        String jsonString = post(url, username, password);
        return "".equals(jsonString) ? null : (T) Json.<T>fromJsonString(jsonString,
                                                                         valueType,
                                                                         ignoreUnknownProperties);
    }

    public static <T> T postReturningJsonObject(Class valueType,
                                                String url,
                                                String username,
                                                String password,
                                                Object object) {
        String jsonString = post(url, username, password, object);
        return "".equals(jsonString) ? null : (T) Json.<T>fromJsonString(jsonString,
                                                                         valueType);
    }

    public static <T> T postReturningJsonObject(Class valueType,
                                                boolean ignoreUnknownProperties,
                                                String url,
                                                String username,
                                                String password,
                                                Object object) {
        String jsonString = post(url, username, password, object);
        return "".equals(jsonString) ? null : (T) Json.<T>fromJsonString(jsonString,
                                                                         valueType,
                                                                         ignoreUnknownProperties);
    }

    public static <T> T putReturningJsonObject(Class valueType,
                                               String url,
                                               String username,
                                               String password) {
        String jsonString = put(url, username, password);
        return "".equals(jsonString) ? null : (T) Json.<T>fromJsonString(jsonString,
                                                                         valueType);
    }

    public static <T> T putReturningJsonObject(Class valueType,
                                               boolean ignoreUnknownProperties,
                                               String url,
                                               String username,
                                               String password) {
        String jsonString = put(url, username, password);
        return "".equals(jsonString) ? null : (T) Json.<T>fromJsonString(jsonString,
                                                                         valueType,
                                                                         ignoreUnknownProperties);
    }

    public static <T> T putReturningJsonObject(Class valueType,
                                               String url,
                                               String username,
                                               String password,
                                               Object object) {
        String jsonString = put(url, username, password, object);
        return "".equals(jsonString) ? null : (T) Json.<T>fromJsonString(jsonString,
                                                                         valueType);
    }

    public static <T> T putReturningJsonObject(Class valueType,
                                               boolean ignoreUnknownProperties,
                                               String url,
                                               String username,
                                               String password,
                                               Object object) {
        String jsonString = put(url, username, password, object);
        return "".equals(jsonString) ? null : (T) Json.<T>fromJsonString(jsonString,
                                                                         valueType,
                                                                         ignoreUnknownProperties);
    }

    public static class HttpErrorContentObject {

        public String errorName;
        public String errorMessage;
        public String errorInfo;
        public String message;
        public String description; // This was for ObPay which is now using title (can be removed???)
        public String detail;
        public String title;
        public List<FieldError> fieldErrors;
        public Long timestamp;
        public Integer status;
        public String error;
        public String exception;
        public String path;

        public static class FieldError {

            public String objectName;
            public String field;
            public String message;

        }

    }

    private static String makeRequest(HttpHost httpHost, Request request, String username, String password) {
        try {

            // Execute the request and get the HTTP response
            Executor executor = Executor.newInstance()
                                        .auth(httpHost, username, password)
                                        .authPreemptive(httpHost);
            HttpResponse httpResponse = executor.execute(request).returnResponse();

            // Process the response as an error or a successful response
            StatusLine statusLine = httpResponse.getStatusLine();
            HttpEntity entity = httpResponse.getEntity();
            if (statusLine.getStatusCode() >= 300) {
                String contentString = new Content(EntityUtils.toByteArray(entity), ContentType.getOrDefault(entity)).asString();
                HttpErrorContentObject error;
                try {
                    error = Json.fromJsonString(contentString, HttpErrorContentObject.class, true);
                } catch (Throwable t) {
                    throw new HttpResponseRuntimeException(statusLine.getStatusCode(),
                                                           statusLine.getReasonPhrase(),
                                                           statusLine.getReasonPhrase());
                }
                throw new HttpResponseRuntimeException(statusLine, error);
            } else {
                if (entity == null) {
                    return "";
                } else {
                    return new Content(EntityUtils.toByteArray(entity), ContentType.getOrDefault(entity)).asString();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
