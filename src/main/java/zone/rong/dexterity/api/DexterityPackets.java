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

package zone.rong.dexterity.api;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import zone.rong.dexterity.DexterityData;
import zone.rong.dexterity.rpg.skill.types.Skill;
import zone.rong.dexterity.rpg.skill.SkillEntry;
import zone.rong.dexterity.rpg.skill.client.ImmutableSkillEntry;
import zone.rong.dexterity.rpg.skill.client.SkillScreen;
import zone.rong.dexterity.rpg.skill.client.SkillToast;
import zone.rong.dexterity.rpg.skill.client.api.HeldItemHandler;
import zone.rong.dexterity.rpg.skill.common.api.SkillHandler;

import java.util.Collection;

public class DexterityPackets {

    public static final Identifier C2S_SKILLS_QUERY = new Identifier("dexterity", "c2s_skills_query");

    public static final Identifier S2C_SKILLS_RESPONSE = new Identifier("dexterity", "c2s_skills_response");
    public static final Identifier S2C_LEVEL_REACHED = new Identifier("dexterity", "s2c_level_reached");
    public static final Identifier S2C_READY = new Identifier("dexterity", "s2c_ready");

    public static void registerC2SPackets() {
        ServerSidePacketRegistry.INSTANCE.register(C2S_SKILLS_QUERY, (ctx, packet) -> ctx.getTaskQueue().execute(() -> {
            ServerPlayerEntity player = (ServerPlayerEntity) ctx.getPlayer();
            PacketByteBuf responsePacket = writePacket(((SkillHandler) player).getSkillManager().getSkillEntries());
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, S2C_SKILLS_RESPONSE, responsePacket);
        }));
    }

    @Environment(EnvType.CLIENT)
    public static void registerS2CPackets() {
        ClientSidePacketRegistry.INSTANCE.register(S2C_LEVEL_REACHED, (ctx, packet) -> {
            Skill<?> skill = DexterityData.SKILLS.get(packet.readInt());
            int levelPriorToCheck = packet.readInt();
            int level = packet.readInt();
            ctx.getTaskQueue().execute(() -> MinecraftClient.getInstance().getToastManager().add(new SkillToast(skill, levelPriorToCheck, level)));
        });
        ClientSidePacketRegistry.INSTANCE.register(S2C_READY, (ctx, packet) -> {
            boolean ready = packet.readBoolean();
            ctx.getTaskQueue().execute(() -> ((HeldItemHandler) MinecraftClient.getInstance().getHeldItemRenderer()).ready(ready));
        });
        SkillScreen.receivePacket();
    }

    public static PacketByteBuf writePacket(Collection<SkillEntry> entries) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        for (SkillEntry entry : entries) {
            entry.writeToPacket(packet);
        }
        return packet;
    }

    @Environment(EnvType.CLIENT)
    public static Object2ObjectRBTreeMap<Skill<?>, ImmutableSkillEntry> readPacket(PacketByteBuf packet) {
        final Object2ObjectRBTreeMap<Skill<?>, ImmutableSkillEntry> packetReads = new Object2ObjectRBTreeMap<>();
        for (int i = 0; i < DexterityData.SKILLS.getEntries().size(); i++) {
            packetReads.put(DexterityData.SKILLS.get(packet.readInt()), new ImmutableSkillEntry(packet.readLong(), packet.readLong(), packet.readInt()));
        }
        return packetReads;
    }

}
