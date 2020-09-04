package zone.rong.dexterity.api.capability.tileentity;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.tuple.Pair;
import zone.rong.dexterity.api.DexterityAPI;
import zone.rong.dexterity.api.capability.DexterityCapabilities;
import zone.rong.dexterity.api.skill.SkillCache;
import zone.rong.dexterity.api.skill.SkillType;

import java.util.UUID;

public interface IXPStore {

    TileEntity getTileEntity();

    void registerLastPlayer(UUID playerUuid);

    void registerLastPlayer(PlayerEntity player);

    UUID getLastUUID();

    Object2ObjectMap<UUID, Object2IntMap<SkillType>> getLocalXPCache();

    default <XP, C> int addXP(Class<XP> xpClass, XP xpObject, Class<C> compatibleClass, C compatibleObject) {
        if (DexterityAPI.Registries.SKILLS.getValues().stream().anyMatch(s -> s.isCompatible(compatibleClass, compatibleObject))) {
            return addXP(xpClass, xpObject);
        }
        return -1;
    }

    /**
     * This miraculous method adds xp straight to the player if they're online.
     * If they're offline, they will be stored, and when they next login, they will be awarded available xp.
     * TODO: The available xp ratio can be modified via config.
     */
    default <XP> int addXP(Class<XP> xpClass, XP xpObject) {
        final Pair<SkillType, Integer> pair = SkillCache.retrieveXP(xpClass, xpObject);
        if (pair.getLeft() != null) {
            PlayerEntity player = getLastPlayer();
            if (player == null) {
                getLocalXPCache().computeIfAbsent(getLastUUID(), s -> new Object2IntOpenHashMap<>()).put(pair.getLeft(), Math.max(pair.getRight() / 2, 1));
            } else {
                player.getCapability(DexterityCapabilities.SKILL_HOLDER).ifPresent(holder -> holder.addXP(pair.getLeft(), pair.getRight()));
            }
        }
        return pair.getRight();
    }

    default PlayerEntity getLastPlayer() {
        UUID uuid = getLastUUID();
        return uuid != null ? getTileEntity().getWorld().getServer().getPlayerList().getPlayerByUUID(uuid) : null;
    }

}
