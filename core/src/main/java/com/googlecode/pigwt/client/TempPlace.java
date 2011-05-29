package com.googlecode.pigwt.client;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class TempPlace extends Place {
    @Prefix("a")
    public static class Tokenizer implements PlaceTokenizer<TempPlace> {
        @Override
        public TempPlace getPlace(final String token) {
            return new TempPlace();
        }

        @Override
        public String getToken(final TempPlace place) {
            return "";
        }
    }
}
