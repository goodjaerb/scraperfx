/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.data.json.gamesdb;

import com.google.gson.annotations.Expose;

/**
 *
 * @author goodjaerb
 * @param <T>
 */
public class GamesDbData<T> {
    
    @Expose(serialize = false, deserialize = true) public int count;
    
    public boolean isDataAvailable() {
        return false;
    }
}
