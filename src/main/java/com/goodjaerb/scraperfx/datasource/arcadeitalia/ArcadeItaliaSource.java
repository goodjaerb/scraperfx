/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.arcadeitalia;

import com.goodjaerb.scraperfx.datasource.CustomHttpDataSource;
import com.goodjaerb.scraperfx.datasource.plugin.JsonDataSourcePlugin;
import com.goodjaerb.scraperfx.datasource.arcadeitalia.data.ArcadeItaliaData;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.Image;
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
public class ArcadeItaliaSource extends CustomHttpDataSource {
//    private static final String HTTP_CONTENT_LANGUAGE_PROP = "Accept-Language";
//    private static final String HTTP_CONTENT_LANGUAGE_VALUE = "en";
    private static final String                 SOURCE_NAME = "Arcade Italia (adb.arcadeitalia.net)";
    private static final String                 API_URL = "http://adb.arcadeitalia.net/service_scraper.php";//?ajax=query_mame&lang=en&game_name=";
    private static final Map<String, String>    DEFAULT_PARAMS;
    private static final String                 GAME_NAME_PARAM = "game_name";
    
    static {
        final Map<String, String> initialMap = new HashMap<>();
        initialMap.put("ajax", "query_mame");
        initialMap.put("lang", "en");
        
        DEFAULT_PARAMS = Collections.unmodifiableMap(initialMap);
    }
    

    @Override
    public String getSourceName() {
        return SOURCE_NAME;
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
        try {
            final Map<String, String> params = new HashMap<>(DEFAULT_PARAMS);
            params.put(GAME_NAME_PARAM, game.matchedName);
            
            final ArcadeItaliaData data = getData(new JsonDataSourcePlugin<>(ArcadeItaliaData.class), API_URL, params);
            if(data != null && data.result != null && data.result.length > 0) {
                final MetaData metadata = new MetaData();
                metadata.metaName = data.result[0].title;
                metadata.players = String.valueOf(data.result[0].players);
                metadata.metaGenre = data.result[0].genre;
                metadata.metaReleaseDate = data.result[0].year;
                metadata.metaDeveloper = data.result[0].manufacturer;
                
                if(notNullNorEmpty(data.result[0].youtube_video_id)) {
                    metadata.videoembed = "https://www.youtube.com/embed/" + data.result[0].youtube_video_id;
                }
                if(notNullNorEmpty(data.result[0].url_video_shortplay)) {
                    metadata.videodownload = data.result[0].url_video_shortplay;
                }
                metadata.images = new ArrayList<>();
                if(notNullNorEmpty(data.result[0].url_image_title)) {
                    metadata.images.add(new Image("title", SOURCE_NAME, data.result[0].url_image_title, true));
                }
                if(notNullNorEmpty(data.result[0].url_image_ingame)) {
                    metadata.images.add(new Image("game", SOURCE_NAME, data.result[0].url_image_ingame, true));
                }
                if(notNullNorEmpty(data.result[0].url_image_marquee)) {
                    metadata.images.add(new Image("marquee", SOURCE_NAME, data.result[0].url_image_marquee, true));
                }
                if(notNullNorEmpty(data.result[0].url_image_flyer)) {
                    metadata.images.add(new Image("flyer", SOURCE_NAME, data.result[0].url_image_flyer, true));
                }
                return metadata;
            }
        }
        catch (IOException ex) {
            Logger.getLogger(ArcadeItaliaSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    private boolean notNullNorEmpty(String s) {
        return s != null && !s.isEmpty();
    }
}
