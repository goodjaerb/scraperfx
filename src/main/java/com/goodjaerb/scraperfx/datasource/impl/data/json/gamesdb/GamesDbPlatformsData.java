/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.data.json.gamesdb;

import com.google.gson.annotations.Expose;
import java.util.Map;

/**
 *
 * @author goodjaerb
 */
public class GamesDbPlatformsData extends GamesDbData<GamesDbPlatformsData.Platform> {
    @Expose(serialize = true, deserialize = true) public Map<String, Platform> platforms;
        
    public static class Platform {
        @Expose(serialize = true, deserialize = true) public int id;
        @Expose(serialize = true, deserialize = true) public String name;
        @Expose(serialize = true, deserialize = true) public String alias;
        @Expose(serialize = true, deserialize = true) public String icon;
        @Expose(serialize = true, deserialize = true) public String console;
        @Expose(serialize = true, deserialize = true) public String controller;
        @Expose(serialize = true, deserialize = true) public String developer;
        
        public Platform() {
            
        }

        @Override
        public String toString() {
            return "Platform{" + "id=" + id + ", name=" + name + ", alias=" + alias + ", icon=" + icon + ", console=" + console + ", controller=" + controller + ", developer=" + developer + '}';
        }
    }
    
    public GamesDbPlatformsData() {
    }

    @Override
    public String toString() {
        return "GamesDbPlatformsData{" + "count=" + count + ", platforms=" + platforms + '}';
    }
    
    @Override
    public boolean isDataAvailable() {
        return !(platforms == null || platforms.isEmpty());
    }
}
