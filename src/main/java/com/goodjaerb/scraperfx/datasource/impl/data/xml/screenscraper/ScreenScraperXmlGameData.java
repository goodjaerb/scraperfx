/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.data.xml.screenscraper;

import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "Data")
public class ScreenScraperXmlGameData {
    
    @Element(name = "jeu")
    public ScreenScraperXmlGame game;

    @Override
    public String toString() {
        return "ScreenScraperXmlGameData{" + "game=" + game + '}';
    }

}
