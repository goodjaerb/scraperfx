/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author goodjaerb
 */
public abstract class HttpDataSource implements DataSource {
    private static final String USER_AGENT_PROPERTY = "User-Agent";
    private static final String USER_AGENT_MOZILLA  = "Mozilla/5.0";

    private String encodeParam(String value) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        }
        catch(UnsupportedEncodingException e) {
            Logger.getLogger(HttpDataSource.class.getName()).log(Level.SEVERE, null, e);
        }
        return encoded;
    }

    protected BufferedReader getReader(String url) {
        return getReader(url, null, USER_AGENT_PROPERTY, USER_AGENT_MOZILLA);
    }

    BufferedReader getReader(String url, Map<String, String> params) {
        return getReader(url, params, USER_AGENT_PROPERTY, USER_AGENT_MOZILLA);
    }

    private BufferedReader getReader(String urlStr, Map<String, String> params, String... httpProps) {
        Logger.getLogger(HttpDataSource.class.getName()).log(Level.INFO, "url=" + urlStr + ", params=" + params + ", httpProps=" + httpProps);
        String encodedUrl = urlStr;
        if(params != null) {
            encodedUrl = params.keySet().stream().map(key -> key + "=" + encodeParam(params.get(key))).collect(Collectors.joining("&", urlStr + "?", ""));
        }
        Logger.getLogger(HttpDataSource.class.getName()).log(Level.INFO, encodedUrl);

        HttpURLConnection conn;
        int retryCount = 0;
        while(retryCount < 3) {
            try {
                Logger.getLogger(HttpDataSource.class.getName()).log(Level.INFO, "Connecting to ''{0}''.", encodedUrl);

                conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
//                conn.setConnectTimeout(3000);
//                conn.setReadTimeout(3000);
                conn.setRequestMethod("GET");
                if(httpProps != null && httpProps.length % 2 == 0) {
                    for(int i = 0; i < httpProps.length; i += 2) {
                        conn.setRequestProperty(httpProps[i], httpProps[i + 1]);
                    }
                }

//                int waitRetries = 0;
//                while(conn.getResponseCode() == 429 && waitRetries++ < 3) {
//                    Logger.getLogger(HttpDataSource.class.getName()).log(Level.WARNING, "Too many requests to {0}. Retrying in 10s.", encodedUrl);
//                    try {
//                        Thread.sleep(10000);
//                    }
//                    catch (InterruptedException ex) {
//                        Logger.getLogger(HttpDataSource.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    
//                    conn = (HttpURLConnection)new URL(encodedUrl).openConnection();
//                    conn.setConnectTimeout(3000);
//                    conn.setReadTimeout(3000);
//                    conn.setRequestMethod("GET");
//                    if(httpProps != null && httpProps.length % 2 == 0) {
//                        for(int i = 0; i < httpProps.length; i += 2) {
//                            conn.setRequestProperty(httpProps[i], httpProps[i + 1]);
//                        }
//                    }
//                }

                Logger.getLogger(HttpDataSource.class.getName()).log(Level.INFO, "{0} {1}", new Object[]{conn.getResponseCode(), conn.getResponseMessage()});
                return new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
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
