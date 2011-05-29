package com.googlecode.pigwt.client;

public class TokenizerUtil {
    private static final String PARAM_SEPERATOR = "|";

    public static String[] parseParams(String token) {
        return token.split("\\" + PARAM_SEPERATOR);
    }

    public static String tokenize(final String page, final String[] params) {
        return page + ":" + getParamString(params);
    }

    public static String getParamString(final String[] params) {
        StringBuilder paramString = new StringBuilder();
        String sep = "";
        for (String param : params) {
            paramString.append(sep).append(param);
            sep = PARAM_SEPERATOR;
        }
        return paramString.toString();
    }
}
