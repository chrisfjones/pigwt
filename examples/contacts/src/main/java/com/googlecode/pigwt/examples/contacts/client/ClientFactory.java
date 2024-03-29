package com.googlecode.pigwt.examples.contacts.client;

import com.google.gwt.core.client.GWT;
import com.googlecode.pigwt.examples.contacts.client.contacts.ContactsView;

public class ClientFactory {
    private final ContactsView contactsView = GWT.create(ContactsView.class);

    public ContactsView getContactsView() {
        return contactsView;
    }
}
