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
public class GamesDbGenresData extends GamesDbData<GamesDbGenresData.Genre> {
    @Expose(serialize = true, deserialize = true)   public Map<String, Genre> genres;
    
    public static class Genre {
        @Expose(serialize = true, deserialize = true) public int id;
        @Expose(serialize = true, deserialize = true) public String name;
        
        public Genre() {
            
        }

        @Override
        public String toString() {
            return "Genre{" + "id=" + id + ", name=" + name + '}';
        }
    }
    
    public GamesDbGenresData() {
        
    }

    @Override
    public String toString() {
        return "GamesDbGenresData{" + "count=" + count + ", genres=" + genres + '}';
    }
    
    @Override
    public boolean isDataAvailable() {
        return !(genres == null || genres.isEmpty());
    }
    
//    @Override
//    public Collection<Genre> values() {
//        return Collections.unmodifiableCollection(genres.values());
//    }
}
