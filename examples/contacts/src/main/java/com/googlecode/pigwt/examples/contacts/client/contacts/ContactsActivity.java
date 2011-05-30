package com.googlecode.pigwt.examples.contacts.client.contacts;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.pigwt.examples.contacts.client.ClientFactory;
import com.googlecode.pigwt.examples.contacts.client.Contact;

import java.util.Arrays;

public class ContactsActivity extends AbstractActivity {
    private ContactsView view;

    public ContactsActivity(final ClientFactory clientFactory) {
        view = clientFactory.getContactsView();
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        // todo: go get this list from a server or something
        view.displayContacts(
                Arrays.asList(
                        new Contact("Bill", "Jones"), new Contact("Sue", "Smith"), new Contact("Pradeep", "Gonduptra"))
        );
        panel.setWidget(view);
    }
}
