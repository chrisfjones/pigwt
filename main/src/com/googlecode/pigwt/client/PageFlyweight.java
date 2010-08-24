package com.googlecode.pigwt.client;

import java.util.Map;

public abstract class PageFlyweight<P extends Page> {
    protected P page;
    private final String uid;

    protected PageFlyweight(String uid) {
        this.uid = uid;
    }

    public P getPage() {
        return page;
    }

    public abstract void load(String token, Map<String, String> params, PageGroup parent);

    public void unload(PageGroup parent) {
        page.hide(parent);
        if (page instanceof Bindable) {
            ((Bindable) page).unbind();
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PageFlyweight && uid.equals(((PageFlyweight) other).uid);
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

    @Override
    public String toString() {
        return "PageFlyweight<" + uid + ">";
    }
}
