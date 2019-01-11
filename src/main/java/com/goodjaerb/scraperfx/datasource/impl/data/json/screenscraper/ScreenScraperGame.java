/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.data.json.screenscraper;

import java.util.List;

/**
 *
 * @author goodjaerb
 */
public class ScreenScraperGame {
    public String id;
    public List<NameData> noms;
    public String systemeid;
    public String systemenom;
    public List<Media> medias;
    
    public static class NameData {
        public String region;
        public String text;
        
        public NameData() {
            
        }
    }
    
    public static class Media {
        public String type;
        public String url;
        public String region;
        
        public Media() {
            
        }
    }

    public ScreenScraperGame() {

    }
    
    @Override
    public String toString() {
        if(noms.isEmpty()) {
            return id + " (" + systemenom + ")";
        }
        return noms.get(0).text + " (" + systemenom + ")";
    }
}
