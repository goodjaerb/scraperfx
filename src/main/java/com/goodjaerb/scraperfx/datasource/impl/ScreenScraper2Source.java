/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import com.goodjaerb.scraperfx.ScraperFX;
import com.goodjaerb.scraperfx.datasource.CustomHttpDataSource;
import com.goodjaerb.scraperfx.datasource.impl.data.json.screenscraper.ScreenScraperGame;
import com.goodjaerb.scraperfx.datasource.impl.data.json.screenscraper.ScreenScraperInfo;
import com.goodjaerb.scraperfx.datasource.impl.data.json.screenscraper.ScreenScraperSearchResults;
import com.goodjaerb.scraperfx.datasource.impl.data.xml.screenscraper.ScreenScraperSystemIdMap;
import com.goodjaerb.scraperfx.datasource.plugin.JsonDataSourcePlugin;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.MetaData;
import java.io.IOException;
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
public class ScreenScraper2Source extends CustomHttpDataSource {
    private static final String                 API_BASE_URL = "https://www.screenscraper.fr/api2/";
    private static final String                 API_INFO = "jeuInfos.php";
    private static final String                 API_SEARCH = "jeuRecherche.php";
    private static final Map<String, String>    DEFAULT_PARAMS;
    
//    public enum MetaDataKey {
//        ID,
//        NAME,
//        VIDEO_DOWNLOAD,
//        VIDEO_EMBED,
//        BOX_US,
//        BOX_WORLD,
//        SCREENSHOT;
//    }
    
    static {
        final Map<String, String> initialParams = new HashMap<>();
        initialParams.put("devid", ScraperFX.getKeysValue("ScreenScraper.ID"));
        initialParams.put("devpassword", ScraperFX.getKeysValue("ScreenScraper.KEY"));
        initialParams.put("softname", "scraperfx");
        initialParams.put("output", "json");
        
        DEFAULT_PARAMS = Collections.unmodifiableMap(initialParams);
    }
    
    @Override
    public String getSourceName() {
        return "Screen Scraper v2 (screenscraper.fr)";
    }
    
    private List<ScreenScraperGame> getSearchResults(String systemName, String gameName) {
        final Integer sysId = ScreenScraperSystemIdMap.getSystemId(systemName);
        if(sysId == null) {
            return null;
        }

        final Map<String, String> params = new HashMap<>(DEFAULT_PARAMS);
        params.put("systemeid", Integer.toString(sysId));
        params.put("recherche", gameName);
        
        try {
            final ScreenScraperSearchResults results = getData(new JsonDataSourcePlugin<>(ScreenScraperSearchResults.class), API_BASE_URL + API_SEARCH, params);
            if(results != null) {
                final List<ScreenScraperGame> games = new ArrayList<>();
                // if there are results it should be the correct system since i put it
                // in the query but i'm just going to make sure.
                results.response.jeux.stream().filter((game) -> (Integer.toString(sysId).equals(game.systemeid))).forEachOrdered((game) -> {
                    games.add(game);
                });
                if(games.isEmpty()) {
                    return null;
                }
                return games;
            }
        }
        catch (IOException ex) {
            Logger.getLogger(ScreenScraper2Source.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private List<ScreenScraperGame> getInfo(String systemName, String gameName, String gameId) {
        final Integer sysId = ScreenScraperSystemIdMap.getSystemId(systemName);
        if(sysId == null) {
            return null;
        }
        
        final Map<String, String> params = new HashMap<>(DEFAULT_PARAMS);
        params.put("systemeid", Integer.toString(sysId));
        params.put("gameid", gameId);
        params.put("romnom", gameName);
        
        try {
            final ScreenScraperInfo info = getData(new JsonDataSourcePlugin<>(ScreenScraperInfo.class), API_BASE_URL + API_INFO, params);
            if(info != null) {
                return Collections.singletonList(info.response.jeu);
            }
        }
        catch(IOException ex) {
            Logger.getLogger(ScreenScraper2Source.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private List<ScreenScraperGame> getJsonData(String systemName, Game game) {
        if(game.metadata != null && game.metadata.screenScraperId != null && !game.metadata.screenScraperId.isEmpty()) {
            return getInfo(systemName, game.matchedName, game.metadata.screenScraperId);
        }
        return getSearchResults(systemName, game.matchedName);
    }
    
    public List<ScreenScraperGame> getExtraMetaData(String systemName, Game game) {
        final List<ScreenScraperGame> results = getJsonData(systemName, game);
        
        if(results != null && !results.isEmpty()) {
            return results;
//            final List<Map<ScreenScraper2Source.MetaDataKey, String>> returnList = new ArrayList<>();
            
//            for(ScreenScraperGame result : results) {
//                final Map<MetaDataKey, String> metaDataMap = new HashMap<>();
//
//                metaDataMap.put(MetaDataKey.ID, result.id);
//                if(result.noms != null && !result.noms.isEmpty()) {
//                    metaDataMap.put(MetaDataKey.NAME, result.noms.get(0).text);
//                }
//                
//                if(result.medias != null && !result.medias.isEmpty()) {
//                    final List<ScreenScraperGame.Media> medias = result.medias;
//                    for(ScreenScraperGame.Media media : medias) {
//                        switch(media.type) {
//                            case "video":
//                                metaDataMap.put(MetaDataKey.VIDEO_DOWNLOAD, media.url);
//                                metaDataMap.put(MetaDataKey.VIDEO_EMBED, "https://www.screenscraper.fr/medias/" + result.systemeid + "/" + result.id + "/video.mp4");
//                                break;
//                            case "ss":
//                                metaDataMap.put(MetaDataKey.SCREENSHOT, media.url);
//                                break;
//                            case "box-2D":
//                                switch(media.region) {
//                                    case "us":
//                                        metaDataMap.put(MetaDataKey.BOX_US, media.url);
//                                        break;
//                                    case "wor":
//                                        metaDataMap.put(MetaDataKey.BOX_WORLD, media.url);
//                                        break;
//                                    default:
//                                }
//                                break;
//                            default:
//                        }
//                    }
//                }
//                
//                returnList.add(metaDataMap);
//            }
//            
//            return returnList;
        }
        return null;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
