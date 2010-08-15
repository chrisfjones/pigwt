package com.googlecode.pigwt.client;

import java.util.Map;

public abstract class PageFlyweight<P extends Page> {
    protected P page;

    protected PageFlyweight() {
    }

    public abstract void load(String token, Map<String, String> params, PageGroup parent);

    public void unload() {
        page.hide(null);
    }
}
