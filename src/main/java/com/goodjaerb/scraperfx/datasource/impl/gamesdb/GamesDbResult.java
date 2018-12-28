/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.gamesdb;

import com.google.gson.annotations.Expose;

/**
 *
 * @author goodjaerb
 * @param <T>
 */
public class GamesDbResult<T extends GamesDbData<?>> {
    @Expose(serialize = false, deserialize = true)  public int code;
    @Expose(serialize = false, deserialize = true)  public String status;
    @Expose(serialize = true, deserialize = true)   public T data;
    @Expose(serialize = false, deserialize = true)  public int remaining_monthly_allowance;
    @Expose(serialize = false, deserialize = true)  public int extra_allowance;
    
    public GamesDbResult() {
        
    }
    
    public boolean isDataAvailable() {
        return data != null && data.isDataAvailable();
    }

    @Override
    public String toString() {
        return "GamesDbResult{" + "code=" + code + ", status=" + status + ", data=" + data + ", remaining_monthly_allowance=" + remaining_monthly_allowance + ", extra_allowance=" + extra_allowance + '}';
    }
}
