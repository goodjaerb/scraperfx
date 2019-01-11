/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import com.goodjaerb.scraperfx.ScraperFX;
import com.goodjaerb.scraperfx.datasource.impl.data.json.gamesdb.GamesDbDevelopersData;
import com.goodjaerb.scraperfx.datasource.impl.data.json.gamesdb.GamesDbGamesByPlatformData;
import com.goodjaerb.scraperfx.datasource.impl.data.json.gamesdb.GamesDbGenresData;
import com.goodjaerb.scraperfx.datasource.impl.data.json.gamesdb.GamesDbImagesData;
import com.goodjaerb.scraperfx.datasource.impl.data.json.gamesdb.GamesDbPaginatedResult;
import com.goodjaerb.scraperfx.datasource.impl.data.json.gamesdb.GamesDbPlatform;
import com.goodjaerb.scraperfx.datasource.impl.data.json.gamesdb.GamesDbPlatformsData;
import com.goodjaerb.scraperfx.datasource.impl.data.json.gamesdb.GamesDbPublishersData;
import com.goodjaerb.scraperfx.datasource.impl.data.json.gamesdb.GamesDbResult;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.Image;
import com.goodjaerb.scraperfx.settings.MetaData;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.goodjaerb.scraperfx.datasource.impl.GamesDbSourceBase.GAMESDB_LOCAL_DIR;

/**
 *
 * @author goodjaerb <goodjaerb@gmail.com>
 */
public class GamesDbPublicSource extends GamesDbSourceBase {
    private static final Map<String, String> DEFAULT_PARAMS;
    
    static {
        final Map<String, String> initialParams = new HashMap<>();
        initialParams.put("apikey", ScraperFX.getKeysValue("GamesDb.Public"));
        
        DEFAULT_PARAMS = Collections.unmodifiableMap(initialParams);
    }
    
    @Override
    public Map<String, String> getDefaultParams() {
        return DEFAULT_PARAMS;
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
    
    public List<GamesDbPlatform> getPlatforms() {
        populatePlatformsData();
        
        if(CACHED_PLATFORMS_DATA.isDataAvailable()) {
            final List<GamesDbPlatform> platforms = new ArrayList<>();
            CACHED_PLATFORMS_DATA.data.platforms.values().forEach((platform) -> {
                platforms.add(new GamesDbPlatform(platform));
            });
            return platforms;
        }
        return null;
    }
    
    private String getPlatformIdForName(String platformName) {
        for(final GamesDbPlatform platform : getPlatforms()) {
            if(platformName.equals(platform.name)) {
                return Integer.toString(platform.id);
            }
        }
        return null;
    }

    @Override
    public List<String> getSystemGameNames(String systemName) {
        final String platformId = getPlatformIdForName(systemName);
        if(platformId != null) {
            loadGamesByPlatformCache(platformId);
            
            final GamesDbPaginatedResult<GamesDbGamesByPlatformData> cachedData = CACHED_GAMES_BY_PLATFORM_DATA.get(platformId);
            if(cachedData != null && cachedData.isDataAvailable()) {
                final List<String> gameNames = new ArrayList<>();
                cachedData.data.games.forEach((game) -> {
                    gameNames.add(game.game_title);
                });
                return gameNames;
            }
        }
        return null;
    }
    
    private GamesDbGamesByPlatformData.Game getGame(String platformId, String gameName) {
        for(final GamesDbGamesByPlatformData.Game game : CACHED_GAMES_BY_PLATFORM_DATA.get(platformId).data.games) {
            if(gameName.equals(game.game_title)) {
                return game;
            }
        }
        return null;
    }

    @Override
    public MetaData getMetaData(String systemName, Game game) {
        final String platformId = getPlatformIdForName(systemName);
        loadGamesByPlatformCache(platformId);
        
        final GamesDbGamesByPlatformData.Game g = getGame(platformId, game.matchedName);
        if(g != null) {
            final MetaData data = new MetaData();
            
            data.metaReleaseDate    = g.release_date;
            data.metaRating         = g.rating;
            data.metaPublisher      = publishersToString(g.publishers);
            data.metaName           = g.game_title;
            data.metaDeveloper      = developersToString(g.developers);
            data.metaDesc           = g.overview;
            data.players            = g.players == null ? "" : g.players.toString();
            data.metaGenre          = genresToString(g.genres);
            data.images             = convertImages(platformId, g.id.toString());
            
            return data;
        }
        
        return null;
    }
    
    private List<Image> convertImages(String platformId, String gameId) {
        loadGamesImagesCache(platformId);
        
        final GamesDbPaginatedResult<GamesDbImagesData> cachedData = CACHED_GAMES_IMAGES_DATA.get(platformId);
        if(cachedData != null && cachedData.isDataAvailable()) {
            final List<Image> images = new ArrayList<>();
            
            int fanartCount = 0;
            int boxartCount = 0;
            int screenshotCount = 0;
            int logoCount = 0;
            
            final List<GamesDbImagesData.Image> cachedImages = cachedData.data.images.get(gameId);
            if(cachedImages != null && !cachedImages.isEmpty()) {
                for(final GamesDbImagesData.Image image : cachedImages) {
                    switch(image.type) {
                        case "fanart":
                            images.add(new Image("fanart", cachedData.data.base_url.thumb + image.filename, (fanartCount == 0)));
                            fanartCount++;
                            break;
    //                    case "banner":
    //                        break;
                        case "boxart":
                            if("front".equals(image.side)) {
                                images.add(new Image("box-front", cachedData.data.base_url.thumb + image.filename, (boxartCount == 0)));
                                boxartCount++;
                            }
                            break;
                        case "screenshot":
                            images.add(new Image("screenshot", cachedData.data.base_url.thumb + image.filename, (screenshotCount == 0)));
                            screenshotCount++;
                            break;
                        case "clearlogo":
                            images.add(new Image("logo", cachedData.data.base_url.thumb + image.filename, (logoCount == 0)));
                            logoCount++;
                            break;
                        default:
                            //don't care.
                    }
                }
            }
            return images;
        }
        
        return Collections.emptyList();
    }
    
    private String genresToString(List<Integer> genres) {
        if(genres != null) {
            populateGenresData();

            if(CACHED_GENRES_DATA.isDataAvailable()) {
                String result = "";
                for(int i = 0; i < genres.size(); i++) {
                    result += CACHED_GENRES_DATA.data.genres.get(genres.get(i).toString()).name;
                    if(i < genres.size() - 1) {
                        result += ", ";
                    }
                }
                return result;
            }
        }
        return "";
    }
    
    private String developersToString(List<Integer> developers) {
        if(developers != null) {
            populateDevelopersData();

            if(CACHED_DEVELOPERS_DATA.isDataAvailable()) {
                String result = "";
                for(int i = 0; i < developers.size(); i++) {
                    result += CACHED_DEVELOPERS_DATA.data.developers.get(developers.get(i).toString()).name;
                    if(i < developers.size() - 1) {
                        result += ", ";
                    }
                }
                return result;
            }
        }
        return "";
    }
    
    private String publishersToString(List<Integer> publishers) {
        if(publishers != null) {
            populatePublishersData();

            if(CACHED_PUBLISHERS_DATA.isDataAvailable()) {
                String result = "";
                for(int i = 0; i < publishers.size(); i++) {
                    result += CACHED_PUBLISHERS_DATA.data.publishers.get(publishers.get(i).toString()).name;
                    if(i < publishers.size() - 1) {
                        result += ", ";
                    }
                }
                return result;
            }
        }
        return "";
    }
}
