/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import com.goodjaerb.scraperfx.ScraperFX;
import com.goodjaerb.scraperfx.datasource.JsonDataSource;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbPlatformsData;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.MetaData;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author goodjaerb
 */
public class GamesDbSource extends JsonDataSource {
    public static final String GAMESDB_LOCAL_DIR = "thegamesdb.net";
    
    private static final String API_BASE_URL = "https://api.thegamesdb.net/";
    private static final String API_GET_PLATFORMS_LIST = "Platforms";
    private static final String API_KEY = "?apikey=#APIKEY";

    @Override
    public String getSourceName() {
        return "TheGamesDB (thegamesdb.net)";
    }

    @Override
    public List<String> getSystemNames() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getSystemGameNames(String systemName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MetaData getMetaData(String systemName, Game game) {
        String url = API_BASE_URL + API_GET_PLATFORMS_LIST + API_KEY;
        url = url.replace("#APIKEY", ScraperFX.getKeysValue("GamesDb.Public"));
        
        try {
            final GamesDbPlatformsData data = getJson(GamesDbPlatformsData.class, url);
            System.out.println(data);
            
            //just testing, don't return anything.
        }
        catch (IOException ex) {
            Logger.getLogger(GamesDbSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
