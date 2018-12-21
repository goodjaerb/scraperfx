/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import com.goodjaerb.scraperfx.datasource.impl.gamesdblegacy.GamesDBGame;
import com.goodjaerb.scraperfx.datasource.impl.gamesdblegacy.GamesDBListGame;
import com.goodjaerb.scraperfx.datasource.impl.gamesdblegacy.GamesDBGameMetaData;
import com.goodjaerb.scraperfx.datasource.impl.gamesdblegacy.GamesDBPlatform;
import com.goodjaerb.scraperfx.datasource.impl.gamesdblegacy.GamesDBGameListData;
import com.goodjaerb.scraperfx.datasource.impl.gamesdblegacy.GamesDBPlatformList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.goodjaerb.scraperfx.datasource.XmlDataSource;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.MetaData;

/**
 *
 * @author goodjaerb
 */
public class GamesDbLegacySource extends XmlDataSource {

    private static final String API_BASE_URL = "http://legacy.thegamesdb.net/api/";
    private static final String API_GET_PLATFORMS_LIST = "GetPlatformsList.php";
    private static final String API_GET_PLATFORM_GAMES = "GetPlatformGames.php";
    private static final String API_GET_GAME = "GetGame.php";

    public static final String IMAGE_BASE_URL = "http://legacy.thegamesdb.net/banners/";
            
    private List<GamesDBPlatform> cachedPlatformList;
    private Map<Integer, List<GamesDBListGame>> cachedGameListMap;
    private Map<Integer, GamesDBGameMetaData> cachedGameMap;
    
    @Override
    public String getSourceName() {
        return "TheGamesDB (legacy.thegamesdb.net)";
    }
    
    private <T> T getXmlData(Class<T> dataHolderClass, String api, String... params) {
        String url = API_BASE_URL + api;
        if(params.length > 0) {
            url += "?";
        }
        for(int i = 0; i < params.length; i+=2) {
            url += params[i] + "=" + params[i + 1];
        }
        
        try {
            return getXml(dataHolderClass, url);
        }
        catch (IOException ex) {
            Logger.getLogger(GamesDbLegacySource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private void populatePlatformList() {
        synchronized(this) {
            if(cachedPlatformList == null) {
                final GamesDBPlatformList data = getXmlData(GamesDBPlatformList.class, API_GET_PLATFORMS_LIST);
                if(data != null) {
                    cachedPlatformList = data.platforms.list;
                }
            }
        }
    }
    
    private void populateGameList(Integer systemId) {
        synchronized(this) {
            if(cachedGameListMap == null || cachedGameListMap.get(systemId) == null) {
                final GamesDBGameListData data = getXmlData(GamesDBGameListData.class, API_GET_PLATFORM_GAMES, "platform", systemId.toString());
                if(data != null) {
                    if(cachedGameListMap == null) {
                        cachedGameListMap = new HashMap<>();
                    }
                    cachedGameListMap.put(systemId, data.games);
                }
            }
        }
    }

    private void populateGameMetaData(Integer gameId) {
        synchronized(this) {
            if(cachedGameMap == null || cachedGameMap.get(gameId) == null) {
                final GamesDBGameMetaData data = getXmlData(GamesDBGameMetaData.class, API_GET_GAME, "id", gameId.toString());
                if(data != null) {
                    if(cachedGameMap == null) {
                        cachedGameMap = new HashMap<>();
                    }
                    cachedGameMap.put(gameId, data);
                }
            }
        }
    }
    
    @Override
    public List<String> getSystemNames() {
        populatePlatformList();
        
        List<String> result = new ArrayList<>();
        cachedPlatformList.stream().forEach((p) -> {
            result.add(p.name);
        });
        return result;
    }
    
    private Integer getSystemId(String name) {
        populatePlatformList();
        
        for(GamesDBPlatform p : cachedPlatformList) {
            if(name.equals(p.name)) {
                return p.id;
            }
        }
        return null;
    }

    @Override
    public List<String> getSystemGameNames(String systemName) {
        return getSystemGameNames(getSystemId(systemName));
    }

    private List<String> getSystemGameNames(Integer systemId) {
        populateGameList(systemId);
        
        List<String> result = new ArrayList<>();
        cachedGameListMap.get(systemId).stream().forEach((g) -> {
            result.add(g.gameTitle);
        });
        
        return result;
    }

    private Integer getGameId(String systemName, String gameName) {
        populateGameList(getSystemId(systemName));
        
        for(GamesDBListGame g : cachedGameListMap.get(getSystemId(systemName))) {
            if(gameName.equals(g.gameTitle)) {
                return g.id;
            }
        }
        return -1;
    }
    
    @Override
    public MetaData getMetaData(String systemName, Game game) {
        Integer id = getGameId(systemName, game.matchedName);
        populateGameMetaData(id);
        
        MetaData data = null;
        if(cachedGameMap.get(id).game != null) {
            data = new MetaData();

            GamesDBGame g = cachedGameMap.get(id).game;
            data.metaReleaseDate    = g.releaseDate;
            data.metaRating         = g.rating;
            data.metaPublisher      = g.publisher;
            data.metaName           = g.title;
            data.metaDeveloper      = g.developer;
            data.metaDesc           = g.overview;
            data.players            = g.players;
            data.metaGenre          = g.convertGenres();
            data.images             = g.getImages();
        }
        return data;
    }
}
