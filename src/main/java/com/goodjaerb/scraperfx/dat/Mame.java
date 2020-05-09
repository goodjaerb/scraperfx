package com.goodjaerb.scraperfx.dat;

import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

import java.util.ArrayList;
import java.util.Collection;

@RootElement(name = "mame")
public class Mame implements DataHolder {

    @Element(name = "game")
    public Collection<GameElement> games;

    public Mame() {
        games = new ArrayList<>();
    }

    public Collection<DatElement> getElements() {
        if(!games.isEmpty()) {
            return new ArrayList<>(games);
        }
        return null;
    }
}
