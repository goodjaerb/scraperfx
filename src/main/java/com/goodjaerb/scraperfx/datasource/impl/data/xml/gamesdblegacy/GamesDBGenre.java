/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.data.xml.gamesdblegacy;

import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "Genres")
public class GamesDBGenre {
    
    @Element(name = "genre")
    public String genre;
    
    public GamesDBGenre() {
        
    }
}
