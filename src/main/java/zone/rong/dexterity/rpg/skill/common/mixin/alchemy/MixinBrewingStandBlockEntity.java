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

package zone.rong.dexterity.rpg.skill.common.mixin.alchemy;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zone.rong.dexterity.DexterityData;
import zone.rong.dexterity.api.DexterityNBT;
import zone.rong.dexterity.api.DexteritySkills;
import zone.rong.dexterity.rpg.skill.common.api.XPStore;
import zone.rong.dexterity.rpg.skill.common.api.alchemy.BrewingStandBlockEntityHandler;

import java.util.UUID;

@Mixin(BrewingStandBlockEntity.class)
public abstract class MixinBrewingStandBlockEntity extends LockableContainerBlockEntity implements XPStore, BrewingStandBlockEntityHandler {

    @Unique private UUID playerUuid;
    @Unique private Object2ObjectMap<UUID, Int2IntMap> xpStore;

    @Shadow private int brewTime;

    protected MixinBrewingStandBlockEntity() {
        super(null);
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 20))
    private int modifyFuel(int fuel) {
        return playerUuid == null ? fuel : fuel + 5; // Temp
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 1, ordinal = 2))
    private int modifyBrewTime(int decrement) {
        return playerUuid == null && this.brewTime < 10 ? decrement : decrement + 9; // Temp
    }

    @Redirect(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private Object addSkill(DefaultedList<Object> inventory, int index, Object stack) {
        addXp(DexteritySkills.ALCHEMY, 5);
        return inventory.set(index, stack);
    }

    @Inject(method = "fromTag", at = @At("TAIL"))
    private void getUuidFromTag(BlockState state, CompoundTag tag, CallbackInfo ci) {
        fromTag(tag, DexterityNBT.XPStore.BREWING_STAND_XP_STORE_KEY);
    }

    @Inject(method = "toTag", at = @At("TAIL"))
    private void addUuidToTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        toTag(tag, DexterityNBT.XPStore.BREWING_STAND_XP_STORE_KEY);
    }

    @Override
    public void registerLastPlayer(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    @Override
    public void registerLastPlayer(PlayerEntity player) {
        this.playerUuid = player.getUuid();
    }

    @Override
    public void setLocalXpStore(Object2ObjectMap<UUID, Int2IntMap> map) {
        this.xpStore = map;
    }

    @Override
    public UUID getLastUUID() {
        return playerUuid;
    }

    @Override
    public Object2ObjectMap<UUID, Int2IntMap> getLocalXpStore() {
        return xpStore;
    }

}
