package zone.rong.dexterity.api.skill;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import org.apache.commons.lang3.tuple.Pair;
import zone.rong.dexterity.Dexterity;
import zone.rong.dexterity.api.DexterityAPI;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public final class SkillCache {

    private static Cache<?, Pair<SkillType, Integer>> internalCache;

    public static void init() {
        Dexterity.INSTANCE.getForgeBus().addListener(SkillCache::onServerStart);
    }

    public static <XP> Pair<SkillType, Integer> retrieveXP(Class<XP> xpClass, XP xpObject) {
        return retrieveXP(xpClass, xpObject, DexterityAPI.Registries.SKILLS.getValues());
    }

    public static <XP> Pair<SkillType, Integer> retrieveXP(Class<XP> xpClass, XP xpObject, Collection<SkillType> skills) {
        try {
            return ((Cache<XP, Pair<SkillType, Integer>>) internalCache).get(xpObject, () -> {
                SkillType type = null;
                int resultXP = 0;
                for (SkillType skillType : skills) {
                    int xp = skillType.getXP(xpClass, xpObject);
                    if (resultXP < xp) {
                        type = skillType;
                        resultXP = xp;
                    }
                }
                return Pair.of(type, resultXP);
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private static void onServerStart(FMLServerStartedEvent event) {
        Dexterity.LOGGER.info("Building cache...");
        internalCache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(64)
                .weakKeys()
                .expireAfterAccess(60, TimeUnit.SECONDS)
                .build();
    }

}
