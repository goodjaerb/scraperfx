/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.settings;

import java.util.List;
import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

/**
 *
 * @author goodjaerb
 */
@RootElement(name = "metadata")
public class MetaData {
    public enum MetaDataId { NAME, DESC, RATING, RELEASE_DATE, DEVELOPER, PUBLISHER, GENRE, PLAYERS, IMAGES };
    
    @Element(name = "name")
    public String metaName;
    
    @Element(name = "lockName")
    public Boolean lockName = false;
    
    @Element(name = "description")
    public String metaDesc;
    
    @Element(name = "lockDesc")
    public Boolean lockDesc = false;
    
    @Element(name = "image")
    public List<Image> images;
    
    @Element(name = "lockImages")
    public Boolean lockImages = false;
    
    @Element(name = "youtubeurl")
    public String youtubeembed;
    
    @Element(name = "videodownload")
    public String videodownload;
    
    @Element(name = "rating")
    public String metaRating;
    
    @Element(name = "lockRating")
    public Boolean lockRating = false;
    
    @Element(name = "releasedate")
    public String metaReleaseDate;
    
    @Element(name = "lockReleasedata")
    public Boolean lockReleasedate = false;
    
    @Element(name = "developer")
    public String metaDeveloper;
    
    @Element(name = "lockDeveloper")
    public Boolean lockDeveloper = false;
    
    @Element(name = "publisher")
    public String metaPublisher;
    
    @Element(name = "lockPublisher")
    public Boolean lockPublisher = false;
    
    @Element(name = "genre")
    public String metaGenre;
    
    @Element(name = "lockGenre")
    public Boolean lockGenre = false;
    
    @Element(name = "players")
    public String players;
    
    @Element(name = "lockPlayers")
    public Boolean lockPlayers = false;
    
    public MetaData() {
    }
    
    public String getSelectedImageUrl(String type) {
        for(Image image : images) {
            if(type.equals(image.type) && image.selected) {
                return image.url;
            }
        }
        return null;
    }
    
    public String getSelectedImageType(String type) {
        for(Image image : images) {
            if(type.equals(image.type) && image.selected) {
                return image.targetImageType;
            }
        }
        return null;
    }
    
    public void selectImage(Image image) {
        images.stream().filter((i) -> (i.type.equals(image.type))).forEach((i) -> {
            i.selected = i.url.equals(image.url);
        });
    }
    
    public void transferLocksFrom(MetaData other) {
        this.lockDesc = other.lockDesc;
        this.lockDeveloper = other.lockDeveloper;
        this.lockGenre = other.lockGenre;
        this.lockImages = other.lockImages;
        this.lockName = other.lockName;
        this.lockPlayers = other.lockPlayers;
        this.lockPublisher = other.lockPublisher;
        this.lockRating = other.lockRating;
        this.lockReleasedate = other.lockReleasedate;
    }
    
    public void updateMetaData(MetaDataId id, String value) {
        switch(id) {
            case NAME:
                metaName = value;
                break;
            case DESC:
                metaDesc = value;
                break;
            case DEVELOPER:
                metaDeveloper = value;
                break;
            case GENRE:
                metaGenre = value;
                break;
            case PLAYERS:
                players = value;
                break;
            case PUBLISHER:
                metaPublisher = value;
                break;
            case RATING:
                metaRating = value;
                break;
            case RELEASE_DATE:
                metaReleaseDate = value;
                break;
        }
    }
    
    public void lockMetaData(MetaDataId id, Boolean lock) {
        switch(id) {
            case NAME:
                lockName = lock;
                break;
            case DESC:
                lockDesc = lock;
                break;
            case DEVELOPER:
                lockDeveloper = lock;
                break;
            case GENRE:
                lockGenre = lock;
                break;
            case PLAYERS:
                lockPlayers = lock;
                break;
            case PUBLISHER:
                lockPublisher = lock;
                break;
            case RATING:
                lockRating = lock;
                break;
            case RELEASE_DATE:
                lockReleasedate = lock;
                break;
            case IMAGES:
                lockImages = lock;
                break;
        }
    }
    
    @Override
    public String toString() {
        return "MetaData(name=" + metaName + ", description=" + metaDesc + 
                ", images(" + images + "), rating=" + metaRating + ", releaseData=" + metaReleaseDate + ", developer=" + metaDeveloper + ", publisher= " +
                metaPublisher + ", genre=" + metaGenre + ", players=" + players + ")";
    }
}
