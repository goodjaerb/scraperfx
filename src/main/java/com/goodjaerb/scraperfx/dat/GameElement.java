/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.dat;

import org.xmappr.annotation.Attribute;
import org.xmappr.annotation.RootElement;

import java.util.Objects;

/**
 * @author goodjaerb
 */
@RootElement(name = "game")
public class GameElement implements DatElement {

    @Attribute(name = "name")
    public String name;

    @Attribute(name = "romof")
    public String romof;

    public GameElement() {
        this("", "");
    }

    public GameElement(String name, String romof) {
        this.name = name;
        this.romof = romof;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRomof() {
        return romof;
    }

    @Override
    public String toString() {
        return "GameElement{" + "name=" + name + ", romof=" + romof + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final GameElement other = (GameElement) obj;
        return Objects.equals(this.name, other.name);
    }
}
