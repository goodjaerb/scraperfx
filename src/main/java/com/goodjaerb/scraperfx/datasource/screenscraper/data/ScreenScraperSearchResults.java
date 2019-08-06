package com.goodjaerb.scraperfx.datasource.screenscraper.data;

import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author goodjaerb
 */
public class ScreenScraperSearchResults {
    public ScreenScraperResponse response;
    
    public static class ScreenScraperResponse {
        public List<ScreenScraperGame> jeux;
        
        public ScreenScraperResponse() {
            
        }
    }
    
    public ScreenScraperSearchResults() {
        
    }
}
