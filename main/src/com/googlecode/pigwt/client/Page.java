package com.googlecode.pigwt.client;

import java.util.Map;

public interface Page {
    void show(PageGroup parent, Map<String, String> params);
    void hide(PageGroup parent);
}
