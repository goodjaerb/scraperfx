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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author goodjaerb
 */
public class GamesDbSource extends JsonDataSource {
    public static final String GAMESDB_LOCAL_DIR = "thegamesdb.net";
    public static final String PLATFORMS_FILE = "platforms.json";
    
    private static final String                 API_BASE_URL = "https://api.thegamesdb.net/";
    private static final String                 API_GET_PLATFORMS_LIST = "Platforms";
    private static final Map<String, String>    DEFAULT_PARAMS;
    
    static {
        final Map<String, String> initialParams = new HashMap<>();
        initialParams.put("apikey", ScraperFX.getKeysValue("GamesDb.Public"));
        
        DEFAULT_PARAMS = Collections.unmodifiableMap(initialParams);
    }
    
    private final GamesDbPlatformsData cachedPlatformsData = new GamesDbPlatformsData();

    @Override
    public String getSourceName() {
        return "TheGamesDB (thegamesdb.net)";
    }

    @Override
    public List<String> getSystemNames() {
        if(cachedPlatformsData.data == null || cachedPlatformsData.data.platforms.isEmpty()) {
            Logger.getLogger(GamesDbSource.class.getName()).log(Level.INFO, "Initializing local GamesDbPlatformsData...");
            
            final Path platformsFilePath = ScraperFX.LOCALDB_PATH.resolve(GAMESDB_LOCAL_DIR).resolve(PLATFORMS_FILE);
            if(!Files.exists(platformsFilePath)) {
                Logger.getLogger(GamesDbSource.class.getName()).log(Level.INFO, "Creating local file ''{0}''...", platformsFilePath.toString());
                try {
                    Files.createDirectories(platformsFilePath.getParent());
                    Files.createFile(platformsFilePath);
                } catch (IOException ex) {
                    Logger.getLogger(GamesDbSource.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            try {
                GamesDbPlatformsData localData;
                try(final BufferedReader reader = Files.newBufferedReader(platformsFilePath)) {
                    localData = gson.fromJson(reader, GamesDbPlatformsData.class);
                }
                
                
                if(localData == null || localData.data.platforms.isEmpty()) {
                    Logger.getLogger(GamesDbSource.class.getName()).log(Level.INFO, "Retrieving Platforms data from remote source...");
                    
                    localData = getJson(GamesDbPlatformsData.class, API_BASE_URL + API_GET_PLATFORMS_LIST, DEFAULT_PARAMS);
                    
                    Logger.getLogger(GamesDbSource.class.getName()).log(Level.INFO, localData.toString());
                    
                    Logger.getLogger(GamesDbSource.class.getName()).log(Level.INFO, "API requests remaining={0}", localData.remaining_monthly_allowance);
                    Logger.getLogger(GamesDbSource.class.getName()).log(Level.INFO, "Writing Platforms data to disk...");
                    try(final BufferedWriter writer = Files.newBufferedWriter(platformsFilePath, StandardCharsets.UTF_8)) {
                        gson.toJson(localData, GamesDbPlatformsData.class, writer);
                        writer.flush();
                    }
                }
                
                cachedPlatformsData.data = localData.data;
                Logger.getLogger(GamesDbSource.class.getName()).log(Level.INFO, "Local GamesDbPlatformsData cache initialized.");
            } catch (IOException ex) {
                Logger.getLogger(GamesDbSource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(cachedPlatformsData.data != null && !cachedPlatformsData.data.platforms.isEmpty()) {
            final List<String> systemNames = new ArrayList<>();
            cachedPlatformsData.data.platforms.values().forEach((platform) -> {
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
