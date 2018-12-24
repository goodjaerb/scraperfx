/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import com.goodjaerb.scraperfx.datasource.JsonDataSource;
import com.goodjaerb.scraperfx.datasource.impl.arcadeitalia.ArcadeItaliaData;
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
public class ArcadeItaliaSource extends JsonDataSource {
    private static final String                 API_URL = "http://adb.arcadeitalia.net/service_scraper.php";//?ajax=query_mame&lang=en&game_name=";
    private static final Map<String, String>    DEFAULT_PARAMS;
    private static final String                 GAME_NAME_PARAM = "game_name";
    
    static {
        final Map<String, String> initialMap = new HashMap<>();
        initialMap.put("ajax", "query_mame");
        initialMap.put("lang", "en");
        
        DEFAULT_PARAMS = Collections.unmodifiableMap(initialMap);
    }
//    private static final String PROP_CONTENT_LANGUAGE = "Accept-Language";
//    private static final String VAL_CONTENT_LANGUAGE = "en";
    

    @Override
    public String getSourceName() {
        return "Arcade Italia (adb.arcadeitalia.net)";
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
            
            final ArcadeItaliaData data = getJson(ArcadeItaliaData.class, API_URL, params);
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
                    metadata.images.add(new Image("title", data.result[0].url_image_title, "png", true));
                }
                if(notNullNorEmpty(data.result[0].url_image_ingame)) {
                    metadata.images.add(new Image("game", data.result[0].url_image_ingame, "png", true));
                }
                if(notNullNorEmpty(data.result[0].url_image_marquee)) {
                    metadata.images.add(new Image("marquee", data.result[0].url_image_marquee, "png", true));
                }
                if(notNullNorEmpty(data.result[0].url_image_flyer)) {
                    metadata.images.add(new Image("flyer", data.result[0].url_image_flyer, "png", true));
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
