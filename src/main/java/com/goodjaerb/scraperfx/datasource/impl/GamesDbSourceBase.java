/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import com.goodjaerb.scraperfx.ScraperFX;
import com.goodjaerb.scraperfx.datasource.CustomHttpDataSource;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbDevelopersData;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbGenresData;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbPlatformsData;
import com.goodjaerb.scraperfx.datasource.impl.gamesdb.GamesDbPublishersData;
import com.goodjaerb.scraperfx.datasource.plugin.JsonDataSourcePlugin;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.MetaData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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
    
    private static final GamesDbPlatformsData   CACHED_PLATFORMS_DATA = new GamesDbPlatformsData();
    private static final GamesDbGenresData      CACHED_GENRES_DATA = new GamesDbGenresData();
    private static final GamesDbDevelopersData  CACHED_DEVELOPERS_DATA = new GamesDbDevelopersData();
    private static final GamesDbPublishersData  CACHED_PUBLISHERS_DATA = new GamesDbPublishersData();
    
    abstract Map<String, String> getDefaultParams();
    
    @Override
    public String getSourceName() {
        return "TheGamesDB (thegamesdb.net)";
    }
    
    private Gson getGson() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }
    
    private <T> T getCachedData(Path cacheFilePath, Class<T> dataClass) throws IOException {
        if(!Files.exists(cacheFilePath)) {
            Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Creating local file ''{0}''...", cacheFilePath.toString());
            Files.createDirectories(cacheFilePath.getParent());
            Files.createFile(cacheFilePath);
            return null; // file was just created so no use in reading from it.
        }

        T localData;
        try(final BufferedReader reader = Files.newBufferedReader(cacheFilePath)) {
            localData = getGson().fromJson(reader, dataClass);
        }
        return localData;
    }
    
    private <T> void writeCachedData(Path cacheFilePath, T dataHolder, Class<T> dataClass) throws IOException {
        try(final BufferedWriter writer = Files.newBufferedWriter(cacheFilePath, StandardCharsets.UTF_8)) {
            getGson().toJson(dataHolder, dataClass, writer);
            writer.flush();
        }
    }
    
    private void populatePlatformsData() {
        if(CACHED_PLATFORMS_DATA.data == null || CACHED_PLATFORMS_DATA.data.platforms.isEmpty()) {
            Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Initializing local GamesDbPlatformsData...");
            try {
                final Path platformsFilePath = ScraperFX.LOCALDB_PATH.resolve(GAMESDB_LOCAL_DIR).resolve(PLATFORMS_FILE);
                GamesDbPlatformsData localData = getCachedData(platformsFilePath, GamesDbPlatformsData.class);
                
                if(localData == null || localData.data.platforms.isEmpty()) {
                    Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Retrieving Platforms data from remote source...");
                    
                    localData = getData(new JsonDataSourcePlugin<>(GamesDbPlatformsData.class), API_BASE_URL + API_GET_PLATFORMS_LIST, getDefaultParams());
                    
                    if(localData == null) {
                        Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.WARNING, "No data returned for Platforms.");
                    }
                    else {
                        Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, localData.toString());
                        Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "API requests remaining={0}", localData.remaining_monthly_allowance);
                        if(!localData.data.platforms.isEmpty()) {
                            Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Writing Platforms data to disk...");
                            writeCachedData(platformsFilePath, localData, GamesDbPlatformsData.class);
                            
                            CACHED_PLATFORMS_DATA.data = localData.data;
                        }
                    }
                }
                else {
                    CACHED_PLATFORMS_DATA.data = localData.data;
                }
                Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Local GamesDbPlatformsData cache initialized.");
            }
            catch (IOException ex) {
                Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public List<String> getSystemNames() {
        populatePlatformsData();
        
        if(CACHED_PLATFORMS_DATA.data != null && !CACHED_PLATFORMS_DATA.data.platforms.isEmpty()) {
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
