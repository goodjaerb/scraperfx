/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.settings;

import java.util.ArrayList;
import java.util.List;
import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "gamedata")
public class GameData {
    
    @Element(name = "gamelist")
    public List<GameList> gamelist;
    
    public GameData() {
        gamelist = new ArrayList<>();
    }
    
    public void setTo(GameData other) {
        this.gamelist.clear();
        this.gamelist.addAll(other.gamelist);
    }
    
    public List<Game> getSystemData(String systemName) {
        for(final GameList list : gamelist) {
            if(systemName.equals(list.system)) {
                return list.games;
            }
        }
        return null;
    }
    
    public void remove(String system) {
        for(int i = 0; i < gamelist.size(); i++) {
            if(system.equals(gamelist.get(i).system)) {
                gamelist.remove(i);
                return;
            }
        }
    }
}
