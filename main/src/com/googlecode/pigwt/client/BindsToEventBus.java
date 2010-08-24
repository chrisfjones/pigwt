package com.googlecode.pigwt.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.Stack;

public abstract class BindsToEventBus implements Bindable {
    protected final HandlerManager eventBus;
    private final Stack<HandlerRegistration> handlerRegistrations;
    private final Stack<BindsToEventBus> boundRegistrations;

    public BindsToEventBus(final HandlerManager eventBus) {
        this.eventBus = eventBus;
        handlerRegistrations = new Stack<HandlerRegistration>();
        boundRegistrations = new Stack<BindsToEventBus>();
    }

    public final void unbind() {
        while (!handlerRegistrations.isEmpty()) {
            handlerRegistrations.pop().removeHandler();
        }
        while (!boundRegistrations.isEmpty()) {
            boundRegistrations.pop().unbind();
        }
        doUnbind();
    }

    protected void doUnbind() {
    }

    protected <H extends EventHandler> void register(GwtEvent.Type<H> type, H handler) {
        handlerRegistrations.add(eventBus.addHandler(type, handler));
    }

    protected void register(HandlerRegistration handlerRegistration) {
        handlerRegistrations.push(handlerRegistration);
    }

    protected void register(BindsToEventBus bound) {
        if (bound == null) {
            return;
        }
        boundRegistrations.push(bound);
        bound.bind();
    }

    protected void fireEvent(GwtEvent<?> event) {
        eventBus.fireEvent(event);
    }
}
