package zone.rong.dexterity.rpg.skill.common.mixin.cultivation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import zone.rong.dexterity.rpg.skill.common.api.cultivation.SizeHandler;

@Mixin(EntityType.class)
public class MixinEntityType {

    @Inject(method = "create(Lnet/minecraft/world/World;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/text/Text;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/SpawnReason;ZZ)Lnet/minecraft/entity/Entity;", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void modifyEntityDimensions(World world, CompoundTag itemTag, Text name, PlayerEntity player, BlockPos pos, SpawnReason spawnReason, boolean alignPosition, boolean invertY, CallbackInfoReturnable<Entity> cir, Entity entity) {
        if (entity instanceof AnimalEntity) {
            float widthMultiplier = 0.8F + world.random.nextFloat() * (1.28F - 0.95F);
            float heightMultiplier = 0.75F + world.random.nextFloat() * (1.3F - 0.75F);
            ((SizeHandler) entity).setDimensions(widthMultiplier, heightMultiplier);
        }
    }

}
