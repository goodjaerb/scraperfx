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

    public void updateMetaData(MetaData newMetaData) {
        if(metadata == null) {
            metadata = new MetaData();
        }
        metadata.metaReleaseDate = metadata.lockReleasedate ? metadata.metaReleaseDate : newMetaData.metaReleaseDate;
        metadata.metaRating = metadata.lockRating ? metadata.metaRating : newMetaData.metaRating;
        metadata.metaPublisher = metadata.lockPublisher ? metadata.metaPublisher : newMetaData.metaPublisher;
        metadata.metaName = metadata.lockName ? metadata.metaName : newMetaData.metaName;
        metadata.metaDeveloper = metadata.lockDeveloper ? metadata.metaDeveloper : newMetaData.metaDeveloper;
        metadata.metaDesc = metadata.lockDesc ? metadata.metaDesc : newMetaData.metaDesc;
        metadata.players = metadata.lockPlayers ? metadata.players : newMetaData.players;
        metadata.metaGenre = metadata.lockGenre ? metadata.metaGenre : newMetaData.metaGenre;
        metadata.images = metadata.lockImages ? metadata.images : newMetaData.images;
        metadata.screenScraperId = metadata.lockScreenScraperId ? metadata.screenScraperId : newMetaData.screenScraperId;
        metadata.videodownload = metadata.lockVideoDownload ? metadata.videodownload : newMetaData.videodownload;
        metadata.videoembed = metadata.lockVideoEmbed ? metadata.videoembed : newMetaData.videoembed;
        metadata.favorite = newMetaData.favorite;
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
