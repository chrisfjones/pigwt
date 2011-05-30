package com.googlecode.pigwt.client;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;

public abstract class ActivityFlyweight extends AbstractActivity {
    private Activity proxiedActivity;
    private PigwtPlace placeWithParams;

    protected ActivityFlyweight(final PigwtPlace placeWithParams) {
        this.placeWithParams = placeWithParams;
    }

    protected String getParam(int i) {
        return placeWithParams.getParam(i);
    }

    protected void setProxiedActivity(Activity proxiedActivity) {
        this.proxiedActivity = proxiedActivity;
    }

    protected Activity getProxiedActivity() {
        return proxiedActivity;
    }
}
