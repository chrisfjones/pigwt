package com.googlecode.pigwt.examples.tutorial_gin.client.characters.detail;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.pigwt.examples.tutorial_gin.client.characters.CharacterService;

import javax.inject.Inject;

public class CharacterDetailActivity extends AbstractActivity {
    private String name;
    private Integer age;
    private CharacterService service;

    @Inject
    public CharacterDetailActivity(CharacterService service) {
        this.service = service;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(new Label("This is a detail of " + name + " who is " + age + " years old and " + service.getFactoid(name)));
    }
}
