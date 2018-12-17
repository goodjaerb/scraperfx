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

                Integer sysId = ScreenScraperSystemIdMap.getSystemId(systemName);
                if(sysId == null) {
                    return null;
                }
                url = url.replaceAll("#DEVID", ScraperFX.getKeysValue("ScreenScraper.ID"));
                url = url.replaceAll("#DEVPASS", ScraperFX.getKeysValue("ScreenScraper.KEY"));
                url = url.replaceAll("#SYSTEMID", Integer.toString(sysId));
                url = url.replaceAll("#GAMENAME", gameName.replaceAll(" ", "%20").replaceAll("&", "%26").replaceAll("\\$", "%24").replaceAll("!", "%21"));
                
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
            catch(Exception ex) {
                Logger.getLogger(ScreenScraperSource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    @Override
    public String[] getVideoLinks(String systemName, Game game) {
        System.out.println("Getting video links for '" + game.matchedName + "'.");
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
