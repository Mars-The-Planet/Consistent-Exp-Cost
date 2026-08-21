package com.mars.consistentexpcost.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mars.consistentexpcost.CommonClass.getMinimumLevelForXp;
import static com.mars.consistentexpcost.CommonClass.getTotalXpAtLevel;
import static com.mars.consistentexpcost.ConfigOptions.set_level_cost;
import static com.mars.consistentexpcost.ConfigOptions.use_minimal_exp_cost;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @Shadow @Final
    private DataSlot cost;

    @Unique boolean consistentexpcost$trip = true;

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

    @Inject(at = @At("HEAD"), method = "createResult")
    public void onTake(CallbackInfo ci) {
        consistentexpcost$trip = true;
    }

    @Inject(at = @At("TAIL"), method = "mayPickup", cancellable = true)
    public void mayPickup(Player player, boolean hasStack, CallbackInfoReturnable<Boolean> cir) {
        if (use_minimal_exp_cost) return;

        int newCost = set_level_cost * this.cost.get();

        if (!player.hasInfiniteMaterials() && player.totalExperience < newCost) {
            if (consistentexpcost$trip) {
                player.sendSystemMessage(Component.literal("You need " + newCost + " EXP (roughly level " + getMinimumLevelForXp(newCost) + "), but you only have " + player.totalExperience + " EXP").withStyle(ChatFormatting.RED));
                consistentexpcost$trip = false;
            }
            cir.setReturnValue(false);
        }
    }
}
