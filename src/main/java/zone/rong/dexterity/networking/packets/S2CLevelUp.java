package zone.rong.dexterity.networking.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import zone.rong.dexterity.api.DexterityAPI;
import zone.rong.dexterity.skill.SkillContainer;
import zone.rong.dexterity.skill.client.LevelUpToast;

import java.util.function.Supplier;

public class S2CLevelUp {

    private final int skillId;
    private final int level;

    public S2CLevelUp(SkillContainer skillContainer) {
        this.skillId = DexterityAPI.getRawID(DexterityAPI.Registries.SKILLS, skillContainer.skillType);
        this.level = skillContainer.level;
    }

    public S2CLevelUp(int skillId, int level) {
        this.skillId = skillId;
        this.level = level;
    }

    public static void encode(S2CLevelUp instance, PacketBuffer buffer) {
        buffer.writeVarInt(instance.skillId);
        buffer.writeVarInt(instance.level);
    }

    public static S2CLevelUp decode(PacketBuffer buffer) {
        return new S2CLevelUp(buffer.readVarInt(), buffer.readVarInt());
    }

    public static void handle(S2CLevelUp instance, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Minecraft.getInstance().getToastGui().add(new LevelUpToast(instance.skillId, instance.level)));
        ctx.get().setPacketHandled(true);
    }

}
