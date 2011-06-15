package com.googlecode.pigwt.test.shell;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.pigwt.client.Shell;

public class TestShell extends Composite implements Shell {

    private SimplePanel simplePanel;

    public TestShell() {
        simplePanel = new SimplePanel();
        initWidget(simplePanel);
    }

    @Override
    public void setWidget(final IsWidget w) {
        simplePanel.setWidget(w);
    }

    public Widget getWidget() {
        return simplePanel.getWidget();
    }
}
