/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraperfx.datasource.impl;

import java.util.ArrayList;
import java.util.List;
import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;
import scraperfx.settings.Image;

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
    
    protected String convertGenres() {
        if(genreList == null) {
            return null;
        }
        
        String genre = "";
        genre = genreList.stream().map((g) -> g.genre).reduce(genre, String::concat);
        return genre;
    }
    
//    protected String getEsImage() {
//        if(images.boxarts != null) {
//            for(GamesDBBoxArt ba : images.boxarts) {
//                if("front".equals(ba.side)) {
//                    return GamesDBSource.IMAGE_BASE_URL + ba.path;
//                }
//            }
//        }
//        else if(images.screenshots != null) {
//            return GamesDBSource.IMAGE_BASE_URL + images.screenshots.get(0).data.path;
//        }
//        return null;
//    }
    
    protected List<Image> getImages() {
        List<Image> imglist = new ArrayList();
        if(images.logo != null) {
            imglist.add(new Image("logo", images.logo.width, images.logo.height, GamesDBSource.IMAGE_BASE_URL + images.logo.path, true));
        }
        if(images.boxarts != null) {
            images.boxarts.stream().forEach((boxart) -> {
                imglist.add(new Image("box-" + boxart.side, boxart.width, boxart.height, GamesDBSource.IMAGE_BASE_URL + boxart.path, true));
            });
        }
        if(images.fanarts != null) {
            int i = 0;
            for(GamesDBFanArt fanart : images.fanarts) {
                i++;
                imglist.add(new Image("fanart", fanart.data.width, fanart.data.height, GamesDBSource.IMAGE_BASE_URL + fanart.data.path, (i == 1)));
            }
        }
        if(images.screenshots != null) {
            int i = 0;
            for(GamesDBScreenshot screenshot : images.screenshots) {
                i++;
                imglist.add(new Image("screenshot", screenshot.data.width, screenshot.data.height, GamesDBSource.IMAGE_BASE_URL + screenshot.data.path, (i == 1)));
            }
        }
        return imglist;
    }
}
