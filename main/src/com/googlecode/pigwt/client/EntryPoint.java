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
        final PageMap pageMap = GWT.create(PageMap.class);
        final PageLoader pageLoader = new PageLoader(pageMap);
        final NavStateGuy navStateGuy = new NavStateGuy(pageLoader);
        navStateGuy.fireCurrentNavState();
    }
}
