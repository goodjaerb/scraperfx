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
        THEGAMESDB("com.goodjaerb.scraperfx.datasource.impl.GamesDbPublicSource"),
        THEGAMESDB_PRIVATE("com.goodjaerb.scraperfx.datasource.impl.GamesDbPrivateSource"),
        THEGAMESDB_LEGACY("com.goodjaerb.scraperfx.datasource.impl.GamesDbLegacySource");
        
        private final String className;
        private DataSource dataSource;
        
        SourceAgent(String className) {
            this.className = className;
        }
        
        DataSource getDataSource() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
            if(dataSource == null) {
                final Class c = Class.forName(className);
                dataSource = (DataSource)c.newInstance();
            }
            return dataSource;
        }
        
        <T extends DataSource> T getDataSource(Class<T> clazz) throws InstantiationException, ClassNotFoundException, IllegalAccessException {
            if(dataSource == null) {
                final Class c = Class.forName(className);
                dataSource = (DataSource)c.newInstance();
            }
            return clazz.cast(dataSource);
        }
    }
    
    public static DataSource get(SourceAgent sa) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return sa.getDataSource();
    }
    
    public static <T extends DataSource> T get(SourceAgent sa, Class<T> clazz) throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        return sa.getDataSource(clazz);
    }
}
