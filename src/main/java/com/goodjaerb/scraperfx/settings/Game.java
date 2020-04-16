/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.settings;

import java.util.Comparator;
import java.util.Objects;

/**
 * @author goodjaerb
 */
public class Game {

    private enum CompareType {
        GAME_NAME, FILE_NAME
    }

    private static class GameComparator implements Comparator<Game> {

        private final CompareType compareType;

        GameComparator(CompareType compareType) {
            this.compareType = compareType;
        }

        @Override
        public int compare(Game o1, Game o2) {
            return o1.getCompareToValue(compareType).compareTo(o2.getCompareToValue(compareType));
        }
    }

    public static final Comparator<Game> GAME_NAME_COMPARATOR = new GameComparator(CompareType.GAME_NAME);
    public static final Comparator<Game> FILE_NAME_COMPARATOR = new GameComparator(CompareType.FILE_NAME);

    public enum MatchStrength {
        LOCKED("lightblue"),
        STRONG("green"),
        BEST_GUESS("lightgreen"),
        TIE_BREAKER("yellow"),
        LOW_PERCENTAGE("orange"),
        NO_MATCH("red"),
        IGNORE("black");

        public final String cssBackground;

        MatchStrength(String cssBackground) {
            this.cssBackground = cssBackground;
        }
    }

    public String        matchedName;
    public MatchStrength strength;
    public String        fileName;
    public MetaData      metadata;

    public Game() {
        this("");
    }

    public Game(String fileName) {
        this.fileName = fileName;
        this.strength = MatchStrength.NO_MATCH;
    }

    public Game(Game g) {
        this(g.fileName);
        this.strength = g.strength;
        this.matchedName = g.matchedName;
        this.metadata = g.metadata;
    }

    public void updateMetaData(MetaData newMetaData, boolean onlyIfBlank) {
        if(metadata == null) {
            metadata = new MetaData();
        }
        metadata.metaReleaseDate    = metadata.lockReleasedate      ? metadata.metaReleaseDate  : (!onlyIfBlank || (onlyIfBlank && metadata.metaReleaseDate != null && metadata.metaReleaseDate.isBlank())) ? newMetaData.metaReleaseDate : metadata.metaReleaseDate;
        metadata.metaRating         = metadata.lockRating           ? metadata.metaRating       : (!onlyIfBlank || (onlyIfBlank && metadata.metaRating != null && metadata.metaRating.isBlank())) ? newMetaData.metaRating : metadata.metaRating;
        metadata.metaPublisher      = metadata.lockPublisher        ? metadata.metaPublisher    : (!onlyIfBlank || (onlyIfBlank && metadata.metaPublisher != null && metadata.metaPublisher.isBlank())) ? newMetaData.metaPublisher : metadata.metaPublisher;
        metadata.metaName           = metadata.lockName             ? metadata.metaName         : (!onlyIfBlank || (onlyIfBlank && metadata.metaName != null && metadata.metaName.isBlank())) ? newMetaData.metaName : metadata.metaName;
        metadata.metaDeveloper      = metadata.lockDeveloper        ? metadata.metaDeveloper    : (!onlyIfBlank || (onlyIfBlank && metadata.metaDeveloper != null && metadata.metaDeveloper.isBlank())) ? newMetaData.metaDeveloper : metadata.metaDeveloper;
        metadata.metaDesc           = metadata.lockDesc             ? metadata.metaDesc         : (!onlyIfBlank || (onlyIfBlank && metadata.metaDesc != null && metadata.metaDesc.isBlank())) ? newMetaData.metaDesc : metadata.metaDesc;
        metadata.players            = metadata.lockPlayers          ? metadata.players          : (!onlyIfBlank || (onlyIfBlank && metadata.players != null && metadata.players.isBlank())) ? newMetaData.players : metadata.players;
        metadata.metaGenre          = metadata.lockGenre            ? metadata.metaGenre        : (!onlyIfBlank || (onlyIfBlank && metadata.metaGenre != null && metadata.metaGenre.isBlank())) ? newMetaData.metaGenre : metadata.metaGenre;
        metadata.images             = metadata.lockImages           ? metadata.images           : (!onlyIfBlank || (onlyIfBlank && metadata.images != null && metadata.images.isEmpty())) ? newMetaData.images : metadata.images;
        metadata.screenScraperId    = metadata.lockScreenScraperId  ? metadata.screenScraperId  : (!onlyIfBlank || (onlyIfBlank && metadata.screenScraperId != null && metadata.screenScraperId.isBlank())) ? newMetaData.screenScraperId : metadata.screenScraperId;
        metadata.videodownload      = metadata.lockVideoDownload    ? metadata.videodownload    : (!onlyIfBlank || (onlyIfBlank && metadata.videodownload != null && metadata.videodownload.isBlank())) ? newMetaData.videodownload : metadata.videodownload;
        metadata.videoembed         = metadata.lockVideoEmbed       ? metadata.videoembed       : (!onlyIfBlank || (onlyIfBlank && metadata.videoembed != null && metadata.videoembed.isBlank())) ? newMetaData.videoembed : metadata.videoembed;
        metadata.favorite           = newMetaData.favorite;
    }

    public String getImageUrlByType(String type) {
        if(metadata != null && metadata.images != null) {
            for(final Image img : metadata.images) {
                if(img.type.equals(type)) {
                    return img.url;
                }
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.fileName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final Game other = (Game) obj;
        return Objects.equals(this.fileName, other.fileName);
    }

    private String getCompareToValue(CompareType compareType) {
        if(compareType == CompareType.GAME_NAME) {
            if(metadata != null && metadata.metaName != null) {
                return metadata.metaName.toLowerCase();
            }
        }
        return fileName.toLowerCase();
    }

    @Override
    public String toString() {
        String result = "";
        if(metadata != null) {
            if(metadata.favorite) {
                result += "*";
            }
            if(metadata.metaName != null && !metadata.metaName.trim().isEmpty()) {
                result += (metadata.metaName + " ");
            }
        }
        result += ("[[" + fileName + "]]");
        return result;
    }

    public String toLongString() {
        return "Game(filename=" + fileName + ", matchedName=" + matchedName + ", strength=" + strength + ", metadata=" + metadata + ")";
    }
}
