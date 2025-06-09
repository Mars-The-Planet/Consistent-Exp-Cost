package com.mars.consistentexpcost;

import com.mars.deimos.config.DeimosConfig;

import static com.mars.consistentexpcost.Constants.MOD_ID;

public class CommonClass {
    public static void init() {
        DeimosConfig.init(MOD_ID, ConfigOptions.class);
    }

    // https://minecraft.wiki/w/Experience#Leveling_up
    public static int getTotalXpAtLevel(int lvl) {
        if (lvl <= 16) {
            return lvl * lvl + 6 * lvl;
        }
        else if (lvl <= 31) {
            return (int) ((2.5 * lvl * lvl) - (40.5 * lvl) + 360);
        }
        else {
            return (int) ((4.5 * lvl * lvl) - (162.5 * lvl) + 2220);
        }
    }
}
