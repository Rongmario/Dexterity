package zone.rong.dexterity.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zone.rong.dexterity.api.capability.DexterityCapabilities;
import zone.rong.dexterity.api.capability.skill.IGlintMarker;

// Fixme: handle this between ScreenHandlers and inventory interactions! (creation of ItemStacks)
// TODO: Port to 1.16, use 'setEntity/setHolder', as ItemStacks have a 'entity' field traditionally used for both ItemFrames and ItemEntities
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Unique private PlayerEntity holder;

    @Inject(method = "inventoryTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;inventoryTick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;IZ)V"))
    private void makePlayerTheHolder(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (this.holder == null) {
            this.holder = (PlayerEntity) entity;
        }
    }

    @Inject(method = "hasEffect", at = @At("RETURN"), cancellable = true)
    private void injectGlintMarkerCheck(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValueZ() || ((ItemStack) (Object) (this)).getCapability(DexterityCapabilities.GLINT_MARKER).map(IGlintMarker::hasGlint).orElse(false));
    }

}
