/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import com.goodjaerb.scraperfx.ScraperFX;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbGamesByPlatformData;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbPaginatedResult;
import com.google.gson.reflect.TypeToken;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author goodjaerb <goodjaerb@gmail.com>
 */
public class GamesDbPrivateSource extends GamesDbSourceBase {
    private static final Map<String, String> DEFAULT_PARAMS;
    
    static {
        final Map<String, String> initialParams = new HashMap<>();
        initialParams.put("apikey", ScraperFX.getKeysValue("GamesDb.Private"));
        
        DEFAULT_PARAMS = Collections.unmodifiableMap(initialParams);
    }
    
    @Override
    public Map<String, String> getDefaultParams() {
        return DEFAULT_PARAMS;
    }
    
    public void populateGamesByPlatform(String platformId) {
        final Map<String, String> params = new HashMap<>(getDefaultParams());
        params.put("id", platformId);
        params.put("fields", "players,publishers,genres,overview,last_updated,rating,platform,coop,youtube,os,processor,ram,hdd,video,sound,alternates");
        
        if(CACHED_GAMES_BY_PLATFORM_DATA.get(platformId) == null) {
            CACHED_GAMES_BY_PLATFORM_DATA.put(platformId, new GamesDbPaginatedResult<>());
        }
        populatePaginatedCache(
                CACHED_GAMES_BY_PLATFORM_DATA.get(platformId), 
                new TypeToken<GamesDbPaginatedResult<GamesDbGamesByPlatformData>>(){}.getType(),
                GamesDbGamesByPlatformData.class, 
                ScraperFX.LOCALDB_PATH.resolve(GAMESDB_LOCAL_DIR).resolve(GAMES_BY_PLATFORM_DIR).resolve(platformId + ".json"), 
                API_BASE_URL + API_GAMES_BY_PLATFORM_ID, 
                params);
        
        System.out.println("FROM GamesDbPrivateSource!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(CACHED_GAMES_BY_PLATFORM_DATA.get(platformId));
        System.out.println("END FROM GamesDbPrivateSource!!!!!!!!!!!!!!!!!!");
    }
}
