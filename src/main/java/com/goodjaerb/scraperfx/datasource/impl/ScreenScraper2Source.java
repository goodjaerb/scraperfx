/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import com.goodjaerb.scraperfx.ScraperFX;
import com.goodjaerb.scraperfx.datasource.CustomHttpDataSource;
import com.goodjaerb.scraperfx.datasource.impl.data.json.screenscraper.ScreenScraperGame;
import com.goodjaerb.scraperfx.datasource.impl.data.json.screenscraper.ScreenScraperSearchResults;
import com.goodjaerb.scraperfx.datasource.impl.data.json.screenscraper.ScreenScraperSystemIdMap;
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
    public static final String                  SOURCE_NAME = "Screen Scraper v2 (screenscraper.fr)";
    
    private static final String                 API_BASE_URL = "https://www.screenscraper.fr/api2/";
//    private static final String                 API_INFO = "jeuInfos.php";
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
    
//    private String crcCalc(Path filePath) throws IOException {
//        try(final InputStream inputStream = new BufferedInputStream(Files.newInputStream(filePath))) {
//            final CRC32 crc = new CRC32();
//
//            int cnt;
//            while ((cnt = inputStream.read()) != -1) {
//                crc.update(cnt);
//            }
//            String crcString = Long.toHexString(crc.getValue());
//            while(crcString.length() < 8) {
//                crcString = "0" + crcString;
//            }
//            return crcString;
//        }
//    }
    
    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }
    
    private List<ScreenScraperGame> getSearchResults(String systemName, Game game) {
        final Integer sysId = ScreenScraperSystemIdMap.getId(systemName);
        if(sysId == null) {
            return null;
        }

        final Map<String, String> params = new HashMap<>(DEFAULT_PARAMS);
        params.put("systemeid", Integer.toString(sysId));
        params.put("recherche", game.matchedName);
        
        try {
            final ScreenScraperSearchResults results = getData(new JsonDataSourcePlugin<>(ScreenScraperSearchResults.class), API_BASE_URL + API_SEARCH, params);
            if(results != null && results.response.jeux != null && !results.response.jeux.isEmpty()) {
                final List<ScreenScraperGame> resultsList = results.response.jeux;
                final List<ScreenScraperGame> returnList = new ArrayList<>();
                
                if(resultsList.size() == 1) {
                    final ScreenScraperGame ssGame = resultsList.get(0);
                    if(ssGame == null || ssGame.systemeid == null) {
                        return null;
                    }
                    
                    if(sysId == Integer.parseInt(resultsList.get(0).systemeid)) {
                        return resultsList;
                    }
                }
                else {
                    for(ScreenScraperGame ssGame : resultsList) {
                        if(sysId == Integer.parseInt(ssGame.systemeid)) {
                            if(game.metadata != null 
                                    && game.metadata.screenScraperId != null 
                                    && !game.metadata.screenScraperId.isEmpty()
                                    && ssGame.id.equals(game.metadata.screenScraperId)) {
                                return Collections.singletonList(ssGame);
                            }
                            else {
                                returnList.add(ssGame);
                            }
                        }
                    }
                    return returnList;
                }
//                results.response.jeux.stream().filter((game) -> (Integer.toString(sysId).equals(game.systemeid))).forEachOrdered((game) -> {
//                    games.add(game);
//                });
//                if(games.isEmpty()) {
//                    return null;
//                }
//                return games;
            }
        }
        catch (IOException ex) {
            Logger.getLogger(ScreenScraper2Source.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
//    private List<ScreenScraperGame> getInfo(String systemName, Game game, String gameId, Path filePath) {
//        final Integer sysId = ScreenScraperSystemIdMap.getSystemId(systemName);
//        if(sysId == null) {
//            return null;
//        }
//        
//        final Map<String, String> params = new HashMap<>(DEFAULT_PARAMS);
//        params.put("romnom", game.fileName);
//        params.put("systemeid", Integer.toString(sysId));
//        params.put("gameid", gameId);
////        try {
////            params.put("crc", crcCalc(filePath));
////        }
////        catch (IOException ex) {
////            Logger.getLogger(ScreenScraperSource.class.getName()).log(Level.WARNING, "Could not calculate CRC of file to assist in identifying on www.screenscraper.fr.", ex);
////        } // sometimes this causes the query to fail even though it's the actual name. but leaving it out breaks the query too.
//        
//        try {
//            final ScreenScraperInfo info = getData(new JsonDataSourcePlugin<>(ScreenScraperInfo.class), API_BASE_URL + API_INFO, params);
//            if(info != null) {
//                return Collections.singletonList(info.response.jeu);
//            }
//        }
//        catch(IOException ex) {
//            Logger.getLogger(ScreenScraper2Source.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
    
    private List<ScreenScraperGame> getJsonData(String systemName, Game game) {
//        if(game.metadata != null && game.metadata.screenScraperId != null && !game.metadata.screenScraperId.isEmpty()) {
//            return getInfo(systemName, game, game.metadata.screenScraperId, filePath);
//        }
        return getSearchResults(systemName, game);
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
