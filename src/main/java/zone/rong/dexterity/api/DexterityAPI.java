package zone.rong.dexterity.api;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.registries.*;
import zone.rong.dexterity.Dexterity;
import zone.rong.dexterity.api.skill.SkillType;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class DexterityAPI {

    public static <T> T annotationInjection() {
        return null;
    }

    public static <T extends IForgeRegistryEntry<T>> int getRawID(T entry, Class<T> clazz) {
        return ((ForgeRegistry<T>) RegistryManager.ACTIVE.getRegistry(clazz)).getID(entry);
    }

    public static <T extends IForgeRegistryEntry<T>> int getRawID(IForgeRegistry<T> registry, T entry) {
        return ((ForgeRegistry<T>) registry).getID(entry);
    }

    public static <T extends IForgeRegistryEntry<T>> T getFromRaw(Class<T> clazz, int id) {
        return ((ForgeRegistry<T>) RegistryManager.ACTIVE.getRegistry(clazz)).getValue(id);
    }

    public static <T extends IForgeRegistryEntry<T>> T getFromRaw(IForgeRegistry<T> registry, int id) {
        return ((ForgeRegistry<T>) registry).getValue(id);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Entity, P extends PlayerEntity> void attachPlayerCapability(boolean serverOnly, Consumer<AttachCapabilitiesEvent<P>> event) {
        Dexterity.INSTANCE.getForgeBus().addGenericListener(Entity.class, (final AttachCapabilitiesEvent<E> e) -> {
            if (e.getObject() instanceof PlayerEntity) {
                if (serverOnly) {
                    if (e.getObject() instanceof ServerPlayerEntity) {
                        event.accept((AttachCapabilitiesEvent<P>) e);
                    }
                } else {
                    event.accept((AttachCapabilitiesEvent<P>) e);
                }
            }
        });
    }

    public static <T> void attachCapability(Class<T> clazz, Consumer<AttachCapabilitiesEvent<T>> event) {
        Dexterity.INSTANCE.getForgeBus().addGenericListener(clazz, event);
    }

    public static void onPlayerClone(boolean onDeath, Consumer<PlayerEvent.Clone> event) {
        Dexterity.INSTANCE.getForgeBus().addListener((final PlayerEvent.Clone e) -> {
            if (onDeath) {
                if (e.isWasDeath()) {
                    event.accept(e);
                }
            } else {
                event.accept(e);
            }
        });
    }

    public static class Registries {

        public static final IForgeRegistry<SkillType> SKILLS = new RegistryBuilder<SkillType>()
                .allowModification()
                .disableOverrides()
                .setName(new ResourceLocation(Dexterity.MOD_ID, "skill_type"))
                .setType(SkillType.class)
                .create();

        public static void initRegistries() { }

    }

    public static class Skills {

        public static final SkillType ALCHEMY = new SkillType("alchemy", TextFormatting.RED, Items.POTION);
        public static final SkillType MINING = new SkillType("mining", TextFormatting.GRAY, Items.DIAMOND_PICKAXE);

        public static void init(final IForgeRegistry<SkillType> registry) {
            initAlchemy();
            initMining();
            registry.registerAll(ALCHEMY, MINING);
        }

        private static void initAlchemy() {
            ALCHEMY.addCompatibility(Block.class, Blocks.BREWING_STAND);
            ALCHEMY.addPredicateXP(Potion.class, p -> p.getEffects().stream().anyMatch(e -> e.getPotion().isBeneficial()), 20);
        }

        private static void initMining() {
            MINING.addPredicateCompatibility(Item.class, PickaxeItem.class::isInstance);
            MINING.addTagXP(Block.class, Tags.Blocks.STONE, 1)
                    .addXP(Block.class, Blocks.NETHERRACK, 2)
                    .addXP(Block.class, Blocks.END_STONE, 5)
                    .addXP(Block.class, Blocks.GLOWSTONE, 20)
                    .addXP(Block.class, Blocks.NETHER_BRICKS, 10)
                    .addXP(Block.class, Blocks.MAGMA_BLOCK, 20)
                    .addXP(Block.class, Blocks.OBSIDIAN, 20)
                    .addXP(Block.class, Blocks.PRISMARINE, 35)
                    .addXP(Block.class, Blocks.PRISMARINE_BRICKS, 35)
                    .addXP(Block.class, Blocks.PRISMARINE_SLAB, 40)
                    .addXP(Block.class, Blocks.PRISMARINE_STAIRS, 40)
                    .addXP(Block.class, Blocks.PRISMARINE_WALL, 40)
                    .addXP(Block.class, Blocks.PRISMARINE_BRICK_STAIRS, 40)
                    .addXP(Block.class, Blocks.COAL_ORE, 50)
                    .addXP(Block.class, Blocks.DARK_PRISMARINE, 80)
                    .addXP(Block.class, Blocks.DARK_PRISMARINE_SLAB, 80)
                    .addXP(Block.class, Blocks.DARK_PRISMARINE_STAIRS, 80)
                    // .addXP(Block.class, Blocks.CRYING_OBSIDIAN, 250) TODO: 1.16
                    .addXP(Block.class, Blocks.IRON_ORE, 100)
                    // .addXP(Block.class, Blocks.NETHER_GOLD_ORE, 300) TODO: 1.16
                    .addXP(Block.class, Blocks.COAL_BLOCK, 200)
                    .addXP(Block.class, Blocks.GOLD_ORE, 250)
                    .addXP(Block.class, Blocks.IRON_BLOCK, 300)
                    .addXP(Block.class, Blocks.DIAMOND_ORE, 500)
                    .addXP(Block.class, Blocks.ANVIL, 600)
                    .addXP(Block.class, Blocks.GOLD_BLOCK, 750)
                    .addXP(Block.class, Blocks.EMERALD_ORE, 1000)
                    .addXP(Block.class, Blocks.DIAMOND_BLOCK, 1500)
                    .addXP(Block.class, Blocks.EMERALD_BLOCK, 3000)
                    .addXP(Material.class, Material.PACKED_ICE, 5)
                    .addXP(Material.class, Material.IRON, 10)
                    .addXP(Material.class, Material.REDSTONE_LIGHT, 100)
                    .addXP(Material.class, Material.SHULKER, 500)
                    .addXP(Material.class, Material.ROCK, 2); // TODO: 1.16 - REPAIR_STATION

        }

    }

    public static class NBT {

        public static final String PLAYER_STATS_TAG = "Stats";

        public static final String LEVEL_TAG = "Level";
        public static final String CURRENT_XP_TAG = "CurrentXp";
        public static final String TOTAL_XP_TAG = "TotalXp";

        public static final String ARTIFICIAL_BLOCKS_STORE_TAG = "ArtificialBlocks";

        public static final String XP_CACHE_TAG = "XPCache";

        public static final String GLINT_MARKER_TAG = "Glint";

        public static final String COOLDOWNS_TAG = "Cooldowns";
        public static final String PERK_COOLDOWN_TAG = "PerkCooldown";

    }

}
