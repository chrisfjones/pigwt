package com.googlecode.pigwt.test.inject;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class InjectActivity extends AbstractActivity {
    private InjectableThing injectableThing;

    public InjectActivity(final InjectableThing injectableThing) {
        this.injectableThing = injectableThing;
    }

    public InjectableThing getInjectableThing() {
        return injectableThing;
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
    }
}
