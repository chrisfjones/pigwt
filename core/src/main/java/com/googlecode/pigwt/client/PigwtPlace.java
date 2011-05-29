package com.googlecode.pigwt.client;

import com.google.gwt.place.shared.Place;

public abstract class PigwtPlace extends Place {
    private String[] params;

    public PigwtPlace(final String... params) {
        this.params = params;
    }

    public String getParam(int i) {
        return i < params.length ? params[i] : null;
    }

    public String getParamString() {
        return TokenizerUtil.getParamString(params);
    }

    public abstract String getPrefix();
}
