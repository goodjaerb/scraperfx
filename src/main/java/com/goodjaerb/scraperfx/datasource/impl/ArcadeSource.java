///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.goodjaerb.scraperfx.datasource.impl;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import com.goodjaerb.scraperfx.datasource.DataSource;
//import com.goodjaerb.scraperfx.settings.Game;
//import com.goodjaerb.scraperfx.settings.Image;
//import com.goodjaerb.scraperfx.settings.MetaData;
//
///**
// *
// * @author goodjaerb
// */
//public class ArcadeSource implements DataSource {
//
//    @Override
//    public List<String> getSystemNames() {
//        throw new UnsupportedOperationException("Not supported.");
//    }
//
//    @Override
//    public List<String> getSystemGameNames(String systemName) {
//        throw new UnsupportedOperationException("Not supported.");
//    }
//
//    @Override
//    public MetaData getMetaData(String systemName, Game game) {
//        HttpURLConnection conn = null;
//        BufferedReader reader = null;
//        try {
//            MetaData data = new MetaData();
//            
//            conn = (HttpURLConnection)new URL("http://adb.arcadeitalia.net/dettaglio_mame.php?game_name=" + game.matchedName).openConnection();
//            
//            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            
//            String result = "";
//            String line = null;
//            while((line = reader.readLine()) != null) {
//                result += line;
//            }
//
//            Pattern p = Pattern.compile("<title>(.*) - .*</title>");
//            Matcher m = p.matcher(result);
//            if(!m.find()) {
//                return null;
//            }
//            else {
//                data.metaName = m.group(1).replace("&amp;", "&").replace("&#039;", "'");
//                data.images = new ArrayList<>();
//                
//                p = Pattern.compile("src_full=\"(http://adb.arcadeitalia.net/media/mame.current/decals/" + game.matchedName + "\\.png).*\"></img>");
//                m = p.matcher(result);
//                if(m.find()) {
//                    data.images.add(new Image("decal", m.group(1), true));
//                }
//                
//                p = Pattern.compile("src_full=\"(http://adb.arcadeitalia.net/media/mame.current/titles/" + game.matchedName + "\\.png).*\"></img>");
//                m = p.matcher(result);
//                if(m.find()) {
//                    data.images.add(new Image("title", m.group(1), true));
//                }
//                
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
//            }
//            
//            return data;
//        }
//        catch(IOException ex) {
//            Logger.getLogger(ArcadeSource.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        finally {
//            if(conn != null) {
//                conn.disconnect();
//            }
//            if(reader != null) {
//                try {
//                    reader.close();
//                }
//                catch(IOException ex) {
//                    Logger.getLogger(ArcadeSource.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//        
//        return null;
//    }
//    
//}
