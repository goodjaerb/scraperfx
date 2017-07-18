/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.settings;

import java.util.ArrayList;
import java.util.List;
import org.xmappr.annotation.Attribute;
import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "gamelist")
public class GameList {
    
    @Attribute(name = "system")
    public String system;
    
    @Element(name = "game")
    public List<Game> games;
    
    public GameList() {
        games = new ArrayList();
    }
    
    public GameList(String system) {
        this();
        this.system = system;
    }
}
