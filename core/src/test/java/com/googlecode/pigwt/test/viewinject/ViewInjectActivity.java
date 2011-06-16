package com.googlecode.pigwt.test.viewinject;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import javax.inject.Inject;

public class ViewInjectActivity extends AbstractActivity {
    public static interface View extends IsWidget {
    }
    
    private View view;

    @Inject
    public ViewInjectActivity(final View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        panel.setWidget(view);
    }
}
