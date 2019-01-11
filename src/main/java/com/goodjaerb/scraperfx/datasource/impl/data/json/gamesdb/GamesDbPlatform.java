/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.data.json.gamesdb;

/**
 * This class is more appropriate for use in drop down comboboxes rather than
 * the static inner class from GamesDbPlatformsData.
 * 
 * @author goodjaerb <goodjaerb@gmail.com>
 */
public class GamesDbPlatform {
    public int id;
    public String name;
    public String alias;
    public String icon;
    public String console;
    public String controller;
    public String developer;

    public GamesDbPlatform(GamesDbPlatformsData.Platform platform) {
        this.id = platform.id;
        this.name = platform.name;
        this.alias = platform.alias;
        this.icon = platform.icon;
        this.console = platform.console;
        this.controller = platform.controller;
        this.developer = platform.developer;
    }

    @Override
    public String toString() {
        return name;
    }
}
