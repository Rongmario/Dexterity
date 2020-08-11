package xyz.rongmario.dexterity.api;

import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.rongmario.dexterity.DexterityData;
import xyz.rongmario.dexterity.DexterityHelper;
import xyz.rongmario.dexterity.mixin.AccessorLootTable;
import xyz.rongmario.dexterity.rpg.skill.perk.EmptyHandPerk;
import xyz.rongmario.dexterity.rpg.skill.perk.GenericPerk;
import xyz.rongmario.dexterity.rpg.skill.perk.PerkBuilder;
import xyz.rongmario.dexterity.rpg.skill.perk.api.InteractionTrigger;
import xyz.rongmario.dexterity.rpg.skill.types.CombatSkill;
import xyz.rongmario.dexterity.rpg.skill.types.DigSkill;
import xyz.rongmario.dexterity.rpg.skill.types.Skill;

import static net.minecraft.block.Material.DENSE_ICE;
import static net.minecraft.block.Material.METAL;
import static net.minecraft.block.Material.ORGANIC_PRODUCT;
import static net.minecraft.block.Material.REPAIR_STATION;
import static net.minecraft.block.Material.SNOW_BLOCK;
import static net.minecraft.block.Material.SNOW_LAYER;
import static net.minecraft.block.Material.SOIL;
import static net.minecraft.block.Material.SOLID_ORGANIC;
import static net.minecraft.block.Material.STONE;

public class DexteritySkills {

    // Main Skills
    public static final CombatSkill ARCHERY = new CombatSkill("archery", Items.BOW, 7798528);
    public static final DigSkill MINING = new DigSkill("mining", Items.DIAMOND_PICKAXE, 6589843);
    public static final DigSkill EXCAVATION = new DigSkill("excavation", Items.DIAMOND_SHOVEL, 16749568);
    public static final CombatSkill SWORDSMANSHIP = new CombatSkill("swordsmanship", Items.DIAMOND_SWORD, 3264206);
    public static final CombatSkill AXES = new CombatSkill("axes", Items.DIAMOND_AXE, 8061183);
    public static final CombatSkill UNARMED = new CombatSkill("unarmed", Items.BARRIER, 15843965);
    public static final Skill ALCHEMY = new Skill<>("alchemy", Items.POTION, 16716947);
    public static final Skill CULTIVATION = new Skill<>("cultivation", Items.FARMLAND, 16383744);
    public static final Skill AUTOMATION = new Skill<>("automation", Blocks.PISTON, 8750469);
    public static final Skill CRAFTING = new Skill<>("crafting", Blocks.CRAFTING_TABLE, 16752128);
    public static final Skill FISHING = new Skill<>("fishing", Items.FISHING_ROD, 2003199);
    public static final Skill SMITHING = new Skill<>("smithing", Blocks.ANVIL, 12845056);
    public static final Skill<?> ACROBATICS = new Skill<>("acrobatics", Items.ELYTRA, 2599896);

    // Perks
    public static final GenericPerk QUICK_FIRE = PerkBuilder.<GenericPerk>of()
            .readyCondition((player, stack) -> ARCHERY.isToolCompatible(stack))
            .start((player, world, stack) -> {
                stack.getTag().putBoolean(DexterityNBT.Skills.RANGED_QUICK_FIRE, true);
                return ActionResult.PASS;
            })
            .end(((player, world, stack) -> stack.getTag().remove(DexterityNBT.Skills.RANGED_QUICK_FIRE)))
            .perkTrigger(InteractionTrigger.IMMEDIATE)
            .build("quick_fire", ARCHERY, s -> 200, s -> Math.max(100, Math.floorDiv(s, 32)));
    public static final GenericPerk SUPER_BREAK = DexterityHelper.getGenericBlockBreakPerk("super_breaker", MINING);
    public static final GenericPerk GIGA_DRILL_BREAK = DexterityHelper.getGenericBlockBreakPerk("giga_drill_break", EXCAVATION);
    public static final EmptyHandPerk BERSERK = PerkBuilder.<EmptyHandPerk>of()
            .readyCondition((player, stack) -> stack.isEmpty())
            .start((player, world, stack) -> {
                // Fixme TEMP VALUE
                player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addTemporaryModifier(new EntityAttributeModifier(DexterityData.DAMAGE_AMOUNT_PERK_UUID, "Bonus Dexterity Damage", 5.0D, EntityAttributeModifier.Operation.ADDITION));
                return ActionResult.PASS;
            })
            .end((player, world, stack) -> player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).removeModifier(DexterityData.DAMAGE_AMOUNT_PERK_UUID))
            .useHand()
            .perkTrigger(InteractionTrigger.ATTACK_ENTITY)
            .build("berserk", UNARMED, s -> 200, s -> 100);

    public static void init() {
        // QUICK_FIRE = Press and Hold
        ARCHERY.addPerk(QUICK_FIRE).addTool(item -> item instanceof RangedWeaponItem).addEntries(builder -> builder.put(EntityType.CREEPER, 10));
        MINING.addPerk(SUPER_BREAK).addTool(FabricToolTags.PICKAXES).addTagEntries(builder -> builder.put(BlockTags.ANVIL, 200).put(BlockTags.GOLD_ORES, 50));
        EXCAVATION.addPerk(GIGA_DRILL_BREAK).addTool(FabricToolTags.SHOVELS);
        UNARMED.addPerk(BERSERK).addTool(ItemStack.EMPTY);
        SWORDSMANSHIP.addTool(FabricToolTags.SWORDS);
        AXES.addTool(FabricToolTags.AXES);
        ACROBATICS.addTrait(1, player -> player.getDataTracker().set(DexterityEntityTrackers.Player.REAL_BLOCK_REACH, 9.0F));
        prepareAdditionalDropConditions();
    }

    public static void prepareAdditionalDropConditions() {
        LootTableLoadingCallback.EVENT.register(((resourceManager, manager, id, supplier, setter) -> {
            if (id.getPath().startsWith("blocks/")) {
                Identifier registryName = new Identifier(id.getNamespace(), id.getPath().substring(7));
                Block block = Registry.BLOCK.get(registryName);
                Material material = block.getDefaultState().getMaterial();
                if (material == STONE || material == METAL || material == DENSE_ICE || material == REPAIR_STATION) {
                    FabricLootPoolBuilder builder = FabricLootPoolBuilder.builder()
                            .withCondition(new DexterityLootCondition(new DexterityPredicate.Builder().perk(SUPER_BREAK).natural().build()))
                            .rolls(ConstantLootTableRange.create(3));
                    for (LootPool pool : ((AccessorLootTable) manager.getTable(id)).getPools()) {
                        builder.copyFrom(pool);
                    }
                    supplier.pool(builder);
                } else if ((material == SOIL || material == SOLID_ORGANIC || material == ORGANIC_PRODUCT || material == SNOW_LAYER || material == SNOW_BLOCK)) {
                    FabricLootPoolBuilder builder = FabricLootPoolBuilder.builder()
                            .withCondition(new DexterityLootCondition(new DexterityPredicate.Builder().perk(GIGA_DRILL_BREAK).natural().build()))
                            .rolls(ConstantLootTableRange.create(3));
                    for (LootPool pool : ((AccessorLootTable) manager.getTable(id)).getPools()) {
                        builder.copyFrom(pool);
                    }
                    supplier.pool(builder);
                }
            }
        }));
    }

}
