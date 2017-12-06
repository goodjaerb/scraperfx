/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.settings;

import java.util.Objects;
import org.xmappr.annotation.Attribute;
import org.xmappr.annotation.RootElement;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "image")
public class Image {
    public enum ImageType {
        LOGO("logo", false), BOX_FRONT("box-front", false), BOX_BACK("box-back", false), SCREENSHOT("screenshot", false), FANART("fanart", false),
        GAME("game", true), DECAL("decal", true), TITLE("title", true), FLYER("flyer", true), MARQUEE("marquee", true);
        
        private final String name;
        private final boolean arcadeImage;
        
        ImageType(String s, boolean b) {
            this.name = s;
            this.arcadeImage = b;
        }
        
        public String getName() {
            return name;
        }
        
        public boolean isArcadeImage() {
            return arcadeImage;
        }
    }
    
    @Attribute(name = "type")
    public String type;
    
    @Attribute(name = "width")
    public Integer width;
    
    @Attribute(name = "height")
    public Integer height;
    
    @Attribute(name = "path")
    public String path;
    
    @Attribute(name = "selected")
    public Boolean selected;
    
    public Image() {
        selected = false;
    }
    
    public Image(String type, String path, boolean b) {
        this.type = type;
        this.path = path;
        this.selected = b;
    }
    
    public Image(String type, Integer width, Integer height, String path, boolean b) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.path = path;
        this.selected = b;
    }
    
    @Override
    public String toString() {
        return "(type=" + type + ", width=" + width + ", height=" + height + ", path=" + path + ")";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + Objects.hashCode(this.path);
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
        final Image other = (Image) obj;
        if(!Objects.equals(this.type, other.type)) {
            return false;
        }
        if(!Objects.equals(this.path, other.path)) {
            return false;
        }
        return true;
    }
}
