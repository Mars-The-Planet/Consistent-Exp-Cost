package com.mars.consistentexpcost.mixin;

import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.mars.consistentexpcost.CommonClass.getMinimumLevelForXp;
import static com.mars.consistentexpcost.ConfigOptions.set_level_cost;
import static com.mars.consistentexpcost.ConfigOptions.use_minimal_exp_cost;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {

    @Shadow @Final
    public int[] costs;

    @Inject(at = @At("HEAD"), method = "getEnchantmentList")
    public void getEnchantmentList(ItemStack $$0, int enchantSlot, int $$2, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (use_minimal_exp_cost) return;

        int newCost = getMinimumLevelForXp(set_level_cost * (enchantSlot + 1));
        if (newCost > costs[enchantSlot])
            costs[enchantSlot] = newCost;
    }
}
