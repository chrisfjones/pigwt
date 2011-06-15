package com.googlecode.pigwt.examples.tutorial_gin.client.characters;

import com.google.gwt.user.client.ui.*;
import com.googlecode.pigwt.client.Shell;

public class CharacterListShell extends DockPanel implements Shell {
    private SimplePanel slot = new SimplePanel();

    public CharacterListShell() {
        this.setBorderWidth(1);
        VerticalPanel characters = new VerticalPanel();
        characters.add(new Hyperlink("Winnie the Pooh", "characters.detail:name=Pooh&age=5"));
        characters.add(new Hyperlink("Piglet", "characters.detail:name=Piglet&age=2"));
        characters.add(new Hyperlink("Christopher Robin", "characters.detail:name=Christopher&age=6"));
        add(characters, DockPanel.WEST);
        add(slot, DockPanel.CENTER);
    }

    @Override
    public void setWidget(final IsWidget w) {
        slot.setWidget(w);
    }
}
