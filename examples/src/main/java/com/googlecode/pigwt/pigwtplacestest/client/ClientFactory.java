package com.googlecode.pigwt.pigwtplacestest.client;

import com.google.gwt.core.client.GWT;
import com.googlecode.pigwt.client.PigwtInjectable;
import com.googlecode.pigwt.pigwtplacestest.client.contacts.ContactsView;

@PigwtInjectable
public class ClientFactory {
    private final ContactsView contactsView = GWT.create(ContactsView.class);

    public ContactsView getContactsView() {
        return contactsView;
    }
}
