package com.googlecode.pigwt.client;

import com.google.gwt.place.shared.Place;

import java.util.Map;

/**
 * A place in pigwt. Each of your packages that contains an Activity will automatically get one of these things, you
 * don't need to create any.
 */
public abstract class PigwtPlace extends Place {
    private Map<String, String> params;

    public PigwtPlace(final Map<String, String> params) {
        this.params = params;
    }

    public String getParam(String key) {
        if (params == null) {
            return null;
        }
        return params.get(key);
    }

    public String getParamString() {
        return TokenizerUtil.getParamString(params);
    }

    public abstract String getPrefix();
}
