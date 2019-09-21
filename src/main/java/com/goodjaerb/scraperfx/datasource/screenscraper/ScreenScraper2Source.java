/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.screenscraper;

import com.goodjaerb.scraperfx.ScraperFX;
import com.goodjaerb.scraperfx.datasource.CustomHttpDataSource;
import com.goodjaerb.scraperfx.datasource.plugin.JsonDataSourcePlugin;
import com.goodjaerb.scraperfx.datasource.screenscraper.data.ScreenScraperGame;
import com.goodjaerb.scraperfx.datasource.screenscraper.data.ScreenScraperInfoV1;
import com.goodjaerb.scraperfx.datasource.screenscraper.data.ScreenScraperSearchResults;
import com.goodjaerb.scraperfx.datasource.screenscraper.data.ScreenScraperSystemIdMap;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.MetaData;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author goodjaerb
 */
public class ScreenScraper2Source extends CustomHttpDataSource {
    public static final String SOURCE_NAME = "Screen Scraper v2 (screenscraper.fr)";

    private static final String              API_BASE_URL = "https://www.screenscraper.fr/";
    private static final String              API_V1       = "api/";
    private static final String              API_V2       = "api2/";
    private static final String              API_INFO     = "jeuInfos.php"; // v1 & v2.
    private static final String              API_SEARCH   = "jeuRecherche.php"; //v2 only.
    private static final Map<String, String> DEFAULT_PARAMS;

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
        DEFAULT_PARAMS = Map.of(
                "devid", ScraperFX.getKeysValue("ScreenScraper.ID"),
                "devpassword", ScraperFX.getKeysValue("ScreenScraper.KEY"),
                "softname", "scraperfx",
                "output", "json");
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

    //    private long lastCheckTime = 0L;
    private List<ScreenScraperGame> getSearchResults(String systemName, Game game, boolean forceUpdate) {
//        if(System.nanoTime() - lastCheckTime > TimeUnit.SECONDS.toNanos(5)) {
//            try {
//                TimeUnit.SECONDS.sleep(5);
//            }
//            catch (InterruptedException ex) {
//                Logger.getLogger(ScreenScraper2Source.class.getName()).log(Level.SEVERE, null, ex);
//                return null;
//            }
//        }
        final Integer sysId = ScreenScraperSystemIdMap.getId(systemName);
        if(sysId == null) {
            return null;
        }

        final Map<String, String> params = new HashMap<>(DEFAULT_PARAMS);
        params.put("systemeid", Integer.toString(sysId));
        params.put("recherche", game.matchedName);

        try {
//            lastCheckTime = System.nanoTime();
            final ScreenScraperSearchResults results = getData(new JsonDataSourcePlugin<>(ScreenScraperSearchResults.class), API_BASE_URL + API_V2 + API_SEARCH, params);
//            System.out.println("Received result.");
            if(results != null && results.response.jeux != null && !results.response.jeux.isEmpty()) {
                final List<ScreenScraperGame> resultsList = results.response.jeux;
                final List<ScreenScraperGame> returnList = new ArrayList<>();

                if(resultsList.size() == 1) {
                    if(resultsList.get(0).id == null) {
                        return null;
                    }

                    final ScreenScraperGame ssGame = getInfo(resultsList.get(0).systemeid, resultsList.get(0).id, resultsList.get(0).noms.get(0).text);
//                    final ScreenScraperGame ssGame = resultsList.get(0);
                    if(ssGame == null || ssGame.systemeid == null) {
                        return null;
                    }

                    if(sysId == Integer.parseInt(ssGame.systemeid)) {
                        return Collections.singletonList(ssGame);
//                        return resultsList;
                    }
                }
                else {
                    for(final ScreenScraperGame ssGame : resultsList) {
                        final ScreenScraperGame fromInfosGame = getInfo(ssGame.systemeid, ssGame.id, ssGame.noms.get(0).text);

                        if(fromInfosGame != null && sysId == Integer.parseInt(fromInfosGame.systemeid)) {
                            if(!forceUpdate
                                    && game.metadata != null
                                    && game.metadata.screenScraperId != null
                                    && !game.metadata.screenScraperId.isEmpty()
                                    && fromInfosGame.id.equals(game.metadata.screenScraperId)) {
                                return Collections.singletonList(fromInfosGame);
                            }
                            else {
                                returnList.add(fromInfosGame);
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
        catch(IOException ex) {
            Logger.getLogger(ScreenScraper2Source.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private ScreenScraperGame getInfo(String systemId, String gameId, String gameName) {
        final Map<String, String> params = new HashMap<>(DEFAULT_PARAMS);
        params.put("romnom", gameName);
        params.put("systemeid", systemId);
        params.put("gameid", gameId);
//        try {
//            params.put("crc", crcCalc(filePath));
//        }
//        catch (IOException ex) {
//            Logger.getLogger(ScreenScraperSource.class.getName()).log(Level.WARNING, "Could not calculate CRC of file to assist in identifying on www.screenscraper.fr.", ex);
//        } // sometimes this causes the query to fail even though it's the actual name. but leaving it out breaks the query too.

        try {
            final ScreenScraperInfoV1 info = getData(new JsonDataSourcePlugin<>(ScreenScraperInfoV1.class), API_BASE_URL + API_V1 + API_INFO, params);
//            System.out.println("Received result. " + info);
            if(info != null && info.response != null && info.response.jeu != null) {
                return new ScreenScraperGame(info.response.jeu);
//                return Collections.singletonList(new ScreenScraperGame(info.response.jeu));
            }
        }
        catch(IOException ex) {
            Logger.getLogger(ScreenScraper2Source.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private ScreenScraperGame getInfo(String systemName, Game game) {//, String gameId, Path filePath) {
        final Integer sysId = ScreenScraperSystemIdMap.getId(systemName);
        if(sysId == null) {
            return null;
        }

        return getInfo(Integer.toString(sysId), game.metadata.screenScraperId, game.matchedName);
    }

    private List<ScreenScraperGame> getJsonData(String systemName, Game game, boolean forceUpdate) {
        if(!forceUpdate && game.metadata != null && game.metadata.screenScraperId != null && !game.metadata.screenScraperId.isEmpty()) {
            final ScreenScraperGame result = getInfo(systemName, game);
            if(result == null) {
                return null;
            }
            return Collections.singletonList(result);
//            return getInfo(systemName, game);//, game.metadata.screenScraperId, filePath);
        }
        return getSearchResults(systemName, game, forceUpdate);
    }

    public List<ScreenScraperGame> getExtraMetaData(String systemName, Game game, boolean forceUpdate) {
        final List<ScreenScraperGame> results = getJsonData(systemName, game, forceUpdate);

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
