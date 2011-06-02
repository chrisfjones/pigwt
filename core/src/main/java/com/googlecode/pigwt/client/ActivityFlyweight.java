package com.googlecode.pigwt.client;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;

public abstract class ActivityFlyweight extends AbstractActivity {
    private Activity proxiedActivity;
    private PigwtPlace placeWithParams;

    protected ActivityFlyweight(final PigwtPlace placeWithParams) {
        this.placeWithParams = placeWithParams;
    }

    protected Integer getIntParam(String name) {
        return null;
    }

    protected Long getLongParam(String name) {
        return null;
    }

    protected String getStringParam(String name) {
        return null;
    }

    protected void setProxiedActivity(Activity proxiedActivity) {
        this.proxiedActivity = proxiedActivity;
    }

    protected Activity getProxiedActivity() {
        return proxiedActivity;
    }
}
