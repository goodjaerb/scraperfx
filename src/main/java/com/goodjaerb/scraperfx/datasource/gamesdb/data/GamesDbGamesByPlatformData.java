/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.gamesdb.data;

import com.google.gson.annotations.Expose;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author goodjaerb
 */
public class GamesDbGamesByPlatformData extends GamesDbPaginatedData<GamesDbGamesByPlatformData.Game> {
    @Expose(serialize = true, deserialize = true) public List<Game> games;
    
    public static class Game {
        @Expose(serialize = true, deserialize = true) public Integer        id;
        @Expose(serialize = true, deserialize = true) public String         game_title;
        @Expose(serialize = true, deserialize = true) public String         release_date;
        @Expose(serialize = true, deserialize = true) public Integer        platform;
        @Expose(serialize = true, deserialize = true) public Integer        players;
        @Expose(serialize = true, deserialize = true) public String         overview;
        @Expose(serialize = true, deserialize = true) public String         last_updated;
        @Expose(serialize = true, deserialize = true) public String         rating;
        @Expose(serialize = true, deserialize = true) public String         coop;
        @Expose(serialize = true, deserialize = true) public String         youtube;
        @Expose(serialize = true, deserialize = true) public String         os;
        @Expose(serialize = true, deserialize = true) public String         processor;
        @Expose(serialize = true, deserialize = true) public String         ram;
        @Expose(serialize = true, deserialize = true) public String         hdd;
        @Expose(serialize = true, deserialize = true) public String         video;
        @Expose(serialize = true, deserialize = true) public String         sound;
        @Expose(serialize = true, deserialize = true) public List<Integer>  developers;
        @Expose(serialize = true, deserialize = true) public List<Integer>  genres;
        @Expose(serialize = true, deserialize = true) public List<Integer>  publishers;
        @Expose(serialize = true, deserialize = true) public List<String>   alternates;
        
        public Game() {
            
        }

        @Override
        public String toString() {
            return "Game{" + "id=" + id + ", game_title=" + game_title + ", release_date=" + release_date + ", platform=" + platform + ", players=" + players + ", overview=" + overview + ", last_updated=" + last_updated + ", rating=" + rating + ", coop=" + coop + ", youtube=" + youtube + ", os=" + os + ", processor=" + processor + ", ram=" + ram + ", hdd=" + hdd + ", video=" + video + ", sound=" + sound + ", developers=" + developers + ", genres=" + genres + ", publishers=" + publishers + ", alternates=" + alternates + '}';
        }
    }
    
    public GamesDbGamesByPlatformData() {
        
    }

    @Override
    public String toString() {
        return "GamesDbGamesByPlatformData{" + "count=" + count + ", games=" + games + '}';
    }

    @Override
    public void appendData(Collection<Game> data) {
        games.addAll(data);
    }

    @Override
    public Collection<Game> values() {
        return Collections.unmodifiableCollection(games);
    }

    @Override
    public boolean isDataAvailable() {
        return !(games == null || games.isEmpty());
    }
}
