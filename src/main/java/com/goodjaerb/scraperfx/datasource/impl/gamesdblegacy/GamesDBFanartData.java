/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.gamesdblegacy;

import org.xmappr.annotation.Attribute;
import org.xmappr.annotation.RootElement;
import org.xmappr.annotation.Text;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "original")
public class GamesDBFanartData {
    
    @Attribute(name = "width")
    public Integer width;
    
    @Attribute(name = "height")
    public Integer height;
    
    @Text
    public String path;
    
    public GamesDBFanartData() {
        
    }
}