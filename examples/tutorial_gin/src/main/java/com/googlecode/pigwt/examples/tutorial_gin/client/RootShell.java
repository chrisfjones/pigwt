package com.googlecode.pigwt.examples.tutorial_gin.client;

import com.google.gwt.user.client.ui.*;
import com.googlecode.pigwt.client.Shell;

public class RootShell extends DockPanel implements Shell {
    private SimplePanel slot = new SimplePanel();

    public RootShell() {
        this.setBorderWidth(1);
        VerticalPanel links = new VerticalPanel();
        links.add(new Hyperlink("Home", ""));
        links.add(new Hyperlink("Characters", "characters:"));
        add(links, DockPanel.WEST);
        add(slot, DockPanel.CENTER);
    }

    @Override
    public void setWidget(final IsWidget w) {
        slot.setWidget(w);
    }
}
