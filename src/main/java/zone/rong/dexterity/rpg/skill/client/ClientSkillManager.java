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

package zone.rong.dexterity.rpg.skill.client;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import zone.rong.dexterity.DexterityData;
import zone.rong.dexterity.api.DexterityPackets;
import zone.rong.dexterity.rpg.skill.types.Skill;

@Environment(EnvType.CLIENT)
public class ClientSkillManager {

    private final ClientPlayerEntity player;

    private final Object2IntMap<Skill<?>> queries = new Object2IntOpenHashMap<>();

    public ClientSkillManager(ClientPlayerEntity player) {
        this.player = player;
    }

    public int queryAndGetLevel(Skill<?> skill) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeInt(DexterityData.SKILLS.getRawId(skill));
        ClientSidePacketRegistry.INSTANCE.sendToServer(DexterityPackets.C2S_SKILL_LEVEL_QUERY, packet);
        return queries.getInt(skill);
    }

    public void setLevel(Skill<?> skill, int level) {
        this.queries.put(skill, level);
    }

}
