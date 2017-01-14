/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraperfx.datasource.impl;

import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "Game")
public class GamesDBListGame {
    
    @Element
    public Integer id;
    
    @Element(name = "GameTitle")
    public String gameTitle;
    
    @Element(name = "ReleaseDate")
    public String releaseDate;
}
