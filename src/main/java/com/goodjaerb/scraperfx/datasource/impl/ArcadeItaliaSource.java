/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import com.goodjaerb.scraperfx.datasource.DataSource;
import com.goodjaerb.scraperfx.datasource.impl.arcadeitalia.ArcadeItaliaData;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.Image;
import com.goodjaerb.scraperfx.settings.MetaData;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author goodjaerb
 */
public class ArcadeItaliaSource implements DataSource {
    private static final String API_URL = "http://adb.arcadeitalia.net/service_scraper.php?ajax=query_mame&lang=en&game_name=";
    private static final String PROP_USER_AGENT = "User-Agent";
    private static final String VAL_USER_AGENT = "Mozilla/5.0";
    
//    private static final String PROP_CONTENT_LANGUAGE = "Accept-Language";
//    private static final String VAL_CONTENT_LANGUAGE = "en";

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
        Gson gson = new Gson();
        try {
            ArcadeItaliaData data = gson.fromJson(getJson(game.matchedName), ArcadeItaliaData.class);

            if(data != null && data.result != null && data.result.length > 0) {
                MetaData metadata = new MetaData();
                metadata.metaName = data.result[0].title;
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
        catch(JsonSyntaxException ex) {
            Logger.getLogger(ArcadeItaliaSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    private boolean notNullNorEmpty(String s) {
        return s != null && !s.isEmpty();
    }
     
    public String getJson(String gameName) {
        HttpURLConnection conn;
        int retryCount = 0;
        while(retryCount < 3) {
            try {
                String url = API_URL + gameName;
                
                conn = (HttpURLConnection)new URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty(PROP_USER_AGENT, VAL_USER_AGENT);
//                conn.setRequestProperty(PROP_CONTENT_LANGUAGE, VAL_CONTENT_LANGUAGE);

                return new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine();
            }
            catch(MalformedURLException ex) {
                Logger.getLogger(ArcadeItaliaSource.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
            catch(IOException ex) {
                if(++retryCount < 3) {
                    System.out.println("Connection error with thegamesdb.net. Retrying...");
                }
            }
        }
        
        return null;
    }
}
