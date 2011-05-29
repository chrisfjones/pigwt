package com.googlecode.pigwt.pigwtplacestest.client.contact;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.googlecode.pigwt.client.Pigwt;
import com.googlecode.pigwt.client.Shell;

public class ContactShell extends Composite implements Shell {
    public interface ContactShellUiBinder extends UiBinder<Widget, ContactShell> {
    }

    @UiField Label demographic;
    @UiField Label addresses;
    @UiField Label other;
    @UiField SimplePanel main;

    public ContactShell() {
        final ContactShellUiBinder uiBinder = (ContactShellUiBinder) GWT.create(ContactShellUiBinder.class);
        initWidget(uiBinder.createAndBindUi(this));
    }

    public AcceptsOneWidget getMain() {
        return main;
    }

    @Override
    public void setWidget(final IsWidget w) {
        main.setWidget(w);
    }

    @UiHandler("demographic")
    void demographicsClick(ClickEvent e) {
        Pigwt.get().goTo("contact.demographic");
    }

    @UiHandler("addresses")
    void addressesClick(ClickEvent e) {
        Pigwt.get().goTo("contact.addresses");
    }

    @UiHandler("other")
    void otherClick(ClickEvent e) {
        Pigwt.get().goTo("contact.other");
    }
}
