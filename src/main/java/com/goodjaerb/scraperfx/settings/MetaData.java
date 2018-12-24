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
    public enum MetaDataId { NAME, DESC, RATING, RELEASE_DATE, DEVELOPER, PUBLISHER, GENRE, PLAYERS, VIDEO_EMBED, VIDEO_DOWNLOAD, IMAGES };
    
    @Element(name = "favorite")
    public Boolean favorite = false;
    
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
    public String videoembed;
    
    @Element(name = "lockyoutubeurl")
    public Boolean lockVideoEmbed = false;
    
    @Element(name = "videodownload")
    public String videodownload;
    
    @Element(name = "lockvideodownload")
    public Boolean lockVideoDownload = false;
    
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
        for(final Image image : images) {
            if(type.equals(image.type) && image.selected) {
                return image.url;
            }
        }
        return null;
    }
    
    public String getSelectedImageType(String type) {
        for(final Image image : images) {
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
        this.lockDesc           = other.lockDesc;
        this.lockDeveloper      = other.lockDeveloper;
        this.lockGenre          = other.lockGenre;
        this.lockImages         = other.lockImages;
        this.lockName           = other.lockName;
        this.lockPlayers        = other.lockPlayers;
        this.lockPublisher      = other.lockPublisher;
        this.lockRating         = other.lockRating;
        this.lockReleasedate    = other.lockReleasedate;
        this.lockVideoEmbed     = other.lockVideoEmbed;
        this.lockVideoDownload  = other.lockVideoDownload;
    }
    
    public void reset() {
        metaReleaseDate = null;
        metaRating      = null;
        metaPublisher   = null;
        metaName        = null;
        metaDeveloper   = null;
        metaDesc        = null;
        players         = null;
        metaGenre       = null;
        images          = null;
        videodownload   = null;
        videoembed      = null;
        favorite        = false;
        
        lockDesc            = false;
        lockDeveloper       = false;
        lockGenre           = false;
        lockImages          = false;
        lockName            = false;
        lockPlayers         = false;
        lockPublisher       = false;
        lockRating          = false;
        lockReleasedate     = false;
        lockVideoDownload   = false;
        lockVideoEmbed      = false;
    }
    
    public void setMetaData(MetaData other) {
        if(other != null) {
            metaReleaseDate    = other.metaReleaseDate;
            metaRating         = other.metaRating;
            metaPublisher      = other.metaPublisher;
            metaName           = other.metaName;
            metaDeveloper      = other.metaDeveloper;
            metaDesc           = other.metaDesc;
            players            = other.players;
            metaGenre          = other.metaGenre;
            images             = other.images;
            videodownload      = other.videodownload;
            videoembed         = other.videoembed;
            favorite           = other.favorite;
            transferLocksFrom(other);
        }
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
            case VIDEO_DOWNLOAD:
                videodownload = value;
                break;
            case VIDEO_EMBED:
                videoembed = value;
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
            case VIDEO_DOWNLOAD:
                lockVideoDownload = lock;
                break;
            case VIDEO_EMBED:
                lockVideoEmbed = lock;
                break;
        }
    }
    
//    @Override
//    public String toString() {
//        return "MetaData(name=" + metaName + ", description=" + metaDesc + 
//                ", images(" + images + "), rating=" + metaRating + ", releaseData=" + metaReleaseDate + ", developer=" + metaDeveloper + ", publisher= " +
//                metaPublisher + ", genre=" + metaGenre + ", players=" + players + ")";
//    }

    @Override
    public String toString() {
        return "MetaData{" + "favorite=" + favorite + ", metaName=" + metaName + ", lockName=" + lockName + ", metaDesc=" + metaDesc + ", lockDesc=" + lockDesc + ", images=" + images + ", lockImages=" + lockImages + ", videoembed=" + videoembed + ", lockVideoEmbed=" + lockVideoEmbed + ", videodownload=" + videodownload + ", lockVideoDownload=" + lockVideoDownload + ", metaRating=" + metaRating + ", lockRating=" + lockRating + ", metaReleaseDate=" + metaReleaseDate + ", lockReleasedate=" + lockReleasedate + ", metaDeveloper=" + metaDeveloper + ", lockDeveloper=" + lockDeveloper + ", metaPublisher=" + metaPublisher + ", lockPublisher=" + lockPublisher + ", metaGenre=" + metaGenre + ", lockGenre=" + lockGenre + ", players=" + players + ", lockPlayers=" + lockPlayers + '}';
    }
}
