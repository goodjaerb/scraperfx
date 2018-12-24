/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.plugin;

import com.google.gson.Gson;
import java.io.BufferedReader;

/**
 *
 * @author goodjaerb
 * @param <T>
 */
public class JsonDataSourcePlugin<T> extends DataSourcePlugin<T> {
    
    public JsonDataSourcePlugin(Class<T> dataClass) {
        super(dataClass);
    }
    
    @Override
    public T convert(BufferedReader reader) {
        return new Gson().fromJson(reader, dataClass);
    }
}
