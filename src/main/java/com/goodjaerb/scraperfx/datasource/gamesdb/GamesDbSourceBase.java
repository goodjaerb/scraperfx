/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.gamesdb;

import com.goodjaerb.scraperfx.ScraperFX;
import com.goodjaerb.scraperfx.datasource.CustomHttpDataSource;
import com.goodjaerb.scraperfx.datasource.gamesdb.data.*;
import com.goodjaerb.scraperfx.datasource.plugin.JsonDataSourcePlugin;
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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author goodjaerb
 */
public abstract class GamesDbSourceBase extends CustomHttpDataSource {
    static final String SOURCE_NAME = "TheGamesDB (thegamesdb.net)";

    static final           String GAMESDB_LOCAL_DIR     = "thegamesdb.net";
    static final String GAMES_BY_PLATFORM_DIR = "GamesByPlatformId";
    static final String IMAGES_DIR            = "GamesImages";
    static final String PLATFORMS_FILE        = "platforms.json";
    static final String GENRES_FILE           = "genres.json";
    static final String DEVELOPERS_FILE       = "developers.json";
    static final String PUBLISHERS_FILE       = "publishers.json";

    static final String API_BASE_URL             = "https://api.thegamesdb.net/v1/";
    static final String API_GET_PLATFORMS_LIST   = "Platforms";
    static final String API_GET_GENRES_LIST      = "Genres";
    static final String API_GET_DEVELOPERS_LIST  = "Developers";
    static final String API_GET_PUBLISHERS_LIST  = "Publishers";
    static final String API_GAMES_BY_PLATFORM_ID = "Games/ByPlatformID";
    static final String API_GAMES_IMAGES         = "Games/Images";

    static final GamesDbResult<GamesDbPlatformsData>  CACHED_PLATFORMS_DATA  = new GamesDbResult<>();
    static final GamesDbResult<GamesDbGenresData>     CACHED_GENRES_DATA     = new GamesDbResult<>();
    static final GamesDbResult<GamesDbDevelopersData> CACHED_DEVELOPERS_DATA = new GamesDbResult<>();
    static final GamesDbResult<GamesDbPublishersData> CACHED_PUBLISHERS_DATA = new GamesDbResult<>();

    static final Map<String, GamesDbPaginatedResult<GamesDbGamesByPlatformData>> CACHED_GAMES_BY_PLATFORM_DATA = new HashMap<>();
    static final Map<String, GamesDbPaginatedResult<GamesDbImagesData>>          CACHED_GAMES_IMAGES_DATA      = new HashMap<>();

    abstract Map<String, String> getDefaultParams();

    @Override
    public String getSourceName() {
        return SOURCE_NAME;
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
    <D extends GamesDbData<?>, T extends GamesDbResult<D>> void populateCache(T cache, Type typeOfT, Class<D> dataClass, Path cachePath, String url, Map<String, String> params) {
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
                        Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Results returned={0}.", localData.data.count);
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
            catch(IOException ex) {
                Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    <E, D extends GamesDbPaginatedData<E>, T extends GamesDbPaginatedResult<D>> void populatePaginatedCache(T cache, Type typeOfT, Class<D> dataClass, Path cachePath, String url, Map<String, String> params) {
        if(!cache.isDataAvailable()) {
            Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Initializing local {0} cache...", dataClass.getName());
            try {
                T localData = getCachedData(cachePath, typeOfT);

                if(localData == null || !localData.isDataAvailable()) {
                    Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Retrieving data from remote source...");

                    final String gameIds = params.get("games_id");
                    if(gameIds != null) {
                        // special case for calling GamesDB with a lot of Game ID's.
                        // GamesDB api Games/Images only takes game id's and i try to attach all of a platforms id's but
                        // it can be too many like for nintendo ds so i'll limit it to 500 because i know that is doable.
                        final Map<String, String> localParams = new HashMap<>(params);
                        final List<String> gameIdList = Arrays.asList(gameIds.split(","));

                        int totalCount = 0;
                        StringBuilder shortenedGameIdParam = new StringBuilder();
                        for(int i = 0; i < gameIdList.size(); i++) {
                            shortenedGameIdParam.append(gameIdList.get(i)).append(",");

                            if(i == gameIdList.size() - 1 || (i + 1) % 500 == 0) {
                                localParams.put("games_id", shortenedGameIdParam.toString());
                                localData = getData(new JsonDataSourcePlugin<>(typeOfT), url, localParams);

                                if(localData == null) {
                                    Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.WARNING, "No data returned for {0}.", dataClass.getName());
                                }
                                else if(localData.isDataAvailable()) {
                                    Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Results returned={0}.", localData.data.count);
                                    Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "API requests remaining: monthly={0}, extra_allowance={1}.", new Object[]{localData.remaining_monthly_allowance, localData.extra_allowance});

                                    totalCount += localData.data.count;

                                    if(cache.data == null) {
                                        cache.data = localData.data;
                                    }
                                    else {
                                        cache.data.appendData(localData.data.values());
                                    }

                                    while(localData.hasNext()) {
                                        localData = getData(new JsonDataSourcePlugin<>(typeOfT), localData.pages.next);

                                        if(localData == null) {
                                            //if it's still null something went wrong.
                                            break;
                                        }

                                        totalCount += localData.data.count;

                                        Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Results returned={0}.", localData.data.count);
                                        Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "API requests remaining: monthly={0}, extra_allowance={1}.", new Object[]{localData.remaining_monthly_allowance, localData.extra_allowance});
                                        if(localData.isDataAvailable()) {
                                            cache.data.appendData(localData.data.values());
                                        }
                                    }
                                }

                                shortenedGameIdParam = new StringBuilder();
                            }
                        }

                        Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Total results returned={0}.", totalCount);
                        Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Writing {0} data to disk...", dataClass.getName());
                        writeCachedData(cachePath, cache);
                    }
                    else {
                        localData = getData(new JsonDataSourcePlugin<>(typeOfT), url, params);

                        if(localData == null) {
                            Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.WARNING, "No data returned for {0}.", dataClass.getName());
                        }
                        else if(localData.isDataAvailable()) {
                            Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Results returned={0}.", localData.data.count);
                            Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "API requests remaining: monthly={0}, extra_allowance={1}.", new Object[]{localData.remaining_monthly_allowance, localData.extra_allowance});

                            int totalCount = localData.data.count;

                            cache.data = localData.data;

                            while(localData.hasNext()) {
                                localData = getData(new JsonDataSourcePlugin<>(typeOfT), localData.pages.next);

                                if(localData == null) {
                                    //if it's still null something went wrong.
                                    break;
                                }

                                totalCount += localData.data.count;

                                Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Results returned={0}.", localData.data.count);
                                Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "API requests remaining: monthly={0}, extra_allowance={1}.", new Object[]{localData.remaining_monthly_allowance, localData.extra_allowance});
                                if(localData.isDataAvailable()) {
                                    cache.data.appendData(localData.data.values());
                                }
                            }

                            Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.INFO, "Total results returned={0}.", totalCount);
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
            catch(IOException ex) {
                Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void loadGamesByPlatformCache(String platformId) {
        GamesDbPaginatedResult<GamesDbGamesByPlatformData> cachedData = CACHED_GAMES_BY_PLATFORM_DATA.get(platformId);
        if(cachedData == null || !cachedData.isDataAvailable()) {
            try {
                cachedData = getCachedData(
                        ScraperFX.LOCALDB_PATH.resolve(GAMESDB_LOCAL_DIR).resolve(GAMES_BY_PLATFORM_DIR).resolve(platformId + ".json"),
                        new TypeToken<GamesDbPaginatedResult<GamesDbGamesByPlatformData>>() {
                        }.getType());

                if(cachedData != null && cachedData.isDataAvailable()) {
                    CACHED_GAMES_BY_PLATFORM_DATA.put(platformId, cachedData);
                }
            }
            catch(IOException ex) {
                Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void loadGamesImagesCache(String platformId) {
        GamesDbPaginatedResult<GamesDbImagesData> cachedData = CACHED_GAMES_IMAGES_DATA.get(platformId);
        if(cachedData == null || !cachedData.isDataAvailable()) {
            try {
                cachedData = getCachedData(
                        ScraperFX.LOCALDB_PATH.resolve(GAMESDB_LOCAL_DIR).resolve(IMAGES_DIR).resolve(platformId + "_images.json"),
                        new TypeToken<GamesDbPaginatedResult<GamesDbImagesData>>() {
                        }.getType());

                if(cachedData != null && cachedData.isDataAvailable()) {
                    CACHED_GAMES_IMAGES_DATA.put(platformId, cachedData);
                }
            }
            catch(IOException ex) {
                Logger.getLogger(GamesDbSourceBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    List<String> getGameIdsForPlatform(String platformId) {
        loadGamesByPlatformCache(platformId);

        final GamesDbPaginatedResult<GamesDbGamesByPlatformData> cachedData = CACHED_GAMES_BY_PLATFORM_DATA.get(platformId);
        if(cachedData != null && cachedData.isDataAvailable()) {
            final List<String> ids = new ArrayList<>();
            cachedData.data.values().forEach((game) -> ids.add(Integer.toString(game.id)));
            return ids;
        }

        return null;
    }
}
