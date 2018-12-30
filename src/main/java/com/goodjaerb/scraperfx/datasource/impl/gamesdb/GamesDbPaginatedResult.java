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
public class GamesDbPaginatedResult<T extends GamesDbPaginatedData<?>> extends GamesDbResult<T> {
    @Expose(serialize = false, deserialize = true) public Pages pages;
    
    public static class Pages {
        @Expose(serialize = false, deserialize = true) public String previous;
        @Expose(serialize = false, deserialize = true) public String current;
        @Expose(serialize = false, deserialize = true) public String next;
        
        public Pages() {
            
        }

        @Override
        public String toString() {
            return "Pages{" + "previous=" + previous + ", current=" + current + ", next=" + next + '}';
        }
    }
    
    public GamesDbPaginatedResult() {
        
    }

    @Override
    public String toString() {
        return "GamesDbPaginatedResult{" + "code=" + code + ", status=" + status + ", data=" + data + ", pages=" + pages + ", remaining_monthly_allowance=" + remaining_monthly_allowance + ", extra_allowance=" + extra_allowance + '}';
    }
    
    public boolean hasNext() {
        return pages != null && pages.next != null && !pages.next.isEmpty();
    }
}
