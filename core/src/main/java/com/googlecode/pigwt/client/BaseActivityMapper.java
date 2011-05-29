package com.googlecode.pigwt.client;

import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public abstract class BaseActivityMapper implements ActivityMapper {
    protected void fail(String shortPackageName, Throwable reason) {
        Window.alert("Unable to load '" + shortPackageName + "' due to: " + reason.getLocalizedMessage());
    }
    public abstract AcceptsOneWidget getShell();
}
