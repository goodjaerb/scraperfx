/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author goodjaerb
 */
public class DataSourceFactory {
    public enum SourceAgent {
        ARCADE_ITALIA("com.goodjaerb.scraperfx.datasource.arcadeitalia.ArcadeItaliaSource"),
        MAMEDB("com.goodjaerb.scraperfx.datasource.mamedb.MameDbSource"),
        SCREEN_SCRAPER("com.goodjaerb.scraperfx.datasource.screenscraper.ScreenScraper2Source"),
        THEGAMESDB("com.goodjaerb.scraperfx.datasource.gamesdb.GamesDbPublicSource"),
        THEGAMESDB_PRIVATE("com.goodjaerb.scraperfx.datasource.gamesdb.GamesDbPrivateSource"),
        ;
        
        private final String className;
        private DataSource dataSource;
        
        SourceAgent(String className) {
            this.className = className;
        }
        
        DataSource getDataSource() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
            if(dataSource == null) {
                final Class<?> c = Class.forName(className);
                try {
                    dataSource = (DataSource)c.getDeclaredConstructor().newInstance();
                }
                catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }
            return dataSource;
        }
        
        <T extends DataSource> T getDataSource(Class<T> clazz) throws InstantiationException, ClassNotFoundException, IllegalAccessException {
            if(dataSource == null) {
                final Class<?> c = Class.forName(className);
                try {
                    dataSource = (DataSource)c.getDeclaredConstructor().newInstance();
                }
                catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(DataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
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
