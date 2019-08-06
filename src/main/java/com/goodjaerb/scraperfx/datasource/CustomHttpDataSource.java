/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource;

import com.goodjaerb.scraperfx.datasource.plugin.DataSourcePlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * @author goodjaerb <goodjaerb@gmail.com>
 */
public abstract class CustomHttpDataSource extends HttpDataSource {

    protected <T> T getData(DataSourcePlugin<T> plugin, String url, Map<String, String> params) throws IOException {
        try(final BufferedReader reader = getReader(url, params)) {
            if(reader != null) {
                return plugin.convert(reader);
            }
        }
        return null;
    }

    protected <T> T getData(DataSourcePlugin<T> plugin, String url) throws IOException {
        return getData(plugin, url, null);
    }
}
