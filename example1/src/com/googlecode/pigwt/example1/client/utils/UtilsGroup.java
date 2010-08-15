package com.googlecode.pigwt.example1.client.utils;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.pigwt.client.PageGroup;

import java.util.Map;

public class UtilsGroup extends Grid implements PageGroup {
    public UtilsGroup() {
        super(4, 1);
        setText(0, 0, "this is a utility...");
        setWidget(1, 0, new Hyperlink("calculator", "utils.calculator"));
        setWidget(2, 0, new Hyperlink("clock", "utils.clock"));
    }

    public void show(PageGroup parent, Map<String, String> params) {
        parent.setContent(this);
    }

    public void hide(PageGroup parent) {
        parent.setContent(null);
    }

    public void setContent(Widget content) {
        setWidget(3, 0, content);
    }
}
