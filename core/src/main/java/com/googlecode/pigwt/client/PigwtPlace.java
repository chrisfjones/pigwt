package com.googlecode.pigwt.client;

import com.google.gwt.place.shared.Place;

/**
 * A place in pigwt. Each of your packages that contains an Activity will automatically get one of these things, you
 * don't need to create any.
 */
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
