/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource;

import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.MetaData;

import java.util.List;

/**
 * @author goodjaerb
 */
public interface DataSource {

    String getSourceName();
    List<String> getSystemNames();
    List<String> getSystemGameNames(String systemName);
    MetaData getMetaData(String systemName, Game game);
}
