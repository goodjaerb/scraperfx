/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.gamesdblegacy;

import java.util.ArrayList;
import java.util.List;
import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;
import com.goodjaerb.scraperfx.datasource.impl.GamesDbLegacySource;
import com.goodjaerb.scraperfx.settings.Image;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "Game")
public class GamesDBGame {
    @Element(name = "id")
    public Integer id;
    
    @Element(name = "GameTitle")
    public String title;
    
    @Element(name = "ReleaseDate")
    public String releaseDate;
    
    @Element(name = "Overview")
    public String overview;
    
    @Element(name = "Genres")
    public List<GamesDBGenre> genreList;
    
    @Element(name = "Publisher")
    public String publisher;
    
    @Element(name = "Developer")
    public String developer;
    
    @Element(name = "ESRB")
    public String rating;
    
    @Element(name = "Images")
    public GamesDBImages images;
    
    @Element(name = "Players")
    public String players;
    
    public GamesDBGame() {
        
    }
    
    @Override
    public String toString() {
        return "id=" + id + ", title=" + title + ", releasedate=" + releaseDate + ", overview=" + overview + ", genre=" + genreList + ", publisher=" + publisher + ", developer=" + developer + ", esrb=" + rating + ", players=" + players;
    }
    
    public String convertGenres() {
        if(genreList == null) {
            return null;
        }
        
        String genre = "";
        genre = genreList.stream().map((g) -> g.genre).reduce(genre, String::concat);
        return genre;
    }
    
    public List<Image> getImages() {
        List<Image> imglist = new ArrayList<>();
        if(images.logo != null) {
            imglist.add(new Image("logo", images.logo.width, images.logo.height, GamesDbLegacySource.IMAGE_BASE_URL + images.logo.path, true));
        }
        if(images.boxarts != null) {
            images.boxarts.stream().forEach((boxart) -> {
                imglist.add(new Image("box-" + boxart.side, boxart.width, boxart.height, GamesDbLegacySource.IMAGE_BASE_URL + boxart.path, true));
            });
        }
        if(images.fanarts != null) {
            int i = 0;
            for(final GamesDBFanArt fanart : images.fanarts) {
                i++;
                imglist.add(new Image("fanart", fanart.data.width, fanart.data.height, GamesDbLegacySource.IMAGE_BASE_URL + fanart.data.path, (i == 1)));
            }
        }
        if(images.screenshots != null) {
            int i = 0;
            for(final GamesDBScreenshot screenshot : images.screenshots) {
                i++;
                imglist.add(new Image("screenshot", screenshot.data.width, screenshot.data.height, GamesDbLegacySource.IMAGE_BASE_URL + screenshot.data.path, (i == 1)));
            }
        }
        return imglist;
    }
}
