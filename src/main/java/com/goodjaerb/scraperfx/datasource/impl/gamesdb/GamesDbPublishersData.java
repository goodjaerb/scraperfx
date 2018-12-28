/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.gamesdb;

import com.google.gson.annotations.Expose;
import java.util.Map;

/**
 *
 * @author goodjaerb <goodjaerb@gmail.com>
 */
public class GamesDbPublishersData extends GamesDbData<GamesDbPublishersData.Publisher> {
    @Expose(serialize = true, deserialize = true)   public Map<String, Publisher> publishers;
        
    public static class Publisher {
        @Expose(serialize = true, deserialize = true) public int id;
        @Expose(serialize = true, deserialize = true) public String name;
        
        public Publisher() {
            
        }

        @Override
        public String toString() {
            return "Genre{" + "id=" + id + ", name=" + name + '}';
        }
    }
    
    public GamesDbPublishersData() {
        
    }

    @Override
    public String toString() {
        return "GamesDbGenresData{" + "count=" + count + ", publishers=" + publishers + '}';
    }
    
    @Override
    public boolean isDataAvailable() {
        return !(publishers == null || publishers.isEmpty());
    }
    
//    @Override
//    public Collection<Publisher> values() {
//        return Collections.unmodifiableCollection(publishers.values());
//    }
}
