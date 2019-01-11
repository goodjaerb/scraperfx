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
@RootElement(name = "media_boxs")
public class ScreenScraperXmlMediaBoxs {
    
    @Element(name = "media_boxs2d")
    public ScreenScraperXmlMediaBoxs2d boxes2d;

    @Override
    public String toString() {
        return "ScreenScraperXmlMediaBoxs{" + "boxes2d=" + boxes2d + '}';
    }
}
