/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.data.xml.gamesdblegacy;

import org.xmappr.annotation.Attribute;
import org.xmappr.annotation.RootElement;
import org.xmappr.annotation.Text;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "boxart")
public class GamesDBBoxArt {
    
    @Attribute(name = "side")
    public String side;
    
    @Attribute(name = "width")
    public Integer width;
    
    @Attribute(name = "height")
    public Integer height;
    
    @Text
    public String path;
    
    public GamesDBBoxArt() {
        
    }
}
