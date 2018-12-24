/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.goodjaerb.scraperfx.datasource.HttpDataSource;
import com.goodjaerb.scraperfx.settings.Game;
import com.goodjaerb.scraperfx.settings.Image;
import com.goodjaerb.scraperfx.settings.MetaData;

/**
 *
 * @author goodjaerb
 */
public class MameDbSource extends HttpDataSource {

    private static final String BASE_URL = "http://mamedb.blu-ferret.co.uk/";
    
    @Override
    public String getSourceName() {
        return "MameDB (mamedb.org)";
    }
    
    @Override
    public List<String> getSystemNames() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public List<String> getSystemGameNames(String systemName) {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    @Override
    public MetaData getMetaData(String systemName, Game game) {
        final MetaData data = new MetaData();

        final String url = BASE_URL + "game/" + game.matchedName;
        
        try(final BufferedReader reader = getReader(url)) {
            if(reader != null) {
                String result = "";
                String line;
                while((line = reader.readLine()) != null) {
                    result += line;
                }

                Pattern p = Pattern.compile("<title>Game Details:  (.*) - mamedb.com</title>");
                Matcher m = p.matcher(result);
                if(!m.find()) {
                    return null;
                }
                else {
                    data.metaName = m.group(1).replace("&amp;", "&").replace("&#039;", "'");
                    data.images = new ArrayList<>();

                    p = Pattern.compile("<img src='/(snap/.*\\.png)' alt='Snapshot:.*'/>");
                    m = p.matcher(result);
                    if(m.find()) {
                        data.images.add(new Image("game", BASE_URL + m.group(1), "png", true));
                    }

                    p = Pattern.compile("<img src='/(titles/.*\\.png)' alt='Title:.*'/>");
                    m = p.matcher(result);
                    if(m.find()) {
                        data.images.add(new Image("title", BASE_URL + m.group(1), "png", true));
                    }
                }

                return data;
            }
        }
        catch (IOException ex) {
            Logger.getLogger(MameDbSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
