/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraperfx.datasource.impl.gamesdb;

import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

/**
 *
 * @author goodjaerb
 * 
 */
@RootElement(name = "Data")
public class GamesDBPlatformList {
    /* A short example of the XML
    <Data>
        <basePlatformUrl>http://thegamesdb.net/platform/</basePlatformUrl>
        <Platforms>
            <Platform>
                <id>25</id>
                <name>3DO</name>
                <alias>3do</alias>
            </Platform>
            <Platform>
                <id>4944</id>
                <name>Acorn Archimedes</name>
                <alias>acorn-archimedes</alias>
            </Platform>
            <Platform>
                <id>4954</id>
                <name>Acorn Electron</name>
                <alias>acorn-electron</alias>
            </Platform>
        </Platforms>
    </Data>
    */
    
    @Element(name = "Platforms")
    public GamesDBPlatforms platforms;
    
    @Override
    public String toString() {
        return platforms.toString();
    }
}
