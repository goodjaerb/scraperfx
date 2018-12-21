/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource;

/**
 *
 * @author goodjaerb
 */
public class DataSourceFactory {
    public enum SourceAgent {
        ARCADE_ITALIA("com.goodjaerb.scraperfx.datasource.impl.ArcadeItaliaSource"),
        MAMEDB("com.goodjaerb.scraperfx.datasource.impl.MameDbSource"),
        SCREEN_SCRAPER("com.goodjaerb.scraperfx.datasource.impl.ScreenScraperSource"),
//        THEGAMESDB("com.goodjaerb.scraperfx.datasource.impl.GamesDbSource"),
        THEGAMESDB_LEGACY("com.goodjaerb.scraperfx.datasource.impl.GamesDbLegacySource");
        
        private final String className;
        private DataSource dataSource;
        
        SourceAgent(String className) {
            this.className = className;
        }
        
        DataSource getDataSource() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
            if(dataSource == null) {
                Class c = Class.forName(className);
                dataSource = (DataSource)c.newInstance();
            }
            return dataSource;
        }
    }
    
    public static DataSource getDataSource(SourceAgent sa) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return sa.getDataSource();
    }
}
