package com.googlecode.pigwt.example1.client;

import com.google.gwt.user.client.ui.*;
import com.googlecode.pigwt.client.PageGroup;

import java.util.Map;

public class MainGroup extends DockPanel implements PageGroup {
    private Widget content;

    public MainGroup() {
        setBorderWidth(1);
        final VerticalPanel menu = new VerticalPanel();
        menu.add(new Hyperlink("home", ""));
        menu.add(new Hyperlink("utils", "utils"));
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
        if (this.content != null) {
            remove(this.content);
        }
        add(content, DockPanel.CENTER);
        this.content = content;
    }
}
