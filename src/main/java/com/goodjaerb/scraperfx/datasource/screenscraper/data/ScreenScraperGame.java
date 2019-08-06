/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.screenscraper.data;

import java.util.ArrayList;
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
        
        public NameData(String region, String text) {
            this.region = region;
            this.text = text;
        }
    }
    
    public static class Media {
        public String type;
        public String url;
        public String region;
        
        public Media() {
            
        }
        
        public Media(String type, String url, String region) {
            this.type = type;
            this.url = url;
            this.region = region;
        }
        
        public Media(String type, String url) {
            this.type = type;
            this.url = url;
        }
    }

    public ScreenScraperGame() {

    }
    
    public ScreenScraperGame(ScreenScraperGameV1 game) {
        this.id = game.id;
        this.systemeid = game.systemeid;
        this.systemenom = game.systemenom;
        
        if(game.noms != null) {
            this.noms = new ArrayList<>();
            if(game.noms.nom_ss != null) {
                this.noms.add(new NameData("ss", game.noms.nom_ss));
            }
            if(game.noms.nom_us != null) {
                this.noms.add(new NameData("us", game.noms.nom_us));
            }
        }
        
        if(game.medias != null) {
            this.medias = new ArrayList<>();
            if(game.medias.media_video != null) {
                this.medias.add(new Media("video", game.medias.media_video));
            }
            if(game.medias.media_screenshot != null) {
                this.medias.add(new Media("ss", game.medias.media_screenshot));
            }
            if(game.medias.media_boxs != null && game.medias.media_boxs.media_box2d_us != null) {
                this.medias.add(new Media("box-2D", game.medias.media_boxs.media_box2d_us));
            }
        }
    }
    
    @Override
    public String toString() {
        if(noms == null || noms.isEmpty()) {
            return id + " (" + systemenom + ")";
        }
        String name = noms.get(0).text;
        for(NameData n : noms) {
            if("us".equals(n.region)) {
                name = n.text;
                break;
            }
        }
        return name + " (" + systemenom + ")";
//        return noms.get(0).text + " (" + systemenom + ")";
    }
}
