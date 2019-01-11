/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.data.xml.gamesdblegacy;

import java.util.List;
import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "Data")
public class GamesDBGameListData {
    /*
    <Data>
        <Game>
            <id>1</id>
            <GameTitle>Halo: Combat Evolved</GameTitle>
            <ReleaseDate>11/15/2001</ReleaseDate>
        </Game>
        <Game>
            <id>2</id>
            <GameTitle>Crysis</GameTitle>
            <ReleaseDate>11/13/2007</ReleaseDate>
        </Game>
        <Game>
            <id>9064</id>
            <GameTitle>Shank 2</GameTitle>
            <ReleaseDate>02/07/2012</ReleaseDate>
        </Game>
    </Data>
    */
    
    @Element(name = "Game")
    public List<GamesDBListGame> games;
}
