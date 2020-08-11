/*
 *  * Copyright (c) 2020 Rongmario
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining
 *  * a copy of this software and associated documentation files (the
 *  * "Software"), to deal in the Software without restriction, including
 *  * without limitation the rights to use, copy, modify, merge, publish,
 *  * distribute, sublicense, and/or sell copies of the Software, and to
 *  * permit persons to whom the Software is furnished to do so, subject to
 *  * the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be
 *  * included in all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package zone.rong.dexterity.rpg.skill.common.mixin.crafting;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zone.rong.dexterity.DexterityHelper;
import zone.rong.dexterity.api.DexteritySkills;
import zone.rong.dexterity.rpg.skill.SkillManager;
import zone.rong.dexterity.rpg.skill.common.api.SkillHandler;

@Mixin(CraftingResultSlot.class)
public abstract class MixinCraftingResultSlot extends Slot {

    @Shadow @Final private PlayerEntity player;
    @Shadow @Final private CraftingInventory input;

    @Unique private final ThreadLocal<Recipe<?>> lastRecipe = ThreadLocal.withInitial(() -> null);
    @Unique private final ThreadLocal<Integer> lastRecipeXp = ThreadLocal.withInitial(() -> 1);

    MixinCraftingResultSlot() {
        super(null, 0, 0, 0);
    }

    @Inject(method = "onCrafted(Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/RecipeUnlocker;unlockLastRecipe(Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void onCraftUnlock(ItemStack stack, CallbackInfo ci) {
        if (!this.player.world.isClient) {
            SkillManager manager = ((SkillHandler) this.player).getSkillManager();
            if (this.lastRecipe.get() != ((RecipeUnlocker) this.inventory).getLastRecipe()) {
                this.lastRecipe.set(((RecipeUnlocker) this.inventory).getLastRecipe());
                this.lastRecipeXp.set(DexterityHelper.getUniqueStackCount(((AccessorCraftingInventory) this.input).getStacks()));
                // ForkJoinPool.commonPool().execute(() -> this.LAST_RECIPE_XP.set(DexterityHelper.getUniqueStackCount(((AccessorCraftingInventory) this.input).getStacks())));
            }
            manager.addXp(DexteritySkills.CARPENTRY, this.lastRecipeXp.get());
        }
    }

}
