/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource;

import java.util.List;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.MetaData;

/**
 *
 * @author goodjaerb
 */
public interface DataSource {
    
    public String getSourceName();
    public List<String> getSystemNames();
    public List<String> getSystemGameNames(String systemName);
    public MetaData getMetaData(String systemName, Game game);
    
    public default String[] getVideoLinks(String systemName, Game game) {
        return null;
    }
}
