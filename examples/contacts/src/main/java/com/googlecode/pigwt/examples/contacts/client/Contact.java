package com.googlecode.pigwt.examples.contacts.client;

public class Contact {
    String first;
    String last;

    public Contact(String first, String last) {
        this.first = first;
        this.last = last;
    }

    @Override
    public String toString() {
        return first + " " + last;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }
}
