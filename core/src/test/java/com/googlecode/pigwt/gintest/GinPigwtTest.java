package com.googlecode.pigwt.gintest;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SimplePanel;
import com.googlecode.pigwt.client.ActivityFlyweight;
import com.googlecode.pigwt.client.Pigwt;
import com.googlecode.pigwt.test.params.ParamActivity;

public class GinPigwtTest extends GWTTestCase {
    private Pigwt pigwt;

    @Override
    public String getModuleName() {
        return "com.googlecode.pigwt.gintest";
    }

    public void gwtSetUp () {
        pigwt = Pigwt.get();
        pigwt.init();
    }

    public void testRoot() {
        final ActivityFlyweight activityFlyweight = startActivity("");
        Timer timer = new Timer() {
            @Override
            public void run() {
                final Activity activity = activityFlyweight.getProxiedActivity();
                assertNotNull(activity);
                assertTrue(activity instanceof RootActivity);
                finishTest();
            }
        };
        delayTestFinish(500);
        timer.schedule(100);
    }

    private ActivityFlyweight startActivity(String token) {
        final PlaceHistoryMapper placeHistoryMapper = pigwt.getPlaceHistoryMapper();
        final Place place = placeHistoryMapper.getPlace(token);
        final Activity paramActivityFlyweight = pigwt.getActivity(place);
        paramActivityFlyweight.start(new SimplePanel(), pigwt.getEventBus());
        return (ActivityFlyweight) paramActivityFlyweight;
    }
}
