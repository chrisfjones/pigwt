package com.googlecode.pigwt.client;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;

public abstract class ActivityFlyweight extends AbstractActivity {
    private Activity proxiedActivity;
    private PigwtPlace placeWithParams;

    protected ActivityFlyweight(final PigwtPlace placeWithParams) {
        this.placeWithParams = placeWithParams;
    }

    // todo: gracefully handle non-numbers
    protected Integer getIntParam(String name) {
        final String param = placeWithParams.getParam(name);
        if (param == null) {
            return null;
        }
        return new Integer(param);
    }

    // todo: gracefully handle non-numbers
    protected Long getLongParam(String name) {
        final String param = placeWithParams.getParam(name);
        if (param == null) {
            return null;
        }
        return new Long(param);
    }

    protected String getStringParam(String name) {
        return placeWithParams.getParam(name);
    }

    protected void setProxiedActivity(Activity proxiedActivity) {
        this.proxiedActivity = proxiedActivity;
    }

    public Activity getProxiedActivity() {
        return proxiedActivity;
    }
}
