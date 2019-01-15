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
public class ScreenScraperInfoV1 {
    public ScreenScraperResponse response;
    
    public static class ScreenScraperResponse {
        public ScreenScraperGameV1 jeu;
        
        public ScreenScraperResponse() {
            
        }
    }
    
    public ScreenScraperInfoV1() {
        
    }
}
