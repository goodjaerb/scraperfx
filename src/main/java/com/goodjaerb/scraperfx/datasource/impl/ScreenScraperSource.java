/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import com.goodjaerb.scraperfx.ScraperFX;
import com.goodjaerb.scraperfx.datasource.DataSource;
import com.goodjaerb.scraperfx.datasource.impl.screenscraper.ScreenScraperSystemIdMap;
import com.goodjaerb.scraperfx.datasource.impl.screenscraper.ScreenScraperXmlGameData;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.MetaData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmappr.Xmappr;

/**
 *
 * @author goodjaerb
 */
public class ScreenScraperSource implements DataSource {
    private static final String API_BASE_URL = "https://www.screenscraper.fr/api/";
//    private static final String API_SYSTEMS_INFO = "systemesListe.php?devid=#DEVID&devpassword=#DEVPASS&softname=scraperfx&output=xml";
    private static final String API_GAME_INFO = "jeuInfos.php?devid=#DEVID&devpassword=#DEVPASS&softname=scraperfx&output=xml&systemeid=#SYSTEMID&romnom=#GAMENAME";
    private static final String PROP_USER_AGENT = "User-Agent";
    private static final String VAL_USER_AGENT = "Mozilla/5.0";
    
    private Reader getGameXML(String systemName, String gameName) {
        HttpURLConnection conn;
        int retryCount = 0;
        while(retryCount < 3) {
            try {
                String url = API_BASE_URL + API_GAME_INFO;
//                if(params.length > 0) {
//                    url += "?";
//                }
//                for(int i = 0; i < params.length; i+=2) {
//                    url += params[i] + "=" + params[i + 1];
//                }

                url = url.replaceAll("#DEVID", ScraperFX.getKeysValue("ScreenScraper.ID"));
                url = url.replaceAll("#DEVPASS", ScraperFX.getKeysValue("ScreenScraper.KEY"));
                url = url.replaceAll("#SYSTEMID", Integer.toString(ScreenScraperSystemIdMap.getSystemId(systemName)));
                url = url.replaceAll("#GAMENAME", gameName.replaceAll(" ", "%20").replaceAll("&", "%26"));
//                url = url.replaceAll(" ", "%20");
                
                System.out.println("Connecting to '" + url + "'.");
                
                conn = (HttpURLConnection)new URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty(PROP_USER_AGENT, VAL_USER_AGENT);

                return new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            catch(MalformedURLException ex) {
                Logger.getLogger(ScreenScraperSource.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
            catch(IOException ex) {
                if(++retryCount < 3) {
                    System.out.println("Connection error with screenscraper.fr. Retrying...");
                }
            }
        }
        return null;
    }
    
    @Override
    public String[] getVideoLinks(String systemName, Game game) {
        try(Reader xmlReader = getGameXML(systemName, game.matchedName)) {
            if(xmlReader != null) {
                Xmappr xm = new Xmappr(ScreenScraperXmlGameData.class);
                try {
                    ScreenScraperXmlGameData data = (ScreenScraperXmlGameData)xm.fromXML(xmlReader);

                    if(data != null && data.game != null && data.game.medias != null && data.game.medias.videoDownloadUrl != null) {
                        return new String[] { 
                            data.game.medias.videoDownloadUrl, 
                            "https://www.screenscraper.fr/medias/" + ScreenScraperSystemIdMap.getSystemId(systemName) + "/" + data.game.id + "/video.mp4"
                        };
                    }
                }
                catch(Exception ex) {
                    Logger.getLogger(ScreenScraperSource.class.getName()).log(Level.WARNING, "Unable to parse XML. Game probably did not match correctly between GamesDB and ScreenScraper.", ex);
                }
            }
        }
        catch (IOException ex) {
            Logger.getLogger(ScreenScraperSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
//    @Override
//    public String getVideoEmbed(String systemName, Game game) {
//        try(Reader xmlReader = getGameXML(systemName, game.matchedName)) {
//            Xmappr xm = new Xmappr(ScreenScraperXmlGameData.class);
//
//            ScreenScraperXmlGameData data = (ScreenScraperXmlGameData)xm.fromXML(xmlReader);
//            return "https://www.screenscraper.fr/medias/" + ScreenScraperSystemIdMap.getSystemId(systemName) + "/" + data.game.id + "/video.mp4";
//        }
//        catch (IOException ex) {
//            Logger.getLogger(ScreenScraperSource.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
    
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
//            try(Reader xmlReader = getGameXML(systemName, game.matchedName)) {
//                Xmappr xm = new Xmappr(ScreenScraperXmlGameData.class);
//
//                ScreenScraperXmlGameData data = (ScreenScraperXmlGameData)xm.fromXML(xmlReader);
//                System.out.println(data);
//            }
//            catch (IOException ex) {
//                Logger.getLogger(ScreenScraperSource.class.getName()).log(Level.SEVERE, null, ex);
//            }
////        Gson gson = new Gson();
////        try {
////            ScreenScraperSystemData data = gson.fromJson(getSystemsJson(), ScreenScraperSystemData.class);
////            System.out.println(data);
////            if(data != null && data.result != null && data.result.length > 0) {
////                MetaData metadata = new MetaData();
////                metadata.metaName = data.result[0].title;
////                metadata.videoembed = "https://www.youtube.com/embed/" + data.result[0].youtube_video_id;
////                metadata.videodownload = data.result[0].url_video_shortplay;
////                metadata.images = new ArrayList<>();
////                metadata.images.add(new Image("title", data.result[0].url_image_title, "png", true));
////                metadata.images.add(new Image("game", data.result[0].url_image_ingame, "png", true));
////                metadata.images.add(new Image("marquee", data.result[0].url_image_marquee, "png", true));
////                metadata.images.add(new Image("flyer", data.result[0].url_image_flyer, "png", true));
////                return metadata;
////            }
////        }
////        catch(JsonSyntaxException ex) {
////            Logger.getLogger(ScreenScraperSource.class.getName()).log(Level.SEVERE, null, ex);
////        }
//        
//        return null;
    }
     
//    public String getSystemsJson() {
//        HttpURLConnection conn = null;
//        int retryCount = 0;
//        while(retryCount < 3) {
//            try {
//                String url = API_BASE_URL + API_SYSTEMS_INFO;
//                url = url.replaceAll("#DEVID", ScraperFX.getKeysValue("ScreenScraper.ID"));
//                url = url.replaceAll("#DEVPASS", ScraperFX.getKeysValue("ScreenScraper.KEY"));
//                
//                System.out.println("Connecting to '" + url + "'.");
//                
//                conn = (HttpURLConnection)new URL(url).openConnection();
//                conn.setRequestMethod("GET");
//                conn.setRequestProperty(PROP_USER_AGENT, VAL_USER_AGENT);
////                conn.setRequestProperty(PROP_CONTENT_LANGUAGE, VAL_CONTENT_LANGUAGE);
//
//                return new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine();
//            }
//            catch(MalformedURLException ex) {
//                Logger.getLogger(ScreenScraperSource.class.getName()).log(Level.SEVERE, null, ex);
//                break;
//            }
//            catch(IOException ex) {
//                if(++retryCount < 3) {
//                    System.out.println("Connection error with screenscraper.fr. Retrying...");
//                }
//            }
//        }
//        
//        return null;
//    }
}
