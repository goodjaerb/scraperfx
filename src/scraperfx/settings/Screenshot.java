/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraperfx.settings;

import org.xmappr.annotation.Attribute;
import org.xmappr.annotation.RootElement;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "screenshot")
public class Screenshot {
    
    @Attribute(name = "width")
    public Integer width;
    
    @Attribute(name = "height")
    public Integer height;
    
    @Attribute(name = "path")
    public String path;
    
    public Screenshot() {
        
    }
    
    public Screenshot(Integer width, Integer height, String path) {
        this.width = width;
        this.height = height;
        this.path = path;
    }
    
    @Override
    public String toString() {
        return "Screenshot(width=" + width + ", height=" + height + ", path=" + path + ")";
    }
}
