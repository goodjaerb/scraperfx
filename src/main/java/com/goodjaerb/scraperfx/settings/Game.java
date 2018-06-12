/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.settings;

import java.util.Objects;
import org.xmappr.annotation.Attribute;
import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "game")
public class Game implements Comparable<Game> {

    public enum MatchStrength {
        LOCKED("lightblue"),
        STRONG("green"),
        BEST_GUESS("lightgreen"),
        TIE_BREAKER("yellow"),
        LOW_PERCENTAGE("orange"),
        NO_MATCH("red"),
        IGNORE("black");
    
        public final String cssBackground;
        
        private MatchStrength(String cssBackground) {
            this.cssBackground = cssBackground;
        }
    };
    
    @Attribute(name = "matchedname")
    public String matchedName;
    
    @Attribute(name = "strength")
    public MatchStrength strength;
    
    @Attribute(name = "filename")
    public String fileName;
    
    @Element(name = "metadata")
    public MetaData metadata;
    
    public Game() {
        this("");
    }
    
    public Game(String fileName) {
        this.fileName = fileName;
        this.strength = MatchStrength.NO_MATCH;
    }
    
    public Game(Game g) {
        this(g.fileName);
        this.strength = g.strength;
        this.matchedName = g.matchedName;
        this.metadata = g.metadata;
    }
    
    public String getImageUrlByType(String type) {
        if(metadata != null && metadata.images != null) {
            for(Image img : metadata.images) {
                if(img.type.equals(type)) {
                    return img.url;
                }
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.fileName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final Game other = (Game) obj;
        return Objects.equals(this.fileName, other.fileName);
    }
    
    @Override
    public int compareTo(Game o) {
        return fileName.toLowerCase().compareTo(o.fileName.toLowerCase());
    }
    
    @Override
    public String toString() {
        return fileName;
    }
    
    public String toLongString() {
        return "Game(filename=" + fileName + ", matchedName=" + matchedName + ", strength=" + strength + ", metadata=" + metadata + ")";
    }
}
