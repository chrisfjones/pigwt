package com.googlecode.pigwt.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class Pigwt implements ActivityMapper, PlaceChangeEvent.Handler {
    private static final Pigwt instance = ((Pigwt) GWT.create(Pigwt.class));
    protected PlaceHistoryMapper historyMapper;
    protected PlaceController placeController;
    private EventBus eventBus;
    private ActivityManager activityManager;

    public static Pigwt get() {
        return instance;
    }
    
    protected void init() {
        eventBus = new SimpleEventBus();
        placeController = new PlaceController(eventBus);
        historyMapper = getPlaceHistoryMapper();
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);

        // todo: weak link, we register Pigwt as a PlaceChangeEvent handler before the activity manager to we can handle the shell game first
        eventBus.addHandler(PlaceChangeEvent.TYPE, this);
        activityManager = new ActivityManager(this, eventBus);

        final Shell rootShell = getNestedShell("");
        activityManager.setDisplay(rootShell);

        RootPanel.get().add(rootShell);
        historyHandler.register(placeController, eventBus, new DefaultPlace());
        historyHandler.handleCurrentHistory();
    }

    protected void fail(String shortPackageName, Throwable reason) {
        Window.alert("Unable to load '" + shortPackageName + "' due to: " + reason.getLocalizedMessage());
    }

    public final void goTo(String page, String ... params) {
        placeController.goTo(historyMapper.getPlace(TokenizerUtil.tokenize(page, params)));
    }

    @Override
    public void onPlaceChange(final PlaceChangeEvent event) {
        final Place newPlace = event.getNewPlace();
        if (!(newPlace instanceof PigwtPlace)) {
            return;
        }
        final Shell newShell = getNestedShell(((PigwtPlace) newPlace).getPrefix());
        activityManager.setDisplay(newShell);
    }

    private Shell getNestedShell(final String placePrefix) {
        final String[] placeChunks = placePrefix.split("\\.");
        Shell outerShell = getRootShell();
        outerShell.setWidget(null);
        String shellPlacePrefix = "";
        for (String placeChunk : placeChunks) {
            shellPlacePrefix += placeChunk;
            Shell innerShell = getShell(shellPlacePrefix);
            if (innerShell == null) {
                continue;
            }
            innerShell.setWidget(null);
            outerShell.setWidget(innerShell);
            outerShell = innerShell;
            shellPlacePrefix += "";
        }
        return outerShell;
    }

    protected abstract PlaceHistoryMapper getPlaceHistoryMapper();
    protected abstract Shell getShell(final String placePrefix);
    protected abstract Shell getRootShell();
}
