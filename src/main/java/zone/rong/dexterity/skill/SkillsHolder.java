package zone.rong.dexterity.skill;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import org.apache.commons.lang3.tuple.Pair;
import zone.rong.dexterity.api.skill.SkillType;
import zone.rong.dexterity.api.capability.skill.ISkillsHolder;
import zone.rong.dexterity.networking.DexterityChannel;
import zone.rong.dexterity.networking.packets.S2CLevelUp;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static zone.rong.dexterity.api.DexterityAPI.Registries.SKILLS;

public class SkillsHolder implements ISkillsHolder {

    private final ServerPlayerEntity player;

    private final Object2ObjectMap<SkillType, SkillContainer> skills;
    private final Cache<?, Pair<SkillType, Integer>> internalCache;

    private long totalXP;

    private int level;
    private int currentXP;

    public SkillsHolder(ServerPlayerEntity player) {
        this.player = player;
        this.skills = Util.make(new Object2ObjectOpenHashMap<>(SKILLS.getKeys().size()), map -> SKILLS.forEach(s -> map.put(s, new SkillContainer(s))));
        this.internalCache = CacheBuilder.newBuilder().concurrencyLevel(2).maximumSize(32).weakKeys().expireAfterAccess(30, TimeUnit.SECONDS).build();
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
            if (skillContainer.level % 10 == 0) {
                if (skillContainer.level % 50 == 0) {
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
    public <XP, C> int addXP(Class<XP> xpClass, XP xpObject, Class<C> compatibleClass, C compatibleObject) {
        if (this.skills.keySet().stream().anyMatch(s -> s.isCompatible(compatibleClass, compatibleObject))) {
            return addXP(xpClass, xpObject);
        }
        return -1;
    }

    @Override
    public <XP> int addXP(Class<XP> xpClass, XP xpObject) {
        try {
            Pair<SkillType, Integer> pair = ((Cache<XP, Pair<SkillType, Integer>>) internalCache).get(xpObject, () -> {
                SkillType type = null;
                int resultXP = 0;
                for (SkillType skillType : this.skills.keySet()) {
                    int xp = skillType.getXP(xpClass, xpObject);
                    if (resultXP < xp) {
                        type = skillType;
                        resultXP = xp;
                    }
                }
                if (type != null) {
                    addXP(type, resultXP);
                }
                return Pair.of(type, resultXP);
            });
            return pair.getRight() != 0 ? addXP(pair.getLeft(), pair.getRight()) : 0;
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @Override
    public int addXP(SkillType skillType, int xp) {
        int xpToAdd = player.getLuck() >= 1F ? xp * 2 : xp;
        SkillContainer skillContainer = this.skills.get(skillType);
        skillContainer.addXP(xpToAdd);
        check(skillContainer);
        return xpToAdd;
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

    // TODO: Make configurable somehow, also -> limit break (bonus when limit broken, player can limit break 10 times...) ?
    long getLevelXPRequirement(int level) {
        return 2 * level * (int) Math.ceil((double) level / 100);
    }

    int getXPNeededForNextLevel(int level, int currentXP) {
        return (int) getLevelXPRequirement(level) - currentXP;
    }

}
