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
 * @author goodjaerb <goodjaerb@gmail.com>
 */
public class GamesDbDevelopersData extends GamesDbData<GamesDbDevelopersData.Developer> {
    @Expose(serialize = true, deserialize = true)   public Map<String, Developer> developers;
    
    public static class Developer {
        @Expose(serialize = true, deserialize = true) public int id;
        @Expose(serialize = true, deserialize = true) public String name;
        
        public Developer() {
            
        }

        @Override
        public String toString() {
            return "Genre{" + "id=" + id + ", name=" + name + '}';
        }
    }
    
    public GamesDbDevelopersData() {
        
    }

    @Override
    public String toString() {
        return "GamesDbGenresData{" + "count=" + count + ", developers=" + developers + '}';
    }
    
    @Override
    public boolean isDataAvailable() {
        return !(developers == null || developers.isEmpty());
    }
}
