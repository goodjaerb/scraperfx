/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.settings;

import java.util.Iterator;
import java.util.List;

/**
 * @author goodjaerb
 */
public class MetaData {
    public enum MetaDataId {NAME, SORTNAME, DESC, RATING, RELEASE_DATE, DEVELOPER, PUBLISHER, GENRE, PLAYERS, SCREEN_SCRAPER_ID, VIDEO_EMBED, VIDEO_DOWNLOAD, IMAGES}

    ;

    public Boolean     favorite            = false;
    public String      metaName;
    public Boolean     lockName            = false;
    public String      metaSortName;
    public Boolean     lockSortName        = false;
    public String      metaDesc;
    public Boolean     lockDesc            = false;
    public List<Image> images;
    public Boolean     lockImages          = false;
    public String      screenScraperId;
    public Boolean     lockScreenScraperId = false;
    public String      videoembed;
    public Boolean     lockVideoEmbed      = false;
    public String      videodownload;
    public Boolean     lockVideoDownload   = false;
    public String      metaRating;
    public Boolean     lockRating          = false;
    public String      metaReleaseDate;
    public Boolean     lockReleasedate     = false;
    public String      metaDeveloper;
    public Boolean     lockDeveloper       = false;
    public String      metaPublisher;
    public Boolean     lockPublisher       = false;
    public String      metaGenre;
    public Boolean     lockGenre           = false;
    public String      players;
    public Boolean     lockPlayers         = false;

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

//    public String getSelectedImageType(String type) {
//        for(final Image image : images) {
//            if(type.equals(image.type) && image.selected) {
//                return image.targetImageType;
//            }
//        }
//        return null;
//    }

    public void selectImage(Image image) {
        images.stream().filter((i) -> (i.type.equals(image.type))).forEach((i) -> {
            i.selected = i.url.equals(image.url);
        });
    }

    public void removeImagesFromSource(String sourceName) {
        final Iterator<Image> i = images.iterator();
        while(i.hasNext()) {
            final Image image = i.next();
            if(sourceName.equals(image.source)) {
                i.remove();
            }
        }
    }

    public void transferLocksFrom(MetaData other) {
        this.lockDesc = other.lockDesc;
        this.lockDeveloper = other.lockDeveloper;
        this.lockGenre = other.lockGenre;
        this.lockImages = other.lockImages;
        this.lockName = other.lockName;
        this.lockSortName = other.lockSortName;
        this.lockPlayers = other.lockPlayers;
        this.lockPublisher = other.lockPublisher;
        this.lockRating = other.lockRating;
        this.lockReleasedate = other.lockReleasedate;
        this.lockScreenScraperId = other.lockScreenScraperId;
        this.lockVideoEmbed = other.lockVideoEmbed;
        this.lockVideoDownload = other.lockVideoDownload;
    }

    public void reset() {
        metaReleaseDate = null;
        metaRating = null;
        metaPublisher = null;
        metaName = null;
        metaSortName = null;
        metaDeveloper = null;
        metaDesc = null;
        players = null;
        metaGenre = null;
        images = null;
        screenScraperId = null;
        videodownload = null;
        videoembed = null;
        favorite = false;

        lockDesc = false;
        lockDeveloper = false;
        lockGenre = false;
        lockImages = false;
        lockName = false;
        lockSortName = false;
        lockPlayers = false;
        lockPublisher = false;
        lockRating = false;
        lockReleasedate = false;
        lockScreenScraperId = false;
        lockVideoDownload = false;
        lockVideoEmbed = false;
    }

    public void setMetaData(MetaData other) {
        if(other != null) {
            metaReleaseDate = other.metaReleaseDate;
            metaRating = other.metaRating;
            metaPublisher = other.metaPublisher;
            metaName = other.metaName;
            metaSortName = other.metaSortName;
            metaDeveloper = other.metaDeveloper;
            metaDesc = other.metaDesc;
            players = other.players;
            metaGenre = other.metaGenre;
            images = other.images;
            screenScraperId = other.screenScraperId;
            videodownload = other.videodownload;
            videoembed = other.videoembed;
            favorite = other.favorite;
            transferLocksFrom(other);
        }
    }

    public void updateMetaData(MetaDataId id, String value) {
        switch(id) {
            case NAME:
                metaName = value;
                break;
            case SORTNAME:
                metaSortName = value;
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
            case SCREEN_SCRAPER_ID:
                screenScraperId = value;
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
            case SORTNAME:
                lockSortName = lock;
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
            case SCREEN_SCRAPER_ID:
                lockScreenScraperId = lock;
                break;
            case VIDEO_DOWNLOAD:
                lockVideoDownload = lock;
                break;
            case VIDEO_EMBED:
                lockVideoEmbed = lock;
                break;
        }
    }

    @Override
    public String toString() {
        return "MetaData{" + "favorite=" + favorite + ", metaName=" + metaName + ", metaSortName=" + metaSortName + ", lockName=" + lockName + ", metaDesc=" + metaDesc + ", lockDesc=" + lockDesc + ", images=" + images + ", lockImages=" + lockImages + ", screenScraperId=" + screenScraperId + ", lockScreenScraperId=" + lockScreenScraperId + ", videoembed=" + videoembed + ", lockVideoEmbed=" + lockVideoEmbed + ", videodownload=" + videodownload + ", lockVideoDownload=" + lockVideoDownload + ", metaRating=" + metaRating + ", lockRating=" + lockRating + ", metaReleaseDate=" + metaReleaseDate + ", lockReleasedate=" + lockReleasedate + ", metaDeveloper=" + metaDeveloper + ", lockDeveloper=" + lockDeveloper + ", metaPublisher=" + metaPublisher + ", lockPublisher=" + lockPublisher + ", metaGenre=" + metaGenre + ", lockGenre=" + lockGenre + ", players=" + players + ", lockPlayers=" + lockPlayers + '}';
    }
}
