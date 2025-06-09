package com.mars.consistentexpcost;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ConsistentExpCost {
    public ConsistentExpCost(IEventBus eventBus) {
        CommonClass.init();
    }
}
