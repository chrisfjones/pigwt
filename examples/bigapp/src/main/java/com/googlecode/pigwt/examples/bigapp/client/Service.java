package com.googlecode.pigwt.examples.bigapp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.pigwt.examples.bigapp.client.attributes.Attribute;

import java.util.Arrays;
import java.util.List;

public class Service {
    public String getRootMessage() {
        return "root";
    }

    public void getAttributes(AsyncCallback<List<Attribute>> callback) {
        callback.onSuccess(
                Arrays.asList(
                        new Attribute("Demographic Market Area"),
                        new Attribute("Subscription Status"),
                        new Attribute("Email"),
                        new Attribute("First Name"),
                        new Attribute("Last Name")
                ));
    }
}
