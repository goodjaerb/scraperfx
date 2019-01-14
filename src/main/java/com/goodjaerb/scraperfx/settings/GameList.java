/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.settings;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author goodjaerb
 */
public class GameList {
    
    public String       system;
    public List<Game>   games;
    
    public GameList() {
        games = new ArrayList<>();
    }
    
    public GameList(String system) {
        this();
        this.system = system;
    }
}
