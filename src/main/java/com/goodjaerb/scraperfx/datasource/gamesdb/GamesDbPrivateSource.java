/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.gamesdb;

import com.goodjaerb.scraperfx.ScraperFX;
import com.goodjaerb.scraperfx.datasource.gamesdb.data.GamesDbGamesByPlatformData;
import com.goodjaerb.scraperfx.datasource.gamesdb.data.GamesDbImagesData;
import com.goodjaerb.scraperfx.datasource.gamesdb.data.GamesDbPaginatedResult;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.MetaData;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjaerb <goodjaerb@gmail.com>
 */
public class GamesDbPrivateSource extends GamesDbSourceBase {
    private static final Map<String, String> DEFAULT_PARAMS;

    static {
        DEFAULT_PARAMS = Map.of("apikey", ScraperFX.getKeysValue("GamesDb.Private"));
    }

    @Override
    public Map<String, String> getDefaultParams() {
        return DEFAULT_PARAMS;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                new TypeToken<GamesDbPaginatedResult<GamesDbGamesByPlatformData>>() {
                }.getType(),
                GamesDbGamesByPlatformData.class,
                ScraperFX.LOCALDB_PATH.resolve(GAMESDB_LOCAL_DIR).resolve(GAMES_BY_PLATFORM_DIR).resolve(platformId + ".json"),
                API_BASE_URL + API_GAMES_BY_PLATFORM_ID,
                params);

        populateGamesImages(platformId);
    }

    private void populateGamesImages(String platformId) {
        loadGamesByPlatformCache(platformId);

        final GamesDbPaginatedResult<GamesDbGamesByPlatformData> cachedGameData = CACHED_GAMES_BY_PLATFORM_DATA.get(platformId);
        if(cachedGameData != null && cachedGameData.isDataAvailable()) {
            if(CACHED_GAMES_IMAGES_DATA.get(platformId) == null) {
                CACHED_GAMES_IMAGES_DATA.put(platformId, new GamesDbPaginatedResult<>());
            }

            final List<String> gameIds = getGameIdsForPlatform(platformId);
            String idParam = "";
            idParam = gameIds.stream().map((gameId) -> gameId + ",").reduce(idParam, String::concat);

            final Map<String, String> params = new HashMap<>(getDefaultParams());
            params.put("games_id", idParam);

            populatePaginatedCache(
                    CACHED_GAMES_IMAGES_DATA.get(platformId),
                    new TypeToken<GamesDbPaginatedResult<GamesDbImagesData>>() {
                    }.getType(),
                    GamesDbImagesData.class,
                    ScraperFX.LOCALDB_PATH.resolve(GAMESDB_LOCAL_DIR).resolve(IMAGES_DIR).resolve(platformId + "_images.json"),
                    API_BASE_URL + API_GAMES_IMAGES,
                    params);
        }
    }
}
