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
@RootElement(name = "media_boxs2d")
public class ScreenScraperXmlMediaBoxs2d {
    
    @Element(name = "media_box2d_ss")
    public String boxUrl;

    @Override
    public String toString() {
        return "ScreenScraperXmlMediaBoxs2d{" + "boxUrl=" + boxUrl + '}';
    }
}
