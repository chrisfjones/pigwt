package com.googlecode.pigwt.test;

import com.google.gwt.user.client.Timer;
import com.googlecode.pigwt.client.ActivityFlyweight;
import com.googlecode.pigwt.test.viewinject.ViewInjectActivity;

public class PigwtTest_noGin extends PigwtTest {
    @Override
    public String getModuleName() {
        return "com.googlecode.pigwt.test";
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
}
