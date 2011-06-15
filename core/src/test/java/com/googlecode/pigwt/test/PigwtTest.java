package com.googlecode.pigwt.test;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SimplePanel;
import com.googlecode.pigwt.client.ActivityFlyweight;
import com.googlecode.pigwt.client.DefaultPlace;
import com.googlecode.pigwt.client.Pigwt;
import com.googlecode.pigwt.test.inject.InjectActivity;
import com.googlecode.pigwt.test.params.ParamActivity;
import com.googlecode.pigwt.test.shell.TestShell;
import com.googlecode.pigwt.test.viewinject.ViewInjectActivity;

public class PigwtTest extends GWTTestCase {
    private Pigwt pigwt;

    @Override
    public String getModuleName() {
        return "com.googlecode.pigwt.test";
    }

    public void gwtSetUp () {
        pigwt = Pigwt.get();
        pigwt.init();
    }

    public void testRoot() {
        final PlaceHistoryMapper placeHistoryMapper = pigwt.getPlaceHistoryMapper();
        final Place rootPlace = placeHistoryMapper.getPlace("");
        assertNotNull(rootPlace);
        assertTrue(rootPlace instanceof DefaultPlace);

        final Activity rootActivityFlyweight = pigwt.getActivity(rootPlace);
        assertNotNull(rootActivityFlyweight);
        assertTrue(rootActivityFlyweight instanceof ActivityFlyweight);
        rootActivityFlyweight.start(new SimplePanel(), pigwt.getEventBus());

        Timer timer = new Timer() {
            @Override
            public void run() {
                final Activity rootActivity = ((ActivityFlyweight) rootActivityFlyweight).getProxiedActivity();
                assertNotNull(rootActivity);
                assertTrue(rootActivity instanceof RootActivity);
                finishTest();
            }
        };
        delayTestFinish(500);
        timer.schedule(100);
    }

    public void testActivityParameters() {
        final ActivityFlyweight paramActivityFlyweight = startActivity("params:stringParam=sp&intParam=1&longParam=2");

        Timer timer = new Timer() {
            @Override
            public void run() {
                final Activity activity = paramActivityFlyweight.getProxiedActivity();
                assertNotNull(activity);
                assertTrue(activity instanceof ParamActivity);
                final ParamActivity paramActivity = (ParamActivity) activity;
                assertEquals("sp", paramActivity.getStringParam());
                assertEquals(new Integer(1), paramActivity.getIntParam());
                assertEquals(new Long(2), paramActivity.getLongParam());
                finishTest();
            }
        };
        delayTestFinish(500);
        timer.schedule(100);
    }

    public void testShell() {
        final TestShell testShell = (TestShell) pigwt.getShell("shell");
        assertNull(testShell.getWidget());

        pigwt.goTo("shell");
        
        Timer timer = new Timer() {
            @Override
            public void run() {
                assertNotNull(testShell.getWidget());
                finishTest();
            }
        };
        delayTestFinish(500);
        timer.schedule(100);
    }

    public void testSimpleInjection() {
        final ActivityFlyweight injectActivityFlyweight = startActivity("inject:");
        Timer timer = new Timer() {
            @Override
            public void run() {
                final InjectActivity activity = (InjectActivity) injectActivityFlyweight.getProxiedActivity();
                assertNotNull(activity.getInjectableThing());
                finishTest();
            }
        };
        delayTestFinish(500);
        timer.schedule(100);
    }

    public void testViewInjection() {
        final ActivityFlyweight flyweight = startActivity("viewinject:");
        Timer timer = new Timer() {
            @Override
            public void run() {
                final ViewInjectActivity activity = (ViewInjectActivity) flyweight.getProxiedActivity();
                assertNotNull(activity.getView());
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
