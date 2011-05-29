package com.googlecode.pigwt.client;

import com.google.gwt.place.shared.*;

public class DefaultPlace extends PigwtPlace {
    @Prefix("")
    public static class Tokenizer implements PlaceTokenizer<DefaultPlace> {
        @Override
        public DefaultPlace getPlace(final String token) {
            return new DefaultPlace();
        }

        @Override
        public String getToken(final DefaultPlace place) {
            return "";
        }
    }

    @Override
    public String getPrefix() {
        return "";
    }
}
