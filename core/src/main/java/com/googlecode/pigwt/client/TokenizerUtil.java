package com.googlecode.pigwt.client;

import com.google.gwt.http.client.URL;

import java.util.HashMap;
import java.util.Map;

public class TokenizerUtil {
    private static final String TOKEN_SEPERATOR = ":";
    private static final String PARAM_SEPERATOR = "&";
    private static final String KEY_VALUE_SEPERATOR = "=";
    private static URLProxyGwtImpl urlProxyGwt;

    public static Map<String, String> parseParams(String token) {
        if (urlProxyGwt == null) {
            urlProxyGwt = new URLProxyGwtImpl();
        }
        return parseParams(token, urlProxyGwt);
    }
    public static Map<String, String> parseParams(String token, URLProxy urlProxy) {
        if (token == null || "".equals(token)) {
            return null;
        }

        final HashMap<String, String> params = new HashMap<String, String>();
        String[] paramChunks = token.split(PARAM_SEPERATOR);
        for (String chunk : paramChunks) {
            final String[] keyValue = chunk.split(KEY_VALUE_SEPERATOR);
            if (keyValue.length < 1) {
                continue;
            }
            String key = keyValue[0];
            String value = keyValue.length > 1 ? keyValue[1] : "";
            params.put(key != null ? urlProxy.decode(key) : key,
                        value != null ? urlProxy.decode(value) : value);
        }
        return params;
    }

    public static String tokenize(final String placeName, final Map<String, String> params) {
        return placeName + TOKEN_SEPERATOR + getParamString(params);
    }

    public static String getParamString(final Map<String, String> params) {
        if (urlProxyGwt == null) {
            urlProxyGwt = new URLProxyGwtImpl();
        }
        return getParamString(params, urlProxyGwt);
    }
    static String getParamString(final Map<String, String> params, URLProxy urlProxy) {
        StringBuilder result = new StringBuilder();
        if (params == null) {
            return "";
        }
        String seperator = "";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(seperator);
            String key = entry.getKey();
            String value = entry.getValue();
            result.append(key != null ? urlProxy.encode(key) : key);
            result.append(KEY_VALUE_SEPERATOR);
            result.append(value != null ? urlProxy.encode(value) : value);
            seperator = PARAM_SEPERATOR;
        }
        return result.toString();
    }

    static interface URLProxy {
        String decode(String s);
        String encode(String s);
    }

    static class URLProxyGwtImpl implements URLProxy {

        @Override
        public String decode(final String s) {
            return URL.decode(s);
        }

        @Override
        public String encode(final String s) {
            return URL.encode(s);
        }
    }
}
