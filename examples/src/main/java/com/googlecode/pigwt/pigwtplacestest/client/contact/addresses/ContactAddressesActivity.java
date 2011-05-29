package com.googlecode.pigwt.pigwtplacestest.client.contact.addresses;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;

public class ContactAddressesActivity extends AbstractActivity {

    private final String first;
    private final String last;

    public ContactAddressesActivity(String first, String last) {
        this.first = first;
        this.last = last;
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        panel.setWidget(new Label("show addresses for " + first + " " + last));
    }
}
