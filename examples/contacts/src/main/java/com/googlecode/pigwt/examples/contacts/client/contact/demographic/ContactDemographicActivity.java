package com.googlecode.pigwt.examples.contacts.client.contact.demographic;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;

public class ContactDemographicActivity extends AbstractActivity {

    private String first;
    private String last;

    public void setFirst(final String first) {
        this.first = first;
    }

    public void setLast(final String last) {
        this.last = last;
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        panel.setWidget(new Label("show demographic info for " + first + " " + last));
    }
}
