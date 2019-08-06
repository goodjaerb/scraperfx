/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.screenscraper.data;

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

        @Override
        public String toString() {
            return "NameData{" + "nom_ss=" + nom_ss + ", nom_us=" + nom_us + '}';
        }
    }
    
    public static class Medias {
        public String media_screenshot;
        public String media_video;
        public MediaBoxs media_boxs;

        @Override
        public String toString() {
            return "Medias{" + "media_screenshot=" + media_screenshot + ", media_video=" + media_video + ", media_boxs=" + media_boxs + '}';
        }
    }
    
    public static class MediaBoxs {
        public String media_box2d_us;
    }

    public ScreenScraperGameV1() {

    }

    @Override
    public String toString() {
        return "ScreenScraperGameV1{" + "id=" + id + ", nom=" + nom + ", noms=" + noms + ", systemeid=" + systemeid + ", systemenom=" + systemenom + ", medias=" + medias + '}';
    }
}
