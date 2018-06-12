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
        GAME("game", true), /*DECAL("decal", true),*/ TITLE("title", true), FLYER("flyer", true), MARQUEE("marquee", true);
        
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
    
//    @Attribute(name = "path")
//    public String path;
    
    @Attribute(name = "url")
    public String url;
    
    @Attribute(name = "target-imagetype")
    public String targetImageType;
    
    @Attribute(name = "selected")
    public Boolean selected;
    
    public Image() {
        selected = false;
    }
    
    public Image(String type, String url, String targetImageType, boolean b) {
        this.type = type;
        this.url = url;
        this.targetImageType = targetImageType;
//        this.path = path;
        this.selected = b;
    }
    
    public Image(String type, Integer width, Integer height, String url, boolean b) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.url = url;
//        this.path = path;
        this.selected = b;
    }

    @Override
    public String toString() {
        return "Image{" + "type=" + type + ", width=" + width + ", height=" + height + ", url=" + url + ", targetImageType=" + targetImageType + ", selected=" + selected + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.type);
        hash = 11 * hash + Objects.hashCode(this.url);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Image other = (Image) obj;
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        return true;
    }
    
}
