/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.plugin;

import java.io.BufferedReader;

/**
 *
 * @author goodjaerb <goodjaerb@gmail.com>
 * @param <T>
 */
public abstract class DataSourcePlugin<T> {
    protected final Class<T> dataClass;
    
    public DataSourcePlugin(Class<T> dataClass) {
        this.dataClass = dataClass;
    }
    
    public abstract T convert(BufferedReader reader);
}
