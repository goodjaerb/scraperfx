/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.plugin;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @param <T>
 * @author goodjaerb
 */
public class JsonDataSourcePlugin<T> implements DataSourcePlugin<T> {
    private final Type typeOfT;

    public JsonDataSourcePlugin(Type typeOfT) {
        this.typeOfT = typeOfT;
    }

    @Override
    public T convert(BufferedReader reader) {
        try {
            return new Gson().fromJson(reader, typeOfT);
        }
        catch(JsonParseException ex) {
            Logger.getLogger(JsonDataSourcePlugin.class.getName()).log(Level.WARNING, "Unable to parse JSON.", ex);
        }
        return null;
    }
}
