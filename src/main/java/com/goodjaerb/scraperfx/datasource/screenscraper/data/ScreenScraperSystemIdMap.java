/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.screenscraper.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps from TheGamesDB system names to a ScreenScraper system id.
 *
 * @author goodjaerb
 */
public class ScreenScraperSystemIdMap {
    private static final Map<String, Integer> SYSTEM_ID_MAP;

    public static Integer getId(String gamesDbSystemName) {
        return SYSTEM_ID_MAP.get(gamesDbSystemName);
    }

    static {
        final HashMap<String, Integer> map = new HashMap<>();
        map.put("3DO", 29);
        map.put("Atari 800", 38);
        map.put("Atari 2600", 26);
        map.put("Atari 5200", 40);
        map.put("Atari 7800", 41);
        map.put("Atari Jaguar", 27);
        map.put("Atari Lynx", 28);
        map.put("Colecovision", 48);
        map.put("Intellivision", 115);
        map.put("Microsoft Xbox", 32);
        map.put("Microsoft Xbox 360", 33);
        map.put("MSX", 113);
        map.put("Neo Geo Pocket Color", 82);
        map.put("Nintendo 3DS", 17);
        map.put("Nintendo 64", 14);
        map.put("Nintendo DS", 15);
        map.put("Nintendo Entertainment System (NES)", 3);
        map.put("Nintendo Game Boy", 9);
        map.put("Nintendo Game Boy Advance", 12);
        map.put("Nintendo Game Boy Color", 10);
        map.put("Nintendo GameCube", 13);
        map.put("Nintendo Wii", 16);
        map.put("Nintendo Wii U", 18);
        map.put("Sega 32X", 19);
        map.put("Sega CD", 20);
        map.put("Sega Dreamcast", 23);
        map.put("Sega Game Gear", 21);
        map.put("Sega Genesis", 1);
        map.put("Sega Master System", 2);
        map.put("Sega Mega Drive", 1);
        map.put("Sega Saturn", 22);
        map.put("Sony Playstation", 57);
        map.put("Sony Playstation 2", 58);
        map.put("Sony Playstation 3", 59);
        map.put("Sony Playstation Portable", 61);
        map.put("Sony Playstation Vita", 62);
        map.put("Super Nintendo (SNES)", 4);
        map.put("TurboGrafx 16", 31);
        map.put("WonderSwan", 45);
        map.put("WonderSwan Color", 46);

        SYSTEM_ID_MAP = Collections.unmodifiableMap(map);
    }
}
