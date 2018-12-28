/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.plugin;

import java.io.BufferedReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmappr.Xmappr;
import org.xmappr.XmapprException;

/**
 *
 * @author goodjaerb
 * @param <T>
 */
public class XmlDataSourcePlugin<T> implements DataSourcePlugin<T> {
    private final Class<T> dataClass;
    
    public XmlDataSourcePlugin(Class<T> dataClass) {
        this.dataClass = dataClass;
    }
    
    @Override
    public T convert(BufferedReader reader) {
        try {
            return dataClass.cast(new Xmappr(dataClass).fromXML(reader));
        }
        catch(XmapprException ex) {
            Logger.getLogger(XmlDataSourcePlugin.class.getName()).log(Level.WARNING, "Unable to parse XML.", ex);
        }
        return null;
    }
}