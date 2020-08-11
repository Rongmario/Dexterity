/*
 *   Copyright (c) 2020 Rongmario
 *
 *   Permission is hereby granted, free of charge, to any person obtaining
 *   a copy of this software and associated documentation files (the
 *   "Software"), to deal in the Software without restriction, including
 *   without limitation the rights to use, copy, modify, merge, publish,
 *   distribute, sublicense, and/or sell copies of the Software, and to
 *   permit persons to whom the Software is furnished to do so, subject to
 *   the following conditions:
 *
 *   The above copyright notice and this permission notice shall be
 *   included in all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *   LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *   OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package zone.rong.dexterity.rpg.skill.common.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zone.rong.dexterity.DexterityData;
import zone.rong.dexterity.api.event.ServerPlayerConstructedEvent;
import zone.rong.dexterity.rpg.skill.perk.PerkManager;
import zone.rong.dexterity.rpg.skill.SkillManager;
import zone.rong.dexterity.rpg.skill.common.api.SkillHandler;
import zone.rong.dexterity.rpg.skill.common.api.XPStore;
import zone.rong.dexterity.rpg.skill.common.api.cultivation.NutritionHandler;
import zone.rong.dexterity.rpg.skill.common.api.cultivation.NutritionManager;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity implements SkillHandler, NutritionHandler {

    @Shadow @Final public ServerPlayerInteractionManager interactionManager;
    @Unique private SkillManager skillManager;
    @Unique private PerkManager perkManager;
    @Unique private NutritionManager nutritionManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructManagers(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager, CallbackInfo ci) {
        this.skillManager = new SkillManager((ServerPlayerEntity) (Object) this);
        this.perkManager = new PerkManager((ServerPlayerEntity) (Object) this, skillManager);
        this.nutritionManager = new NutritionManager((ServerPlayerEntity) (Object) this);
        XPStore.GLOBAL_STORE.stream()
                .filter(map -> map.containsKey(profile.getId()))
                .forEach(map -> map.get(profile.getId()).int2IntEntrySet()
                        .forEach(pair -> skillManager.addXp(DexterityData.SKILLS.get(pair.getIntKey()), pair.getIntValue())));
        ServerPlayerConstructedEvent.EVENT.invoker().onConstruction((ServerPlayerEntity) (Object) this, world, profile, interactionManager);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void injectCooldownUpdate(CallbackInfo ci) {
        this.perkManager.update();
    }

    @Inject(method = "readCustomDataFromTag", at = @At("TAIL"))
    private void deserializeManagers(CompoundTag tag, CallbackInfo ci) {
        this.skillManager.deserialize(tag);
        this.nutritionManager.deserialize(tag);
    }

    @Inject(method = "writeCustomDataToTag", at = @At("TAIL"))
    private void serializeManagers(CompoundTag tag, CallbackInfo ci) {
        this.skillManager.serialize(tag);
        this.nutritionManager.serialize(tag);
    }

    @Inject(method = "dropItem", at = @At("HEAD"))
    private void removeActiveTagsOnDropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (perkManager.isPerkActive(stack)) {
            perkManager.endPerk();
        }
    }

    @Override
    public SkillManager getSkillManager() {
        return skillManager;
    }

    @Override
    public PerkManager getPerkManager() {
        return perkManager;
    }

    @Override
    public NutritionManager getNutritionManager() {
        return nutritionManager;
    }
}
