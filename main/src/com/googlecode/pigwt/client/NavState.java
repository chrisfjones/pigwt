package com.googlecode.pigwt.client;

import com.google.gwt.http.client.URL;

import java.util.*;

public class NavState {
    private String token;
    private Map<String, String> params;
    public static final String PARAM_PERSERVE_MESSAGE = "PARAM_PERSERVE_MESSAGE";
    // keys that should not be part of the URL
    private static final Set<String> internalParamKeys = new HashSet<String>(Arrays.asList(PARAM_PERSERVE_MESSAGE));

    public NavState(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, String> getParams() {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        return params;
    }

    public void addParam(String key, String value) {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        params.put(key, value);
    }

    public String getParam(String key) {
        if (params == null) return null;
        return params.get(key);
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof NavState)) return false;

        NavState navState = (NavState) o;

        if (params != null ? !params.equals(navState.params) : navState.params != null) return false;
        if (token != null ? !token.equals(navState.token) : navState.token != null) return false;

        return true;
    }

    public String toString(boolean includeInternalKeys, boolean encodeFragments) {
        StringBuilder result = new StringBuilder();
        result.append(token);
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (includeInternalKeys || !internalParamKeys.contains(entry.getKey())) {
                    if (result.toString().contains("?")) {
                        result.append("&");
                    } else {
                        result.append("?");
                    }
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (encodeFragments) {
                        key = key != null ? URL.encode(key) : key;
                        value = value != null ? URL.encode(value) : value;
                    }
                    result.append(key);
                    result.append("=");
                    result.append(value);
                }
            }
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return toString(false, false);
    }

    public String toInternalString() {
        return toString(true, true);
    }
}