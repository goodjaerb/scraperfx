/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.datasource.impl.screenscraper;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps from TheGamesDB system names to a ScreenScraper system id.
 *
 * @author goodjaerb
 */
public class ScreenScraperSystemIdMap {
    private static final Map<String, Integer> SYSTEM_ID_MAP = new HashMap<>();
    
    public static int getSystemId(String gamesDbSystemName) {
        return SYSTEM_ID_MAP.get(gamesDbSystemName);
    }
    
    static {
        SYSTEM_ID_MAP.put("3DO", 29);
        SYSTEM_ID_MAP.put("Atari 800", 38);
        SYSTEM_ID_MAP.put("Atari 2600", 39);
        SYSTEM_ID_MAP.put("Atari 5200", 40);
        SYSTEM_ID_MAP.put("Atari 7800", 41);
        SYSTEM_ID_MAP.put("Atari Jaguar", 27);
        SYSTEM_ID_MAP.put("Atari Lynx", 28);
        SYSTEM_ID_MAP.put("Colecovision", 48);
        SYSTEM_ID_MAP.put("Intellivision", 115);
        SYSTEM_ID_MAP.put("Microsoft Xbox", 32);
        SYSTEM_ID_MAP.put("Microsoft Xbox 360", 33);
        SYSTEM_ID_MAP.put("MSX", 113);
        SYSTEM_ID_MAP.put("Nintendo 3DS", 17);
        SYSTEM_ID_MAP.put("Nintendo 64", 14);
        SYSTEM_ID_MAP.put("Nintendo DS", 15);
        SYSTEM_ID_MAP.put("Nintendo Entertainment System (NES)", 3);
        SYSTEM_ID_MAP.put("Nintendo Game Boy", 9);
        SYSTEM_ID_MAP.put("Nintendo Game Boy Advance", 12);
        SYSTEM_ID_MAP.put("Nintendo Game Boy Color", 10);
        SYSTEM_ID_MAP.put("Nintendo GameCube", 13);
        SYSTEM_ID_MAP.put("Nintendo Wii", 16);
        SYSTEM_ID_MAP.put("Nintendo Wii U", 18);
        SYSTEM_ID_MAP.put("Sega 32X", 19);
        SYSTEM_ID_MAP.put("Sega CD", 20);
        SYSTEM_ID_MAP.put("Sega Dreamcast", 23);
        SYSTEM_ID_MAP.put("Sega Game Gear", 21);
        SYSTEM_ID_MAP.put("Sega Genesis", 1);
        SYSTEM_ID_MAP.put("Sega Master System", 2);
        SYSTEM_ID_MAP.put("Sega Mega Drive", 1);
        SYSTEM_ID_MAP.put("Sega Saturn", 22);
        SYSTEM_ID_MAP.put("Sony Playstation", 57);
        SYSTEM_ID_MAP.put("Sony Playstation 2", 58);
        SYSTEM_ID_MAP.put("Sony Playstation 3", 59);
        SYSTEM_ID_MAP.put("Sony Playstation Portable", 61);
        SYSTEM_ID_MAP.put("Sony Playstation Vita", 62);
        SYSTEM_ID_MAP.put("Super Nintendo (SNES)", 4);
        SYSTEM_ID_MAP.put("TurboGrafx 16", 31);
        SYSTEM_ID_MAP.put("WonderSwan", 45);
        SYSTEM_ID_MAP.put("WonderSwan Color", 46);
    }
}
