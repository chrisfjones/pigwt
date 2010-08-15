package com.googlecode.pigwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class EntryPoint implements com.google.gwt.core.client.EntryPoint {
    public void onModuleLoad() {
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                deferredLoad();
            }
        });
    }

    private void deferredLoad() {
        final PageTree pageTree = GWT.create(PageTree.class);
        final PageLoader pageLoader = new PageLoader(pageTree);
        final NavStateGuy navStateGuy = new NavStateGuy(pageLoader);
        navStateGuy.fireCurrentNavState();
    }
}
