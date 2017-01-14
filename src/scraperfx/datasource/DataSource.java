/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraperfx.datasource;

import java.util.List;
import scraperfx.settings.Game;
import scraperfx.settings.MetaData;

/**
 *
 * @author goodjaerb
 */
public interface DataSource {
    
    public List<String> getSystemNames();
    public List<String> getSystemGameNames(String systemName);
    public MetaData getMetaData(String systemName, Game game);
}
