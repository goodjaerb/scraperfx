/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.gamesdb.data;

import com.google.gson.annotations.Expose;

/**
 * @author goodjaerb <goodjaerb@gmail.com>
 */
public class GamesDbPlatform {
    @Expose(serialize = true, deserialize = true)
    public int    id;
    @Expose(serialize = true, deserialize = true)
    public String name;
    @Expose(serialize = true, deserialize = true)
    public String alias;
    @Expose(serialize = true, deserialize = true)
    public String icon;
    @Expose(serialize = true, deserialize = true)
    public String console;
    @Expose(serialize = true, deserialize = true)
    public String controller;
    @Expose(serialize = true, deserialize = true)
    public String developer;

    public GamesDbPlatform() {

    }

    @Override
    public String toString() {
        return name;
    }
}
