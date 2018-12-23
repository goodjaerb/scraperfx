/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author goodjaerb
 */
public abstract class JsonDataSource extends HttpDataSource {
    
    protected <T> T getJson(Class<T> clazz, String url) throws IOException  {
        try(BufferedReader reader = getReader(url)) {
            if(reader != null) {
                return new Gson().fromJson(reader, clazz);
            }
        }
        return null;
    }
}
