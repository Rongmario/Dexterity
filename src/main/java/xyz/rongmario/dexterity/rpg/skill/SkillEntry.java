package xyz.rongmario.dexterity.rpg.skill;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.network.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.TranslatableText;
import xyz.rongmario.dexterity.DexterityData;
import xyz.rongmario.dexterity.api.DexterityPackets;
import xyz.rongmario.dexterity.rpg.skill.types.Skill;

public class SkillEntry {

    private final ServerPlayerEntity player;
    private final Skill<?> skill;

    private long totalXp;
    private int currentXp;
    private int totalLevel = 0;

    private final ThreadLocal<Integer> levelPriorToCheck = ThreadLocal.withInitial(this::getLevel);

    public SkillEntry(ServerPlayerEntity player, Skill<?> skill) {
        this.player = player;
        this.skill = skill;
    }

    void check() {
        int difference = getXpNeededForNextLevel();
        if (difference <= 0) {
            ++this.totalLevel;
            this.currentXp = Math.abs(difference);
            if (this.totalLevel % 10 == 0 && !(player.server instanceof IntegratedServer)) {
                if (this.totalLevel % 50 == 0) {
                    PlayerStream.all(player.server).filter(p -> p != player).forEach(p -> p.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 0.75F, 1.0F));
                }
                player.server.getPlayerManager().broadcastChatMessage(
                        new TranslatableText("message.dexterity.congratulations",
                                player.getName(), skill.getDisplayName(), totalLevel)
                                .styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, skill.getDescription()))), MessageType.CHAT, player.getUuid());
            }
            skill.getTraitLookup().stream().filter(t -> t.getLevelBarrier() <= this.totalLevel).forEach(t -> t.applyModification(player));
            if (this.currentXp > 0) {
                check();
            }
        } else if (levelPriorToCheck.get() < this.totalLevel) {
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeInt(DexterityData.SKILLS.getRawId(skill));
            packet.writeInt(levelPriorToCheck.get());
            packet.writeInt(totalLevel);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, DexterityPackets.S2C_LEVEL_REACHED, packet);
        }
    }

    public void setTotalXp(long totalXp) {
        this.totalXp = totalXp;
    }

    public void setCurrentXp(int currentXp) {
        this.currentXp = currentXp;
    }

    public void setTotalLevel(int totalLevel) {
        this.totalLevel = totalLevel;
    }

    public void addXp(float xp) {
        if (player.getLuck() >= 1.0F) {
            this.totalXp += xp * 2;
            this.currentXp += xp * 2;
        } else {
            this.totalXp += xp;
            this.currentXp += xp;
        }
        this.levelPriorToCheck.set(this.totalLevel);
        check();
    }

    public Skill<?> getSkill() {
        return skill;
    }

    public int getLevel() {
        return totalLevel;
    }

    public long getTotalXp() {
        return totalXp;
    }

    public int getCurrentXp() {
        return currentXp;
    }

    public long getLevelXpRequirement() {
        return Math.round(1.618033988 * totalLevel * Math.E);
    }

    public int getXpNeededForNextLevel() {
        return (int) getLevelXpRequirement() - currentXp;
    }

    public void writeToPacket(PacketByteBuf packet) {
        packet.writeInt(DexterityData.SKILLS.getRawId(skill)); // First skill{rawId} (INT)
        packet.writeLong(totalXp); // Second totalXp (LONG)
        packet.writeLong(currentXp); // Third currentXp (LONG)
        packet.writeInt(totalLevel); // Fourth totalLevel (INT)
    }

}
