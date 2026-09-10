package com.mars.consistentexpcost.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mars.consistentexpcost.CommonClass.getMinimumLevelForXp;
import static com.mars.consistentexpcost.CommonClass.getTotalXpAtLevel;
import static com.mars.consistentexpcost.ConfigOptions.*;
import static com.mars.consistentexpcost.ConfigOptions.remove_too_expensive;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu  {

    @Shadow @Final
    private DataSlot cost;

    @Unique boolean consistentexpcost$trip = true;

    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

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

        if (remove_too_expensive && this.cost.get() >= 40) {
            if (use_minimal_exp_cost) {
                this.player.sendSystemMessage(Component.literal("Ignore \"TOO EXPENSIVE\" you can pick the item. It will cost " + this.cost.get() + " levels.").withStyle(ChatFormatting.GREEN));
            }
            else {
                this.player.sendSystemMessage(Component.literal("Ignore \"TOO EXPENSIVE\" you can pick the item. It will cost " + this.cost.get() * set_level_cost + " EXP.").withStyle(ChatFormatting.GREEN));
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "mayPickup", cancellable = true)
    public void mayPickup(Player player, boolean hasStack, CallbackInfoReturnable<Boolean> cir) {
        if (use_minimal_exp_cost) return;

        int newCost = set_level_cost * this.cost.get();

        if (!player.getAbilities().instabuild && player.totalExperience < newCost) {
            if (consistentexpcost$trip) {
                player.sendSystemMessage(Component.literal("You need " + newCost + " EXP (roughly level " + getMinimumLevelForXp(newCost) + "), but you only have " + player.totalExperience + " EXP").withStyle(ChatFormatting.RED));
                consistentexpcost$trip = false;
            }
            cir.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40))
    private int whenTooExpensive(int value) {
        if (!remove_too_expensive)
            return 40;
        return Integer.MAX_VALUE;
    }
}
