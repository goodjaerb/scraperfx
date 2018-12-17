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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmappr.Xmappr;
import com.goodjaerb.scraperfx.datasource.DataSource;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.MetaData;

/**
 *
 * @author goodjaerb
 */
public class GamesDbLegacySource implements DataSource {

    private static final String API_BASE_URL = "http://legacy.thegamesdb.net/api/";
    private static final String API_GET_PLATFORMS_LIST = "GetPlatformsList.php";
    private static final String API_GET_PLATFORM_GAMES = "GetPlatformGames.php";
    private static final String API_GET_GAME = "GetGame.php";

    public static final String IMAGE_BASE_URL = "http://legacy.thegamesdb.net/banners/";
            
    private static final String PROP_USER_AGENT = "User-Agent";
    private static final String VAL_USER_AGENT = "Mozilla/5.0";
    
    private List<GamesDBPlatform> cachedPlatformList;
    private Map<Integer, List<GamesDBListGame>> cachedGameListMap;
    private Map<Integer, GamesDBGameMetaData> cachedGameMap;
    
    private Reader getXML(String api, String... params) {
        HttpURLConnection conn;
        int retryCount = 0;
        while(retryCount < 3) {
            try {
                String url = API_BASE_URL + api;
                if(params.length > 0) {
                    url += "?";
                }
                for(int i = 0; i < params.length; i+=2) {
                    url += params[i] + "=" + params[i + 1];
                }
                
                System.out.println("Connecting to '" + url + "'.");

                conn = (HttpURLConnection)new URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty(PROP_USER_AGENT, VAL_USER_AGENT);

                return new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            catch(MalformedURLException ex) {
                Logger.getLogger(GamesDbLegacySource.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
            catch(IOException ex) {
                if(++retryCount < 3) {
                    System.out.println("Connection error with thegamesdb.net. Retrying...");
                }
            }
        }
        
        return null;
    }
    
    private void populatePlatformList() {
        synchronized(this) {
            if(cachedPlatformList == null) {
                try(Reader xmlReader = getXML(API_GET_PLATFORMS_LIST)) {
                    Xmappr xm = new Xmappr(GamesDBPlatformList.class);
                    GamesDBPlatformList data = (GamesDBPlatformList)xm.fromXML(xmlReader);

                    cachedPlatformList = data.platforms.list;
                }
                catch (IOException ex) {
                    Logger.getLogger(GamesDbLegacySource.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private void populateGameList(Integer systemId) {
        synchronized(this) {
            if(cachedGameListMap == null || cachedGameListMap.get(systemId) == null) {
                try(Reader xmlReader = getXML(API_GET_PLATFORM_GAMES, "platform", systemId.toString())) {
                    Xmappr xm = new Xmappr(GamesDBGameListData.class);
                    GamesDBGameListData data = (GamesDBGameListData)xm.fromXML(xmlReader);

                    if(cachedGameListMap == null) {
                        cachedGameListMap = new HashMap<>();
                    }
                    cachedGameListMap.put(systemId, data.games);
                }
                catch (IOException ex) {
                    Logger.getLogger(GamesDbLegacySource.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void populateGameMetaData(Integer gameId) {
        synchronized(this) {
            if(cachedGameMap == null || cachedGameMap.get(gameId) == null) {
                try(Reader xmlReader = getXML(API_GET_GAME, "id", gameId.toString())) {
                    Xmappr xm = new Xmappr(GamesDBGameMetaData.class);

                    GamesDBGameMetaData data = (GamesDBGameMetaData)xm.fromXML(xmlReader);

                    if(cachedGameMap == null) {
                        cachedGameMap = new HashMap<>();
                    }
                    cachedGameMap.put(gameId, data);
                }
                catch (IOException ex) {
                    Logger.getLogger(GamesDbLegacySource.class.getName()).log(Level.SEVERE, null, ex);
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
