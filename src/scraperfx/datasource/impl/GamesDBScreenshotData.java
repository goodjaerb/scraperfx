/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraperfx.datasource.impl;

import org.xmappr.annotation.Attribute;
import org.xmappr.annotation.RootElement;
import org.xmappr.annotation.Text;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "original")
public class GamesDBScreenshotData {
    
    @Attribute(name = "width")
    public Integer width;
    
    @Attribute(name = "height")
    public Integer height;
    
    @Text
    public String path;
    
    public GamesDBScreenshotData() {
        
    }
}
