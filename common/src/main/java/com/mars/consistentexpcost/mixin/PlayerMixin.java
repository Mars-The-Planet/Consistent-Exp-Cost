package com.mars.consistentexpcost.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mars.consistentexpcost.CommonClass.getTotalXpAtLevel;
import static com.mars.consistentexpcost.ConfigOptions.set_level_cost;
import static com.mars.consistentexpcost.ConfigOptions.use_minimal_exp_cost;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow public AbstractContainerMenu containerMenu;
    @Shadow public int experienceLevel;
    @Shadow public abstract void giveExperiencePoints(int p_36291_);
    @Shadow public float experienceProgress;
    @Shadow public int totalExperience;

    @Inject(at = @At("TAIL"), method = "onEnchantmentPerformed")
    public void onEnchantmentPerformed(ItemStack enchantedItem, int levelCost, CallbackInfo ci) {
        this.experienceLevel += levelCost;

        if(use_minimal_exp_cost){
            int[] costs = ((EnchantmentMenu) containerMenu).costs;
            int minLvl = costs[levelCost - 1];
            int expCost = getTotalXpAtLevel(minLvl - levelCost) - getTotalXpAtLevel(minLvl);

            this.giveExperiencePoints(expCost);
        }
        else{
            this.giveExperiencePoints(levelCost * set_level_cost);
        }

        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0F;
            this.totalExperience = 0;
        }
    }
}
