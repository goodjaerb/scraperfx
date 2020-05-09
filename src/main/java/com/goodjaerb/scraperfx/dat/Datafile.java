/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.dat;

import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author goodjaerb
 */
@RootElement(name = "datafile")
public class Datafile {

    @Element(name = "mame")
    public Collection<GameElement> mame;

    @Element(name = "game")
    public Collection<GameElement> games;

    @Element(name = "machine")
    public Collection<MachineElement> machines;

    public Datafile() {
        mame = new ArrayList<>();
        games = new ArrayList<>();
        machines = new ArrayList<>();
    }

    public Collection<DatElement> getElements() {
        if(!mame.isEmpty()) {
            return new ArrayList<>(mame);
        }
        if(!games.isEmpty()) {
            return new ArrayList<>(games);
        }
        if(!machines.isEmpty()) {
            return new ArrayList<>(machines);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Datafile{" + "mame=" + mame + ", games=" + games + ", machines=" + machines + '}';
    }
}
