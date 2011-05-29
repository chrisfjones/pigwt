package com.googlecode.pigwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;

public final class EntryPoint implements com.google.gwt.core.client.EntryPoint, GWT.UncaughtExceptionHandler {
    public void onModuleLoad() {
        GWT.setUncaughtExceptionHandler(this);
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                Pigwt.get().init();
            }
        });
    }

    @Override
    public void onUncaughtException(Throwable e) {
        // todo: something sensible
        Window.alert("Uncaught exception " + e.toString());
    }
}
