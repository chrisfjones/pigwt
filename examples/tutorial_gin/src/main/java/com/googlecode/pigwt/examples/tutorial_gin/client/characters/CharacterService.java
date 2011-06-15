package com.googlecode.pigwt.examples.tutorial_gin.client.characters;

import javax.inject.Singleton;

@Singleton
public class CharacterService {
    public String getFactoid(String name) {
        if ("Pooh".equals(name)) {
            return "born on December 24, 1925.";
        }
        if ("Piglet".equals(name)) {
            return "10 inches tall.";
        }
        if ("Christopher".equals(name)) {
            return "master of the 100 Acre Woods";
        }
        return null;
    }
}
