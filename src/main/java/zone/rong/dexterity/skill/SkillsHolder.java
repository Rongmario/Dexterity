package zone.rong.dexterity.skill;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import zone.rong.dexterity.api.skill.SkillType;
import zone.rong.dexterity.api.capability.skill.ISkillsHolder;
import zone.rong.dexterity.networking.DexterityChannel;
import zone.rong.dexterity.networking.packets.S2CLevelUp;

import java.util.Collection;

import static zone.rong.dexterity.api.DexterityAPI.Registries.SKILLS;

public class SkillsHolder implements ISkillsHolder {

    private final ServerPlayerEntity player;

    private final Object2ObjectMap<SkillType, SkillContainer> skills;

    private long totalXP;

    private int level;
    private int currentXP;

    public SkillsHolder(ServerPlayerEntity player) {
        this.player = player;
        this.skills = Util.make(new Object2ObjectOpenHashMap<>(SKILLS.getKeys().size()), map -> SKILLS.forEach(s -> map.put(s, new SkillContainer(s))));
    }

    void check(SkillContainer skillContainer) {
        int difference = getXPNeededForNextLevel(skillContainer.level, skillContainer.currentXP);
        if (difference <= 0) {
            onLevelUp(skillContainer, difference);
            check(skillContainer);
        }
    }

    void onLevelUp(SkillContainer skillContainer, int difference) {
        ++skillContainer.level;
        skillContainer.currentXP = Math.abs(difference);
        MinecraftServer server = player.server;
        if (!(server instanceof IntegratedServer)) {
            if (this.level % 10 == 0) {
                if (this.level % 50 == 0) {
                    server.getPlayerList().getPlayers().stream().filter(p -> p != player).forEach(p -> p.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 0.75F, 1.0F));
                }
                SkillType skill = skillContainer.skillType;
                server.getPlayerList().sendMessage(new TranslationTextComponent("message.dexterity.congratulations", player.getName(), skill.getName(), this.level).applyTextStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, skill.getDescription()))));
            }
        }
        DexterityChannel.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new S2CLevelUp(skillContainer));
    }

    @Override
    public Collection<SkillContainer> getContainers() {
        return ImmutableSet.copyOf(this.skills.values());
    }

    @Override
    public <XP> void addXP(Class<XP> entryClass, XP entry) {
        this.skills.keySet().forEach(s -> addXP(s, s.getXP(entryClass, entry)));
    }

    @Override
    public void addXP(SkillType skillType, int xp) {
        int xpToAdd = player.getLuck() >= 1F ? xp * 2 : xp;
        SkillContainer skillContainer = this.skills.get(skillType);
        skillContainer.currentXP += xpToAdd;
        skillContainer.totalXP += xpToAdd;
        check(skillContainer);
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void setLevel(SkillType skillType, int level) {
        this.skills.get(skillType).level = level;
    }

    @Override
    public void setCurrentXP(int xp) {
        this.currentXP = xp;
    }

    @Override
    public void setCurrentXP(SkillType skillType, int xp) {
        this.skills.get(skillType).currentXP = xp;
    }

    @Override
    public void setTotalXP(long xp) {
        this.totalXP = xp;
    }

    @Override
    public void setTotalXP(SkillType skillType, long xp) {
        this.skills.get(skillType).totalXP = xp;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getLevel(SkillType skillType) {
        return this.skills.get(skillType).level;
    }

    @Override
    public int getCurrentXP() {
        return currentXP;
    }

    @Override
    public int getCurrentXP(SkillType skillType) {
        return this.skills.get(skillType).currentXP;
    }

    @Override
    public long getTotalXP() {
        return totalXP;
    }

    @Override
    public long getTotalXP(SkillType skillType) {
        return this.skills.get(skillType).totalXP;
    }

    // TODO: Subject to change - and made configurable
    long getLevelXPRequirement(int level) {
        return Math.round(1.618033988 * level * Math.E);
    }

    int getXPNeededForNextLevel(int level, int currentXP) {
        return (int) getLevelXPRequirement(level) - currentXP;
    }

}
