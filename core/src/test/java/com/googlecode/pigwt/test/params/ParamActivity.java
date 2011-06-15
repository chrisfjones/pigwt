package com.googlecode.pigwt.test.params;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ParamActivity extends AbstractActivity {
    private String stringParam;
    private Integer intParam;
    private Long longParam;

    public String getStringParam() {
        return stringParam;
    }

    public void setStringParam(final String stringParam) {
        this.stringParam = stringParam;
    }

    public Integer getIntParam() {
        return intParam;
    }

    public void setIntParam(final Integer intParam) {
        this.intParam = intParam;
    }

    public Long getLongParam() {
        return longParam;
    }

    public void setLongParam(final Long longParam) {
        this.longParam = longParam;
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
    }
}
