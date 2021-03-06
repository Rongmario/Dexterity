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

package zone.rong.dexterity;

import com.chocohead.mm.api.ClassTinkerers;
import com.google.common.collect.Streams;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import zone.rong.dexterity.api.DexterityPackets;
import zone.rong.dexterity.api.DexteritySkills;
import zone.rong.dexterity.rpg.skill.common.api.ServerWorldArtificialBlockStatesHandler;
import zone.rong.dexterity.rpg.skill.common.api.SkillHandler;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 1a. Handles keybind while loops
 * 2a. Handles Mining trait - Double Drops
 * 2b. Handles Mining Perk - Super Breaker: Triple Drops
 * 3.  BrewingStandBlockEntity IFNE -> IFGT
 * {@link DexterityASM#modifyDrops(BlockState, LootContext.Builder, ServerWorld, BlockPos, Entity)}
 */
public class DexterityASM implements Runnable {

    public static final String INTERMEDIARY = "intermediary";
    public static final MappingResolver REMAPPER = FabricLoader.getInstance().getMappingResolver();

    @Override
    public void run() {
        ClassTinkerers.addTransformation(REMAPPER.mapClassName(INTERMEDIARY, "net.minecraft.client.MinecraftClient"), this::transformMinecraftClient);
        ClassTinkerers.addTransformation(REMAPPER.mapClassName(INTERMEDIARY, "net.minecraft.block.Block"), this::transformBlock);
        ClassTinkerers.addTransformation(REMAPPER.mapClassName(INTERMEDIARY, "net.minecraft.block.entity.BrewingStandBlockEntity"), this::transformBrewingStandBlockEntity);
    }

    private void transformMinecraftClient(ClassNode classNode) {
        final String handleInputEvents = REMAPPER.mapMethodName(INTERMEDIARY, "net.minecraft.client.MinecraftClient", "handleInputEvents", "Lnet/minecraft/client/MinecraftClient;handleInputEvents()V");
        final String isUsingItem = REMAPPER.mapMethodName(INTERMEDIARY, "net.minecraft.client.network.ClientPlayerEntity", "isUsingItem", "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z");
        classNode.methods.stream()
                .filter(m -> m.name.equals(handleInputEvents))
                .findFirst()
                .ifPresent(m -> Streams.stream(m.instructions)
                        .filter(i -> i.getOpcode() == Opcodes.INVOKEVIRTUAL)
                        .map(i -> (MethodInsnNode) i)
                        .filter(i -> i.name.equals(isUsingItem))
                        .findFirst()
                        .ifPresent(i -> {
                            AbstractInsnNode inject = i.getPrevious().getPrevious();
                            m.instructions.insertBefore(inject, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(this.getClass()), "injectKeyBinds", "()V"));
                        }));
    }

    private void transformBlock(ClassNode classNode) {
        final String getDroppedStacks = REMAPPER.mapMethodName(INTERMEDIARY, "net.minecraft.block.Block", "getDroppedStacks", "(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;");
        classNode.methods.stream()
                .filter(m -> m.name.equals(getDroppedStacks))
                .skip(1)
                .findFirst()
                .ifPresent(m -> Streams.stream(m.instructions)
                        .filter(i -> i.getOpcode() == Opcodes.ARETURN)
                        .findFirst()
                        .ifPresent(i -> {
                            m.instructions.remove(i.getPrevious()); // INVOKEVIRTUAL BlockState#getDroppedStacks
                            // ALOAD 0 - Load BlockState reference
                            // ALOAD 6 - Load LootContext.Builder reference
                            m.instructions.insertBefore(i, new VarInsnNode(Opcodes.ALOAD, 1)); // Load ServerWorld reference
                            m.instructions.insertBefore(i, new VarInsnNode(Opcodes.ALOAD, 2)); // Load BlockPos reference
                            m.instructions.insertBefore(i, new VarInsnNode(Opcodes.ALOAD, 4)); // Load Entity reference
                            m.instructions.insertBefore(i, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(this.getClass()), "modifyDrops", "(Lnet/minecraft/block/BlockState;Lnet/minecraft/loot/context/LootContext$Builder;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)Ljava/util/List;"));
                        }));
    }

    private void transformBrewingStandBlockEntity(ClassNode classNode) {
        final String tick = REMAPPER.mapMethodName(INTERMEDIARY, "net.minecraft.block.entity.BrewingStandBlockEntity", "tick", "(Lnet/minecraft/block/entity/BrewingStandBlockEntity;tick()V");
        classNode.methods.stream()
                .filter(m -> m.name.equals(tick))
                .findFirst()
                .ifPresent(m -> Streams.stream(m.instructions)
                        .filter(i -> i.getOpcode() == Opcodes.IFNE)
                        .findFirst()
                        .ifPresent(i -> {
                            m.instructions.insertBefore(i, new JumpInsnNode(Opcodes.IFGT, ((JumpInsnNode) i).label));
                            m.instructions.remove(i);
                        }));
    }

    public static void injectKeyBinds() {
        while (DexterityClient.READY_UP.wasPressed()) {
            ClientSidePacketRegistry.INSTANCE.sendToServer(DexterityPackets.C2S_READY_UP, new PacketByteBuf(Unpooled.EMPTY_BUFFER));
        }
        while (DexterityClient.SKILL_MENU.wasPressed()) {
            ClientSidePacketRegistry.INSTANCE.sendToServer(DexterityPackets.C2S_SKILLS_QUERY, new PacketByteBuf(Unpooled.EMPTY_BUFFER));
        }
    }

    // TODO modify triple drop chance, and double will happen as a trait
    public static List<ItemStack> modifyDrops(BlockState state, LootContext.Builder builder, ServerWorld world, BlockPos pos, Entity entity) {
        List<ItemStack> builtStacks = state.getDroppedStacks(builder);
        if (entity instanceof ServerPlayerEntity && !((ServerWorldArtificialBlockStatesHandler) world).isArtificial(pos) && ((ServerPlayerEntity) entity).getRandom().nextFloat() >= 0.5F) {
            return builtStacks.stream()
                    .flatMap(stack -> Stream.generate(() -> stack).limit(((SkillHandler) entity).getPerkManager().isPerkActive(DexteritySkills.SUPER_BREAK) ? 3 : 2))
                    .collect(Collectors.toList());
        }
        return builtStacks;
    }

}
