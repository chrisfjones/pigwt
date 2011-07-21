package com.googlecode.pigwt.examples.bigapp.client.attributes;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.pigwt.examples.bigapp.client.Service;
import com.googlecode.pigwt.examples.bigapp.client.model.Attribute;

import javax.inject.Inject;
import java.util.List;

public class AttributesActivity extends AbstractActivity implements AsyncCallback<List<Attribute>> {
    private Service service;
    private final View view;
    private Integer maxRows = 10;

    public static interface View extends IsWidget {
        void displayAttributes(List<Attribute> result, int maxRows);
    }

    @Inject
    public AttributesActivity(
            final Service service,
            final View view) {
        this.service = service;
        this.view = view;
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        panel.setWidget(view);
        service.getAttributes(this);
    }

    public void setMaxRows(final Integer maxRows) {
        this.maxRows = (maxRows == null) ? 10 : maxRows;
    }

    @Override
    public void onFailure(final Throwable caught) {
    }

    @Override
    public void onSuccess(final List<Attribute> result) {
        view.displayAttributes(result, maxRows);
    }
}
