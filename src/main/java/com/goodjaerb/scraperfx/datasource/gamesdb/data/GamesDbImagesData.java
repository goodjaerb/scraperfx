/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.gamesdb.data;

import com.google.gson.annotations.Expose;

import java.util.*;

/**
 * @author goodjaerb
 */
public class GamesDbImagesData extends GamesDbPaginatedData<Map<String, List<GamesDbImagesData.Image>>> {
    @Expose(serialize = true, deserialize = true)
    public BaseUrl                  base_url;
    @Expose(serialize = true, deserialize = true)
    public Map<String, List<Image>> images;

    public static class BaseUrl {
        @Expose(serialize = true, deserialize = true)
        public String original;
        @Expose(serialize = true, deserialize = true)
        public String small;
        @Expose(serialize = true, deserialize = true)
        public String thumb;
        @Expose(serialize = true, deserialize = true)
        public String cropped_center_thumb;
        @Expose(serialize = true, deserialize = true)
        public String medium;
        @Expose(serialize = true, deserialize = true)
        public String large;

        public BaseUrl() {

        }

        @Override
        public String toString() {
            return "BaseUrl{" + "original=" + original + ", small=" + small + ", thumb=" + thumb + ", cropped_center_thumb=" + cropped_center_thumb + ", medium=" + medium + ", large=" + large + '}';
        }
    }

    public static class Image {
        @Expose(serialize = true, deserialize = true)
        public int    id;
        @Expose(serialize = true, deserialize = true)
        public String type;
        @Expose(serialize = true, deserialize = true)
        public String side;
        @Expose(serialize = true, deserialize = true)
        public String filename;
        @Expose(serialize = true, deserialize = true)
        public String resolution;

        public Image() {

        }

        @Override
        public String toString() {
            return "Image{" + "id=" + id + ", type=" + type + ", side=" + side + ", filename=" + filename + ", resolution=" + resolution + '}';
        }
    }

    public GamesDbImagesData() {

    }

    @Override
    public String toString() {
        return "GamesDbImagesData{" + "count=" + count + ", base_url=" + base_url + ", images=" + images + '}';
    }

    @Override
    public void appendData(Collection<Map<String, List<Image>>> data) {
        data.forEach((map) -> {
            images.putAll(map);
        });
    }

    /**
     * kind of awkward but going to try this. i need the whole map out of this to associate the game id to the list of images.
     * so this will return a one element collection which is the Map of the values of this data.
     *
     * @return
     */
    @Override
    public Collection<Map<String, List<Image>>> values() {
        final List<Map<String, List<Image>>> values = new ArrayList<>();
        values.add(images);
        return Collections.unmodifiableCollection(values);
    }

    @Override
    public boolean isDataAvailable() {
        return !(images == null || images.isEmpty());
    }
}
