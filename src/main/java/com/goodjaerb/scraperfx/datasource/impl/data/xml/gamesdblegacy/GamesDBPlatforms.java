/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.data.xml.gamesdblegacy;

import java.util.List;
import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "Platforms")
public class GamesDBPlatforms {
    
    @Element(name = "Platform")
    public List<GamesDBPlatform> list;
    
    @Override
    public String toString() {
        return list.toString();
    }
}
