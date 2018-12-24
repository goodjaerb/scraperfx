/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import com.goodjaerb.scraperfx.ScraperFX;
import com.goodjaerb.scraperfx.datasource.CustomHttpDataSource;
import com.goodjaerb.scraperfx.datasource.impl.screenscraper.ScreenScraperSystemIdMap;
import com.goodjaerb.scraperfx.datasource.impl.screenscraper.ScreenScraperXmlGameData;
import com.goodjaerb.scraperfx.datasource.plugin.XmlDataSourcePlugin;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.MetaData;
import java.io.IOException;
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
public class ScreenScraperSource extends CustomHttpDataSource {
    private static final String                 API_BASE_URL = "https://www.screenscraper.fr/api/jeuInfos.php";
    private static final Map<String, String>    DEFAULT_PARAMS;
    
    static {
        final Map<String, String> initialParams = new HashMap<>();
        initialParams.put("devid", ScraperFX.getKeysValue("ScreenScraper.ID"));
        initialParams.put("devpassword", ScraperFX.getKeysValue("ScreenScraper.KEY"));
        initialParams.put("softname", "scraperfx");
        initialParams.put("output", "xml");
        
        DEFAULT_PARAMS = Collections.unmodifiableMap(initialParams);
    }
    
    @Override
    public String getSourceName() {
        return "Screen Scraper (screenscraper.fr)";
    }
    
    private ScreenScraperXmlGameData getXmlData(String systemName, String gameName) {
        final Integer sysId = ScreenScraperSystemIdMap.getSystemId(systemName);
        if(sysId == null) {
            return null;
        }

        final Map<String, String> params = new HashMap<>(DEFAULT_PARAMS);
        params.put("systemid", Integer.toString(sysId));
        params.put("romnom", gameName);
        
        try {
            return getData(new XmlDataSourcePlugin<>(ScreenScraperXmlGameData.class), API_BASE_URL, params);
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
