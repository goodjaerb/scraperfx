/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.plugin;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.lang.reflect.Type;

/**
 *
 * @author goodjaerb
 * @param <T>
 */
public class JsonDataSourcePlugin<T> implements DataSourcePlugin<T> {
    private final Type typeOfT;
    
    public JsonDataSourcePlugin(Type typeOfT) {
        this.typeOfT = typeOfT;
    }
    
    @Override
    public T convert(BufferedReader reader) {
        return new Gson().fromJson(reader, typeOfT);
    }
}
