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
import com.goodjaerb.scraperfx.datasource.impl.screenscraper.ScreenScraperXmlGameMedias;
import com.goodjaerb.scraperfx.datasource.plugin.XmlDataSourcePlugin;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.MetaData;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

/**
 *
 * @author goodjaerb
 */
public class ScreenScraperSource extends CustomHttpDataSource {
    private static final String                 API_BASE_URL = "https://www.screenscraper.fr/api/jeuInfos.php";
    private static final Map<String, String>    DEFAULT_PARAMS;
    
    public enum MetaDataKey {
        ID,
        VIDEO_DOWNLOAD,
        VIDEO_EMBED,
        BOX_US,
        BOX_WORLD,
        SCREENSHOT;
    }
    
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
    
    private String crcCalc(Path filePath) throws IOException {
        try(final InputStream inputStream = new BufferedInputStream(Files.newInputStream(filePath))) {
            final CRC32 crc = new CRC32();

            int cnt;
            while ((cnt = inputStream.read()) != -1) {
                crc.update(cnt);
            }
            String crcString = Long.toHexString(crc.getValue());
            while(crcString.length() < 8) {
                crcString = "0" + crcString;
            }
            return crcString;
        }
    }
    
    private ScreenScraperXmlGameData getXmlData(String systemName, String gameName, Path filePath) {
        final Integer sysId = ScreenScraperSystemIdMap.getSystemId(systemName);
        if(sysId == null) {
            return null;
        }

        final Map<String, String> params = new HashMap<>(DEFAULT_PARAMS);
        params.put("systemid", Integer.toString(sysId));
        params.put("romnom", gameName);
        try {
            params.put("crc", crcCalc(filePath));
        }
        catch (IOException ex) {
            Logger.getLogger(ScreenScraperSource.class.getName()).log(Level.WARNING, "Could not calculate CRC of file to assist in identifying on www.screenscraper.fr.", ex);
        }
        
        try {
            return getData(new XmlDataSourcePlugin<>(ScreenScraperXmlGameData.class), API_BASE_URL, params);
        }
        catch (IOException ex) {
            Logger.getLogger(ScreenScraperSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private ScreenScraperXmlGameData getXmlData(String gameId) {
        final Map<String, String> params = new HashMap<>(DEFAULT_PARAMS);
        params.put("gameid", gameId);
        
        try {
            return getData(new XmlDataSourcePlugin<>(ScreenScraperXmlGameData.class), API_BASE_URL, params);
        }
        catch(IOException ex) {
            Logger.getLogger(ScreenScraperSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private ScreenScraperXmlGameData getXmlData(String systemName, Game game, Path filePath) {
        if(game.metadata != null && game.metadata.screenScraperId != null && !game.metadata.screenScraperId.isEmpty()) {
            return getXmlData(game.metadata.screenScraperId);
        }
        return getXmlData(systemName, game.matchedName, filePath);
    }
    
    public Map<MetaDataKey, String> getExtraMetaData(String systemName, Game game, Path filePath) {
        final ScreenScraperXmlGameData data = getXmlData(systemName, game, filePath);
        if(data != null && data.game != null) {
            final Map<MetaDataKey, String> metaDataMap = new HashMap<>();
            
            metaDataMap.put(MetaDataKey.ID, Integer.toString(data.game.id));
            
            if(data.game.medias != null) {
                final ScreenScraperXmlGameMedias medias = data.game.medias;
                if(medias.videoDownloadUrl != null) {
                    metaDataMap.put(MetaDataKey.VIDEO_DOWNLOAD, medias.videoDownloadUrl);
                    metaDataMap.put(MetaDataKey.VIDEO_EMBED, "https://www.screenscraper.fr/medias/" + ScreenScraperSystemIdMap.getSystemId(systemName) + "/" + data.game.id + "/video.mp4");
    //            return new String[] { 
    //                data.game.medias.videoDownloadUrl, 
    //                "https://www.screenscraper.fr/medias/" + ScreenScraperSystemIdMap.getSystemId(systemName) + "/" + data.game.id + "/video.mp4"
    //            };
                }

                if(medias.screenshotUrl != null) {
                    metaDataMap.put(MetaDataKey.SCREENSHOT, medias.screenshotUrl);
                }

                if(medias.boxes != null && medias.boxes.boxes2d != null && medias.boxes.boxes2d.boxUsUrl != null) {
                    metaDataMap.put(MetaDataKey.BOX_US, medias.boxes.boxes2d.boxUsUrl);
                }

                if(medias.boxes != null && medias.boxes.boxes2d != null && medias.boxes.boxes2d.boxWorUrl != null) {
                    metaDataMap.put(MetaDataKey.BOX_WORLD, medias.boxes.boxes2d.boxWorUrl);
                }
            }
            
            return metaDataMap;
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
