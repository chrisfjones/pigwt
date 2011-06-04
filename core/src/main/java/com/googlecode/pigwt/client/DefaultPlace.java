package com.googlecode.pigwt.client;

import com.google.gwt.place.shared.*;

import java.util.Map;

public class DefaultPlace extends PigwtPlace {
    @Prefix("")
    public static class Tokenizer implements PlaceTokenizer<DefaultPlace> {
        @Override
        public DefaultPlace getPlace(final String token) {
            return new DefaultPlace(TokenizerUtil.parseParams(token));
        }

        @Override
        public String getToken(final DefaultPlace place) {
            return "";
        }
    }

    public DefaultPlace(final Map<String, String> params) {
        super(params);
    }

    @Override
    public String getPrefix() {
        return "";
    }
}
