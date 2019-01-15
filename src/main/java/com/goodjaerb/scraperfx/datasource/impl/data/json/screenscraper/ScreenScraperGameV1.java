/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.data.json.screenscraper;

/**
 *
 * @author goodjaerb
 */
public class ScreenScraperGameV1 {
    public String id;
    public String nom;
    public NameData noms;
    public String systemeid;
    public String systemenom;
    public Medias medias;
    
    public static class NameData {
        public String nom_ss;
        public String nom_us;
        
        public NameData() {
            
        }
    }
    
    public static class Medias {
        public String media_screenshot;
        public String media_video;
        public MediaBoxs media_boxs;
    }
    
    public static class MediaBoxs {
        public String media_box2d_us;
    }

    public ScreenScraperGameV1() {

    }
    
//    @Override
//    public String toString() {
//        if(noms == null || noms.isEmpty()) {
//            return id + " (" + systemenom + ")";
//        }
//        return noms.get(0).text + " (" + systemenom + ")";
//    }
}
