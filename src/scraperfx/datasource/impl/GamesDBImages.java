/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraperfx.datasource.impl;

import java.util.List;
import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "Images")
public class GamesDBImages {
    
    @Element(name = "fanart")
    public List<GamesDBFanArt> fanarts;
    
    @Element(name = "boxart")
    public List<GamesDBBoxArt> boxarts;
    
    @Element(name = "screenshot")
    public List<GamesDBScreenshot> screenshots;
    
    @Element(name = "clearlogo")
    public GamesDBClearLogo logo;
    
    public GamesDBImages() {
        
    }
}
