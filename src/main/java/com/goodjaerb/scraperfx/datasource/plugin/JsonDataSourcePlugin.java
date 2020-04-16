/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.plugin;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.IOException;
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
        String json = "";
        try {
            String line;
            while((line = reader.readLine()) != null) {
                json += line + "\n";
            }
//            return new Gson().fromJson(reader, typeOfT);
            return new Gson().fromJson(json, typeOfT);
        }
        catch(JsonParseException | IOException ex) {
            Logger.getLogger(JsonDataSourcePlugin.class.getName()).log(Level.WARNING, "Unable to parse JSON.", ex);
            Logger.getLogger(JsonDataSourcePlugin.class.getName()).log(Level.WARNING, "JSON: " + json);
        }
        return null;
    }
}
