package com.googlecode.pigwt.example1.client;

import com.google.gwt.user.client.ui.*;
import com.googlecode.pigwt.client.PageGroup;

import java.util.Map;

public class MainGroup extends DockPanel implements PageGroup {
    public MainGroup() {
        setBorderWidth(1);
        final VerticalPanel menu = new VerticalPanel();
        menu.add(new Hyperlink("home", ""));
        menu.add(new Hyperlink("calculator", "utils.calculator"));
        menu.add(new Hyperlink("clock", "utils.clock"));
        menu.add(new Hyperlink("settings", "settings"));
        add(menu, DockPanel.WEST);
    }

    public void show(PageGroup parent, Map<String, String> params) {
        parent.setContent(this);
    }

    public void hide(PageGroup parent) {
        parent.setContent(null);
    }

    public void setContent(Widget content) {
        add(content, DockPanel.CENTER);
    }
}
