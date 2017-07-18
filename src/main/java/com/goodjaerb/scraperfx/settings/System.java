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
@RootElement
public class System implements Comparable<System> {
    
    @Attribute
    public String name;
    
    @Attribute
    public String scrapeAs;
    
    @Attribute
    public Boolean unmatchedOnly;
    
    @Attribute
    public Boolean scrapeAsArcade;
    
    @Element
    public String romsDir;
    
    @Element
    public String substringRegex;
    
    @Element
    public String ignoreRegex;
    
    public System() {
        this("");
    }
    
    public System(String name) {
        this.name = name;
        this.scrapeAsArcade = false;
        this.scrapeAs = "";
        this.romsDir = "";
        this.unmatchedOnly = false;
    }
    
    public System(System sys) {
        this.name = sys.name;
        this.scrapeAsArcade = sys.scrapeAsArcade;
        this.scrapeAs = sys.scrapeAs;
        this.romsDir = sys.romsDir;
        this.unmatchedOnly = sys.unmatchedOnly;
    }
    
    @Override
    public int compareTo(System o) {
        return name.compareTo(o.name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.name);
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
        final System other = (System) obj;
        if(!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
}
