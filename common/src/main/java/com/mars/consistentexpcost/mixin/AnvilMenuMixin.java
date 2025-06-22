package com.mars.consistentexpcost.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.mars.consistentexpcost.CommonClass.getTotalXpAtLevel;
import static com.mars.consistentexpcost.ConfigOptions.set_level_cost;
import static com.mars.consistentexpcost.ConfigOptions.use_minimal_exp_cost;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {
    @Redirect(method = "onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;giveExperienceLevels(I)V"))
    private void onTake(Player player, int levelCost) {
        player.giveExperiencePoints(use_minimal_exp_cost ? -getTotalXpAtLevel(-levelCost) : levelCost * set_level_cost);

        if (player.experienceLevel < 0) {
            player.experienceLevel = 0;
            player.experienceProgress = 0.0F;
            player.totalExperience = 0;
        }
    }
}
