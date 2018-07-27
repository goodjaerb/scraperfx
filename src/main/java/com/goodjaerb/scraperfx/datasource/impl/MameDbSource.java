/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.goodjaerb.scraperfx.datasource.DataSource;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.Image;
import com.goodjaerb.scraperfx.settings.MetaData;
import java.io.FileNotFoundException;

/**
 *
 * @author goodjaerb
 */
public class MameDbSource implements DataSource {

    private static final String BASE_URL = "http://mamedb.blu-ferret.co.uk/";
    
    @Override
    public List<String> getSystemNames() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public List<String> getSystemGameNames(String systemName) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public MetaData getMetaData(String systemName, Game game) {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            final MetaData data = new MetaData();
            
            conn = (HttpURLConnection)new URL(BASE_URL + "game/" + game.matchedName).openConnection();
            
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            String result = "";
            String line;
            while((line = reader.readLine()) != null) {
                result += line;
            }

            Pattern p = Pattern.compile("<title>Game Details:  (.*) - mamedb.com</title>");
            Matcher m = p.matcher(result);
            if(!m.find()) {
                return null;
            }
            else {
                data.metaName = m.group(1).replace("&amp;", "&").replace("&#039;", "'");
                data.images = new ArrayList<>();
                
//                p = Pattern.compile("src_full=\"(http://adb.arcadeitalia.net/media/mame.current/decals/" + game.matchedName + "\\.png).*\"></img>");
                p = Pattern.compile("<img src='/(snap/.*\\.png)' alt='Snapshot:.*'/>");
                m = p.matcher(result);
                if(m.find()) {
                    data.images.add(new Image("game", BASE_URL + m.group(1), "png", true));
                }
                
//                p = Pattern.compile("src_full=\"(http://adb.arcadeitalia.net/media/mame.current/titles/" + game.matchedName + "\\.png).*\"></img>");
                p = Pattern.compile("<img src='/(titles/.*\\.png)' alt='Title:.*'/>");
                m = p.matcher(result);
                if(m.find()) {
                    data.images.add(new Image("title", BASE_URL + m.group(1), "png", true));
                }
                
//                p = Pattern.compile("src_full=\"(http://adb.arcadeitalia.net/media/mame.current/ingames/" + game.matchedName + "\\.png).*\"></img>");
//                m = p.matcher(result);
//                if(m.find()) {
//                    data.images.add(new Image("game", m.group(1), true));
//                }
//                
//                p = Pattern.compile("src_full=\"(http://adb.arcadeitalia.net/media/mame.current/marquees/" + game.matchedName + "\\.png).*\"></img>");
//                m = p.matcher(result);
//                if(m.find()) {
//                    data.images.add(new Image("marquee", m.group(1), true));
//                }
//                
//                p = Pattern.compile("src_full=\"(http://adb.arcadeitalia.net/media/mame.current/flyers/" + game.matchedName + "\\.png).*\"></img>");
//                m = p.matcher(result);
//                if(m.find()) {
//                    data.images.add(new Image("flyer", m.group(1), true));
//                }
            }
            
            return data;
        }
        catch(FileNotFoundException ex) {
            Logger.getLogger(MameDbSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(IOException ex) {
            Logger.getLogger(MameDbSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if(conn != null) {
                conn.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                }
                catch(IOException ex) {
                    Logger.getLogger(MameDbSource.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }
}
