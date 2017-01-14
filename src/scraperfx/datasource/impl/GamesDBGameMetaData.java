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
@RootElement(name = "Data")
public class GamesDBGameMetaData {
    
    @Element(name = "Game")
    public GamesDBGame game;
    
    public GamesDBGameMetaData() {
        
    }
    
    @Override
    public String toString() {
        return game.toString();
    }
    /*
    <Data>
    <baseImgUrl>http://thegamesdb.net/banners/</baseImgUrl>
    <Game>
    <id>2</id>
    <GameTitle>Crysis</GameTitle>
    <PlatformId>1</PlatformId>
    <Platform>PC</Platform>
    <ReleaseDate>11/13/2007</ReleaseDate>
    <Overview>
    From the makers of Far Cry, Crysis offers FPS fans the best-looking, most highly-evolving gameplay, requiring the player to use adaptive tactics and total customization of weapons and armor to survive in dynamic, hostile environments including Zero-G. Earth, 2019. A team of US scientists makes a frightening discovery on an island in the South China Sea. All contact with the team is lost when the North Korean Government quickly seals off the area. The United States responds by dispatching an elite team of Delta Force Operators to recon the situation. As tension rises between the two nations, a massive alien ship reveals itself in the middle of the island. The ship generates an immense force sphere that freezes a vast portion of the island and drastically alters the global weather system. Now the US and North Korea must join forces to battle the alien menace. With hope rapidly fading, you must fight epic battles through tropical jungle, frozen landscapes, and finally into the heart of the alien ship itself for the ultimate Zero G showdown.
    </Overview>
    <ESRB>M - Mature</ESRB>
    <Genres>
    <genre>Shooter</genre>
    </Genres>
    <Players>4+</Players>
    <Co-op>No</Co-op>
    <Youtube>http://www.youtube.com/watch?v=i3vO01xQ-DM</Youtube>
    <Publisher>Electronic Arts</Publisher>
    <Developer>Crytek</Developer>
    <Rating>7.3077</Rating>
    <Similar>
    <SimilarCount>2</SimilarCount>
    <Game>
    <id>15246</id>
    <PlatformId>15</PlatformId>
    </Game>
    <Game>
    <id>15225</id>
    <PlatformId>12</PlatformId>
    </Game>
    </Similar>
    <Images>
    <fanart>
    <original width="1920" height="1080">fanart/original/2-1.jpg</original>
    <thumb>fanart/thumb/2-1.jpg</thumb>
    </fanart>
    <fanart>
    <original width="1920" height="1080">fanart/original/2-2.jpg</original>
    <thumb>fanart/thumb/2-2.jpg</thumb>
    </fanart>
    <fanart>
    <original width="1920" height="1080">fanart/original/2-3.jpg</original>
    <thumb>fanart/thumb/2-3.jpg</thumb>
    </fanart>
    <fanart>
    <original width="1920" height="1080">fanart/original/2-4.jpg</original>
    <thumb>fanart/thumb/2-4.jpg</thumb>
    </fanart>
    <fanart>
    <original width="1920" height="1080">fanart/original/2-5.jpg</original>
    <thumb>fanart/thumb/2-5.jpg</thumb>
    </fanart>
    <fanart>
    <original width="1920" height="1080">fanart/original/2-6.jpg</original>
    <thumb>fanart/thumb/2-6.jpg</thumb>
    </fanart>
    <boxart side="back" width="1525" height="2162" thumb="boxart/thumb/original/back/2-1.jpg">boxart/original/back/2-1.jpg</boxart>
    <boxart side="front" width="1525" height="2160" thumb="boxart/thumb/original/front/2-1.jpg">boxart/original/front/2-1.jpg</boxart>
    <banner width="760" height="140">graphical/2-g2.jpg</banner>
    <banner width="760" height="140">graphical/2-g3.jpg</banner>
    <screenshot>
    <original width="1920" height="1080">screenshots/2-1.jpg</original>
    <thumb>screenshots/thumb/2-1.jpg</thumb>
    </screenshot>
    <clearlogo width="400" height="100">clearlogo/2.png</clearlogo>
    </Images>
    </Game>
    </Data>
    */
}
