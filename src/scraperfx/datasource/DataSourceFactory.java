/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraperfx.datasource;

/**
 *
 * @author goodjaerb
 */
public class DataSourceFactory {
    public enum SourceAgent {
        THEGAMESDB("scraperfx.datasource.impl.GamesDBSource"),
        ARCADE("scraperfx.datasource.impl.ArcadeSource");
        
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
