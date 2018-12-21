/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmappr.Xmappr;
import org.xmappr.XmapprException;

/**
 *
 * @author goodjaerb
 */
public abstract class XmlDataSource extends HttpDataSource {
    
    protected <T> T getXml(Class<T> dataHolderClass, String url) throws IOException {
        try(BufferedReader reader = getReader(url)) {
            if(reader != null) {
                try {
                    return dataHolderClass.cast(new Xmappr(dataHolderClass).fromXML(reader));
                }
                catch(XmapprException ex) {
                    Logger.getLogger(XmlDataSource.class.getName()).log(Level.WARNING, "Unable to parse XML.", ex);
                }
            }
        }
        return null;
    }
}