package com.googlecode.pigwt.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.Map;

/**
 * Your window into the pigwt universe.
 * @see Pigwt get()
 */
public abstract class Pigwt implements ActivityMapper {
    private static final Pigwt instance = ((Pigwt) GWT.create(Pigwt.class));
    protected PlaceHistoryMapper historyMapper;
    protected PlaceController placeController;
    private EventBus eventBus;
    private ActivityManager activityManager;

    /**
     * Get the singleton Pigwt instance.
     * @return The one and only Pigwt
     */
    public static Pigwt get() {
        return instance;
    }

    /**
     * Go to the specified place with the specified parameters. The history will reflect the new place.
     * @param pkg The package/place you want to go to
     * @param params Any number of String parameters you want to pass along
     */
    public final void goTo(String pkg, Map<String, String> params) {
        placeController.goTo(historyMapper.getPlace(TokenizerUtil.tokenize(pkg, params)));
    }

    /**
     * Go to the specified place. The history will reflect the new place.
     * @param pkg The package/place you want to go to
     */
    public final void goTo(String pkg) {
        placeController.goTo(historyMapper.getPlace(TokenizerUtil.tokenize(pkg, null)));
    }

    protected void init() {
        eventBus = new SimpleEventBus();
        placeController = new PlaceController(eventBus);
        historyMapper = getPlaceHistoryMapper();
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);

        // todo: weak link, we register Pigwt as a PlaceChangeEvent handler before the activity manager so we can handle the shell game first
        eventBus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
            @Override
            public void onPlaceChange(final PlaceChangeEvent event) {
                final Place newPlace = event.getNewPlace();
                if (!(newPlace instanceof PigwtPlace)) {
                    return;
                }
                final Shell newShell = getNestedShell(((PigwtPlace) newPlace).getPrefix());
                activityManager.setDisplay(newShell);
            }
        });
        activityManager = new ActivityManager(this, eventBus);

        final Shell rootShell = getNestedShell("");
        activityManager.setDisplay(rootShell);

        RootPanel.get().add(rootShell);
        historyHandler.register(placeController, eventBus, new DefaultPlace(null));
        historyHandler.handleCurrentHistory();
    }

    protected void fail(String shortPackageName, Throwable reason) {
        Window.alert("Unable to load '" + shortPackageName + "' due to: " + reason.getLocalizedMessage());
    }

    private Shell getNestedShell(final String placePrefix) {
        final String[] placeChunks = placePrefix.split("\\.");
        Shell outerShell = getRootShell();
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
