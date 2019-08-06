/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.arcadeitalia.data;

/**
 *
 * @author goodjaerb
 */
public class ArcadeItaliaData {
    public static class Result {
        public int index;
        public String url;
        public String game_name;
        public String title;
        public String cloneof;
        public String manufacturer;
        public String url_image_ingame;
        public String url_image_title;
        public String url_image_marquee;
        public String url_image_cabinet;
        public String url_image_flyer;
        public String genre;
        public int players;
        public String year;
        public String status;
        public String history;
        public String history_copyright_short;
        public String history_copyright_long;
        public String youtube_video_id;
        public String url_video_shortplay;
        public String url_video_shortplay_hd;
        public int emulator_id;
        public String emulator_name;
        public String languages;
        public int rate;
        
        public Result() {
            
        }

        @Override
        public String toString() {
            return "Result{" + "index=" + index + ", url=" + url + ", game_name=" + game_name + ", title=" + title + ", cloneof=" + cloneof + ", manufacturer=" + manufacturer + ", url_image_ingame=" + url_image_ingame + ", url_image_title=" + url_image_title + ", url_image_marquee=" + url_image_marquee + ", url_image_cabinet=" + url_image_cabinet + ", url_image_flyer=" + url_image_flyer + ", genre=" + genre + ", players=" + players + ", year=" + year + ", status=" + status + ", history=" + history + ", history_copyright_short=" + history_copyright_short + ", history_copyright_long=" + history_copyright_long + ", youtube_video_id=" + youtube_video_id + ", url_video_shortplay=" + url_video_shortplay + ", url_video_shortplay_hd=" + url_video_shortplay_hd + ", emulator_id=" + emulator_id + ", emulator_name=" + emulator_name + ", languages=" + languages + ", rate=" + rate + '}';
        }
    }

    public int release;
    public Result[] result;
    
    public ArcadeItaliaData() {
        
    }

    @Override
    public String toString() {
        return "ArcadeItaliaData{" + "release=" + release + ", result=" + (result == null ? "null" : (result.length == 0 ? "null" : result[0])) + '}';
    }
}
