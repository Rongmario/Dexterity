package xyz.rongmario.dexterity.rpg.skill.common.api;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import xyz.rongmario.dexterity.DexterityData;
import xyz.rongmario.dexterity.api.DexteritySkills;
import xyz.rongmario.dexterity.rpg.skill.types.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface XPStore {

    List<Object2ObjectMap<UUID, Int2IntMap>> GLOBAL_STORE = new ArrayList<>();

    default void fromTag(CompoundTag tag, String xpStoreKey) {
        if (tag.contains(DexterityData.PLAYER_UUID_KEY)) {
            registerLastPlayer(tag.getUuid(DexterityData.PLAYER_UUID_KEY));
            final Object2ObjectMap<UUID, Int2IntMap> map = new Object2ObjectOpenHashMap<>();
            if (tag.contains(xpStoreKey)) {
                CompoundTag xpStore = tag.getCompound(xpStoreKey);
                for (String tagKey : xpStore.getKeys()) {
                    Tag currentTag = xpStore.get(tagKey);
                    if (currentTag instanceof CompoundTag) {
                        CompoundTag skillTag = tag;
                        Int2IntOpenHashMap skillToLevelMap = new Int2IntOpenHashMap();
                        map.put(skillTag.getUuid(DexterityData.PLAYER_UUID_KEY), skillToLevelMap);
                        skillToLevelMap.put(DexterityData.SKILLS.getRawId(DexterityData.SKILLS.get(Identifier.tryParse(tagKey))), skillTag.getInt(DexterityData.PLAYER_UUID_XP_KEY));
                    }
                }
            }
            setLocalXpStore(map);
            GLOBAL_STORE.add(map);
        }
    }

    default void toTag(CompoundTag tag, String xpStoreKey) {
        if (getLastUUID() != null) {
            tag.putUuid(DexterityData.PLAYER_UUID_KEY, getLastUUID());
        }
        if (this.getLocalXpStore() != null && !this.getLocalXpStore().isEmpty()) {
            CompoundTag xpStoreTag = new CompoundTag();
            getLocalXpStore().object2ObjectEntrySet().forEach(uuidMap -> uuidMap.getValue().int2IntEntrySet().forEach(e -> {
                CompoundTag skillTag = new CompoundTag();
                skillTag.putUuid(DexterityData.PLAYER_UUID_KEY, uuidMap.getKey());
                skillTag.putInt(DexterityData.PLAYER_UUID_XP_KEY, e.getIntValue());
                skillTag.put(DexterityData.SKILLS.get(e.getIntKey()).toString(), skillTag);
            }));
            tag.put(xpStoreKey, xpStoreTag);
        }
    }

    default void addXp(Skill skill, int xp) {
        PlayerEntity player = ((BlockEntity) this).getWorld().getServer().getPlayerManager().getPlayer(getLastUUID());
        if (player == null) {
            getLocalXpStore().putIfAbsent(getLastUUID(), new Int2IntOpenHashMap());
            getLocalXpStore().get(getLastUUID()).putIfAbsent(DexterityData.SKILLS.getRawId(skill), xp);
            getLocalXpStore().get(getLastUUID()).putIfAbsent(DexterityData.SKILLS.getRawId(DexteritySkills.AUTOMATION), xp);
        } else {
            ((SkillHandler) player).getSkillManager().addXp(skill, xp);
        }
    }

    void registerLastPlayer(UUID playerUuid);

    void registerLastPlayer(PlayerEntity player);

    void setLocalXpStore(Object2ObjectMap<UUID, Int2IntMap> map);

    UUID getLastUUID();

    Object2ObjectMap<UUID, Int2IntMap> getLocalXpStore();

}
