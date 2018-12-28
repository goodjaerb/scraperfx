/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import com.goodjaerb.scraperfx.ScraperFX;
import com.goodjaerb.scraperfx.datasource.CustomHttpDataSource;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbData;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbDevelopersData;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbGenresData;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbPaginatedData;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbPaginatedResult;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbPlatformsData;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbPublishersData;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbResult;
import com.goodjaerb.scraperfx.datasource.plugin.JsonDataSourcePlugin;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.MetaData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author goodjaerb
 */
public abstract class GamesDbSourceBase extends CustomHttpDataSource {
    private static final String GAMESDB_LOCAL_DIR = "thegamesdb.net";
    private static final String PLATFORMS_FILE = "platforms.json";
    private static final String GENRES_FILE = "genres.json";
    private static final String DEVELOPERS_FILE = "developers.json";
    private static final String PUBLISHERS_FILE = "publishers.json";
    
    private static final String API_BASE_URL = "https://api.thegamesdb.net/";
    private static final String API_GET_PLATFORMS_LIST = "Platforms";
    private static final String API_GET_GENRES_LIST = "Genres";
    private static final String API_GET_DEVELOPERS_LIST = "Developers";
    private static final String API_GET_PUBLISHERS_LIST = "Publishers";
    
    private static final GamesDbResult<GamesDbPlatformsData>    CACHED_PLATFORMS_DATA = new GamesDbResult<>();
    private static final GamesDbResult<GamesDbGenresData>       CACHED_GENRES_DATA = new GamesDbResult<>();
    private static final GamesDbResult<GamesDbDevelopersData>   CACHED_DEVELOPERS_DATA = new GamesDbResult<>();
    private static final GamesDbResult<GamesDbPublishersData>   CACHED_PUBLISHERS_DATA = new GamesDbResult<>();
    
    abstract Map<String, String> getDefaultParams();
    
    @Override
    public String getSourceName() {
        return "TheGamesDB (thegamesdb.net)";
    }
    
    private Gson getGson() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }
    
    private <T> T getCachedData(Path cacheFilePath, Type typeOfT) throws IOException {
        if(!Files.exists(cacheFilePath)) {
            Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Creating local file ''{0}''...", cacheFilePath.toString());
            Files.createDirectories(cacheFilePath.getParent());
            Files.createFile(cacheFilePath);
            return null; // file was just created so no use in reading from it.
        }

        T localData;
        try(final BufferedReader reader = Files.newBufferedReader(cacheFilePath)) {
            localData = getGson().fromJson(reader, typeOfT);
        }
        return localData;
    }
    
    private <T> void writeCachedData(Path cacheFilePath, T dataHolder) throws IOException {
        try(final BufferedWriter writer = Files.newBufferedWriter(cacheFilePath, StandardCharsets.UTF_8)) {
            getGson().toJson(dataHolder, writer);
            writer.flush();
        }
    }
    
    /**
     * Class<D> dataClass required to alleviate unchecked cast warning.
     * 
     * @param <D>
     * @param <T>
     * @param cache
     * @param typeOfT
     * @param dataClass
     * @param cachePath
     * @param url
     * @param params 
     */
    private <D extends GamesDbData<?>, T extends GamesDbResult<D>> void populateCache(T cache, Type typeOfT, Class<D> dataClass, Path cachePath, String url, Map<String, String> params) {
        if(!cache.isDataAvailable()) {
            Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Initializing local {0} cache...", dataClass.getName());
            try {
                T localData = getCachedData(cachePath, typeOfT);
                
                if(localData == null || !localData.isDataAvailable()) {
                    Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Retrieving data from remote source...");
                    
                    localData = getData(new JsonDataSourcePlugin<>(typeOfT), url, params);
                    
                    if(localData == null) {
                        Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.WARNING, "No data returned for {0}.", dataClass.getName());
                    }
                    else {
                        Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "API requests remaining: monthly={0}, extra_allowance={1}.", new Object[]{localData.remaining_monthly_allowance, localData.extra_allowance});
                        if(localData.isDataAvailable()) {
                            Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Writing {0} data to disk...", dataClass.getName());
                            writeCachedData(cachePath, localData);
                            
                            cache.data = localData.data;
                        }
                    }
                }
                else {
                    cache.data = localData.data;
                }
                Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Local {0} cache initialized.", dataClass.getName());
            }
            catch (IOException ex) {
                Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private <E, D extends GamesDbPaginatedData<E>, T extends GamesDbPaginatedResult<D>> void populatePaginatedCache(T cache, Type typeOfT, Class<D> dataClass, Path cachePath, String url, Map<String, String> params) {
        if(!cache.isDataAvailable()) {
            Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Initializing local {0} cache...", dataClass.getName());
            try {
                T localData = getCachedData(cachePath, typeOfT);
                
                if(localData == null || !localData.isDataAvailable()) {
                    Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Retrieving data from remote source...");
                    
                    localData = getData(new JsonDataSourcePlugin<>(typeOfT), url, params);
                    
                    if(localData == null) {
                        Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.WARNING, "No data returned for {0}.", dataClass.getName());
                    }
                    else {
                        Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "API requests remaining: monthly={0}, extra_allowance={1}.", new Object[]{localData.remaining_monthly_allowance, localData.extra_allowance});

                        if(localData.isDataAvailable()) {
                            cache.data = localData.data;

                            while(localData.hasNext()) {
                                localData = getData(new JsonDataSourcePlugin<>(typeOfT), localData.pages.next);

                                if(localData == null) {
                                    //if it's still null something went wrong.
                                    break;
                                }
                                
                                Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "API requests remaining: monthly={0}, extra_allowance={1}.", new Object[]{localData.remaining_monthly_allowance, localData.extra_allowance});
                                if(localData.isDataAvailable()) {
                                    cache.data.appendData(localData.data.values());
                                }
                            }

                            Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Writing {0} data to disk...", dataClass.getName());
                            writeCachedData(cachePath, cache);
                        }
                    }
                }
                else {
                    cache.data = localData.data;
                }
                Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Local {0} cache initialized.", dataClass.getName());
            }
            catch (IOException ex) {
                Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void populatePlatformsData() {
        populateCache(
                CACHED_PLATFORMS_DATA, 
                new TypeToken<GamesDbResult<GamesDbPlatformsData>>(){}.getType(), 
                GamesDbPlatformsData.class,
                ScraperFX.LOCALDB_PATH.resolve(GAMESDB_LOCAL_DIR).resolve(PLATFORMS_FILE), 
                API_BASE_URL + API_GET_PLATFORMS_LIST, 
                getDefaultParams());
    }
    
    private void populateGenresData() {
        populateCache(
                CACHED_GENRES_DATA,
                new TypeToken<GamesDbResult<GamesDbGenresData>>(){}.getType(),
                GamesDbGenresData.class,
                ScraperFX.LOCALDB_PATH.resolve(GAMESDB_LOCAL_DIR).resolve(GENRES_FILE),
                API_BASE_URL + API_GET_GENRES_LIST,
                getDefaultParams());
    }
    
    private void populateDevelopersData() {
        populateCache(
                CACHED_DEVELOPERS_DATA,
                new TypeToken<GamesDbResult<GamesDbDevelopersData>>(){}.getType(),
                GamesDbDevelopersData.class,
                ScraperFX.LOCALDB_PATH.resolve(GAMESDB_LOCAL_DIR).resolve(DEVELOPERS_FILE),
                API_BASE_URL + API_GET_DEVELOPERS_LIST,
                getDefaultParams());
    }
    
    private void populatePublishersData() {
        populateCache(
                CACHED_PUBLISHERS_DATA,
                new TypeToken<GamesDbResult<GamesDbPublishersData>>(){}.getType(),
                GamesDbPublishersData.class,
                ScraperFX.LOCALDB_PATH.resolve(GAMESDB_LOCAL_DIR).resolve(PUBLISHERS_FILE),
                API_BASE_URL + API_GET_PUBLISHERS_LIST,
                getDefaultParams());
    }
    
    @Override
    public List<String> getSystemNames() {
        populatePlatformsData();
        
        if(CACHED_PLATFORMS_DATA.isDataAvailable()) {
            final List<String> systemNames = new ArrayList<>();
            CACHED_PLATFORMS_DATA.data.platforms.values().forEach((platform) -> {
                systemNames.add(platform.name);
            });
            return systemNames;
        }
        return null;
    }

    @Override
    public List<String> getSystemGameNames(String systemName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MetaData getMetaData(String systemName, Game game) {
        return null;
    }
}
