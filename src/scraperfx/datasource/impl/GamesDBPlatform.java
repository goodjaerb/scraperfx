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
@RootElement(name = "Platform")
public class GamesDBPlatform {
    
    @Element
    public Integer id;
    
    @Element
    public String name;
    
    @Element
    public String alias;
    
    @Override
    public String toString() {
        return "{ id=" + id.toString() + ", name=" + name + ", alias=" + alias + "}";
    }
}
