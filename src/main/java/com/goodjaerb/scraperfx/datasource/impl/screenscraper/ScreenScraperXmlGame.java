/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.screenscraper;

import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "jeu")
public class ScreenScraperXmlGame {
    
    @Element(name = "id")
    public int id;
    
    @Element(name = "medias")
    public ScreenScraperXmlGameMedias medias;

    @Override
    public String toString() {
        return "ScreenScraperXmlGame{" + "id=" + id + ", medias=" + medias + '}';
    }

}
