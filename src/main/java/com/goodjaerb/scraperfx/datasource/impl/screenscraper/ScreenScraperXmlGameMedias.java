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
@RootElement(name = "medias")
public class ScreenScraperXmlGameMedias {
    
    @Element(name = "media_video")
    public String videoDownloadUrl;

    @Override
    public String toString() {
        return "ScreenScraperXmlGameMedias{" + "videoDownloadUrl=" + videoDownloadUrl + '}';
    }
    
}
