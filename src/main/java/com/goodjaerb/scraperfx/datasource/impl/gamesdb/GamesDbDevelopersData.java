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
public class GamesDbDevelopersData {
    public static class Data {
        @Expose(serialize = false, deserialize = true)  public int count;
        @Expose(serialize = true, deserialize = true)   public Map<String, Developer> developers;
        
        public Data() {
            
        }

        @Override
        public String toString() {
            return "Data{" + "count=" + count + ", genres=" + developers + '}';
        }
    }
    
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
    
    @Expose(serialize = false, deserialize = true)  public int code;
    @Expose(serialize = false, deserialize = true)  public String status;
    @Expose(serialize = true, deserialize = true)   public Data data;
    @Expose(serialize = false, deserialize = true)  public int remaining_monthly_allowance;
    @Expose(serialize = false, deserialize = true)  public int extra_allowance;
    
    public GamesDbDevelopersData() {
        
    }

    @Override
    public String toString() {
        return "GamesDbGenresData{" + "code=" + code + ", status=" + status + ", data=" + data + ", remaining_monthly_allowance=" + remaining_monthly_allowance + ", extra_allowance=" + extra_allowance + '}';
    }
}
