package com.googlecode.pigwt.example1.client.utils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.pigwt.client.Bindable;
import com.googlecode.pigwt.client.PageGroup;

import java.util.Map;

public class UtilsGroup extends Grid implements PageGroup, Bindable, ClickHandler {
    private Button calcLink;
    private HandlerRegistration handlerRegistration;

    public UtilsGroup() {
        super(4, 1);
        setText(0, 0, "this is a utility...");
        calcLink = new Button("calculator");
        setWidget(1, 0, calcLink);
        setWidget(2, 0, new Hyperlink("clock", "utils.clock"));
    }

    @Override
    public void show(PageGroup parent, Map<String, String> params) {
        parent.setContent(this);
    }

    @Override
    public void hide(PageGroup parent) {
        parent.setContent(null);
    }

    @Override
    public void setContent(Widget content) {
        if (content == null) {
            clearCell(3, 0);
        } else {
            setWidget(3, 0, content);
        }
    }

    @Override
    public void bind() {
        handlerRegistration = calcLink.addClickHandler(this);
    }

    @Override
    public void unbind() {
        handlerRegistration.removeHandler();
    }

    @Override
    public void onClick(ClickEvent event) {
        History.newItem("utils.calculator");
    }
}
