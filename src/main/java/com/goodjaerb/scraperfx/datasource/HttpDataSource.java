/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author goodjaerb
 */
public abstract class HttpDataSource implements DataSource {
    public static final String USER_AGENT_PROPERTY = "User-Agent";
    public static final String USER_AGENT_MOZILLA = "Mozilla/5.0";
    
    BufferedReader getReader(String url) {
        return getReader(url, USER_AGENT_PROPERTY, USER_AGENT_MOZILLA);
    }
    
    BufferedReader getReader(String url, String... httpProps) {
        HttpURLConnection conn;
        int retryCount = 0;
        while(retryCount < 3) {
            try {
                Logger.getLogger(HttpDataSource.class.getName()).log(Level.INFO, "Connecting to ''{0}''.", url);
                
                conn = (HttpURLConnection)new URL(url).openConnection();
                conn.setConnectTimeout(1500);
                conn.setReadTimeout(1500);
                conn.setRequestMethod("GET");
                if(httpProps != null && httpProps.length % 2 == 0) {
                    for(int i = 0; i < httpProps.length; i += 2) {
                        conn.setRequestProperty(httpProps[i], httpProps[i + 1]);
                    }
                }

                return new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            catch(SocketTimeoutException ex) {
                if(++retryCount < 3) {
                    Logger.getLogger(HttpDataSource.class.getName()).log(Level.WARNING, "Connection error with {0}. Retrying (" + retryCount + ")...", getSourceName());
                }
                else {
                    Logger.getLogger(HttpDataSource.class.getName()).log(Level.WARNING, "Unable to connect to {0}.", getSourceName());
                }
            }
            catch(IOException ex) {
                Logger.getLogger(HttpDataSource.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
        
        return null;
    }
}
