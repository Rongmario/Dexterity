package zone.rong.dexterity.rpg.skill.client.mixin;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zone.rong.dexterity.DexterityClient;
import zone.rong.dexterity.api.DexterityPackets;
import zone.rong.dexterity.rpg.skill.perk.EmptyHandPerk;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Inject(method = "handleInputEvents", at = @At("TAIL"))
    private void handleSkillMenuKeybind(CallbackInfo ci) {
        while (DexterityClient.SKILL_MENU.wasPressed()) {
            ClientSidePacketRegistry.INSTANCE.sendToServer(DexterityPackets.C2S_SKILLS_QUERY, new PacketByteBuf(Unpooled.EMPTY_BUFFER));
        }
    }

    @Redirect(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 1))
    private boolean onStackEmpty(ItemStack stack) {
        if (stack.isEmpty()) {
            ClientSidePacketRegistry.INSTANCE.sendToServer(EmptyHandPerk.PACKET, new PacketByteBuf(Unpooled.EMPTY_BUFFER));
            return true;
        }
        return false;
    }

}
