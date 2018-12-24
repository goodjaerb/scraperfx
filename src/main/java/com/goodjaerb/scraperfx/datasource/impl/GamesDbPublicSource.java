/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import com.goodjaerb.scraperfx.ScraperFX;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author goodjaerb <goodjaerb@gmail.com>
 */
public class GamesDbPublicSource extends GamesDbSourceBase {
    private static final Map<String, String> DEFAULT_PARAMS;
    
    static {
        final Map<String, String> initialParams = new HashMap<>();
        initialParams.put("apikey", ScraperFX.getKeysValue("GamesDb.Public"));
        
        DEFAULT_PARAMS = Collections.unmodifiableMap(initialParams);
    }
    
    @Override
    public Map<String, String> getDefaultParams() {
        return DEFAULT_PARAMS;
    }
}
