/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import com.goodjaerb.scraperfx.ScraperFX;
import com.goodjaerb.scraperfx.datasource.XmlDataSource;
import com.goodjaerb.scraperfx.datasource.impl.screenscraper.ScreenScraperSystemIdMap;
import com.goodjaerb.scraperfx.datasource.impl.screenscraper.ScreenScraperXmlGameData;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.MetaData;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author goodjaerb
 */
public class ScreenScraperSource extends XmlDataSource {
    private static final String API_BASE_URL = "https://www.screenscraper.fr/api/";
//    private static final String API_SYSTEMS_INFO = "systemesListe.php?devid=#DEVID&devpassword=#DEVPASS&softname=scraperfx&output=xml";
    private static final String API_GAME_INFO = "jeuInfos.php?devid=#DEVID&devpassword=#DEVPASS&softname=scraperfx&output=xml&systemeid=#SYSTEMID&romnom=#GAMENAME";
    
    @Override
    public String getSourceName() {
        return "Screen Scraper (screenscraper.fr)";
    }
    
    private ScreenScraperXmlGameData getXmlData(String systemName, String gameName) {
        String url = API_BASE_URL + API_GAME_INFO;

        Integer sysId = ScreenScraperSystemIdMap.getSystemId(systemName);
        if(sysId == null) {
            return null;
        }
        url = url.replaceAll("#DEVID", ScraperFX.getKeysValue("ScreenScraper.ID"));
        url = url.replaceAll("#DEVPASS", ScraperFX.getKeysValue("ScreenScraper.KEY"));
        url = url.replaceAll("#SYSTEMID", Integer.toString(sysId));
        url = url.replaceAll("#GAMENAME", gameName.replaceAll(" ", "%20").replaceAll("&", "%26").replaceAll("\\$", "%24").replaceAll("!", "%21"));
        
        try {
            return getXml(ScreenScraperXmlGameData.class, url);
        }
        catch (IOException ex) {
            Logger.getLogger(ScreenScraperSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public String[] getVideoLinks(String systemName, Game game) {
        final ScreenScraperXmlGameData data = getXmlData(systemName, game.matchedName);
        if(data != null && data.game != null && data.game.medias != null && data.game.medias.videoDownloadUrl != null) {
            return new String[] { 
                data.game.medias.videoDownloadUrl, 
                "https://www.screenscraper.fr/medias/" + ScreenScraperSystemIdMap.getSystemId(systemName) + "/" + data.game.id + "/video.mp4"
            };
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
