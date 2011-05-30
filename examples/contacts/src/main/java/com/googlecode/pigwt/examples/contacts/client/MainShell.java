package com.googlecode.pigwt.examples.contacts.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.googlecode.pigwt.client.Pigwt;
import com.googlecode.pigwt.client.Shell;

public class MainShell extends Composite implements Shell {
    public interface ShellUiBinder extends UiBinder<Widget, MainShell> {
    }

    @UiField
    SimplePanel main;

    public MainShell() {
        final ShellUiBinder uiBinder = (ShellUiBinder) GWT.create(ShellUiBinder.class);
        initWidget(uiBinder.createAndBindUi(this));
    }

    public AcceptsOneWidget getMain() {
        return main;
    }

    @UiHandler("home")
    void homeClick(ClickEvent e) {
        Pigwt.get().goTo("");
    }

    @UiHandler("contacts")
    void contactsClick(ClickEvent e) {
        Pigwt.get().goTo("contacts");
    }

    @UiHandler("settings")
    void settingsClick(ClickEvent e) {
        Pigwt.get().goTo("settings");
    }

    @Override
    public void setWidget(final IsWidget w) {
        main.setWidget(w);
    }
}
