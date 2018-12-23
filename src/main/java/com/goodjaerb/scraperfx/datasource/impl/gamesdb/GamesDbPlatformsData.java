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
 * @author goodjaerb
 */
public class GamesDbPlatformsData {
    public static class Data {
        @Expose(serialize = false, deserialize = true)  public int count;
        @Expose(serialize = true, deserialize = true)   public Map<String, Platform> platforms;
        
        public Data() {
            
        }

        @Override
        public String toString() {
            return "Data{" + "count=" + count + ", platforms=" + platforms + '}';
        }
    }
    
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
    
    @Expose(serialize = false, deserialize = true)  public int code;
    @Expose(serialize = false, deserialize = true)  public String status;
    @Expose(serialize = true, deserialize = true)   public Data data;
    @Expose(serialize = false, deserialize = true)  public int remaining_monthly_allowance;
    @Expose(serialize = false, deserialize = true)  public int extra_allowance;
    
    public GamesDbPlatformsData() {
        
    }

    @Override
    public String toString() {
        return "GamesDbPlatformsData{" + "code=" + code + ", status=" + status + ", data=" + data + ", remaining_monthly_allowance=" + remaining_monthly_allowance + ", extra_allowance=" + extra_allowance + '}';
    }
}
