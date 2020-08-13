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

package zone.rong.dexterity.api;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import zone.rong.dexterity.DexterityHelper;
import zone.rong.dexterity.rpg.skill.perk.EmptyHandPerk;
import zone.rong.dexterity.rpg.skill.perk.GenericPerk;
import zone.rong.dexterity.rpg.skill.perk.PerkBuilder;
import zone.rong.dexterity.rpg.skill.perk.api.InteractionTrigger;
import zone.rong.dexterity.rpg.skill.types.CombatSkill;
import zone.rong.dexterity.rpg.skill.types.DigSkill;
import zone.rong.dexterity.rpg.skill.types.Skill;

import static net.minecraft.block.Material.*;

public class DexteritySkills {

    // Main Skills
    public static final CombatSkill ARCHERY = new CombatSkill("archery", Items.BOW, 7798528);
    public static final DigSkill MINING = new DigSkill("mining", Items.DIAMOND_PICKAXE, 6589843);
    public static final DigSkill EXCAVATION = new DigSkill("excavation", Items.DIAMOND_SHOVEL, 16749568);
    public static final CombatSkill SWORDSMANSHIP = new CombatSkill("swordsmanship", Items.DIAMOND_SWORD, 3264206);
    public static final CombatSkill AXES = new CombatSkill("axes", Items.DIAMOND_AXE, 8061183);
    public static final CombatSkill UNARMED = new CombatSkill("unarmed", Items.BARRIER, 15843965);
    public static final Skill<?> ALCHEMY = new Skill<>("alchemy", Items.POTION, 16716947);
    public static final Skill<?> CULTIVATION = new Skill<>("cultivation", Items.FARMLAND, 16383744);
    public static final Skill<?> AUTOMATION = new Skill<>("automation", Blocks.PISTON, 8750469);
    public static final DigSkill CARPENTRY = new DigSkill("carpentry", Blocks.CRAFTING_TABLE, 16752128);
    public static final Skill<?> FISHING = new Skill<>("fishing", Items.FISHING_ROD, 2003199);
    public static final Skill<?> SMITHING = new Skill<>("smithing", Blocks.ANVIL, 12845056);
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
                player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addTemporaryModifier(new EntityAttributeModifier(DexterityEntityAttributes.DAMAGE_AMOUNT_PERK_UUID, "Bonus Dexterity Damage", 5.0D, EntityAttributeModifier.Operation.ADDITION));
                return ActionResult.PASS;
            })
            .end((player, world, stack) -> player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).removeModifier(DexterityEntityAttributes.DAMAGE_AMOUNT_PERK_UUID))
            .useHand()
            .perkTrigger(InteractionTrigger.ATTACK_ENTITY)
            .build("berserk", UNARMED, s -> 200, s -> 100);

    public static void init() {
        initAcrobatics();
        initArchery();
        initAxes();
        initCarpentry();
        initExcavation();
        initMining();
        initSwordsmanship();
        initUnarmed();
        // prepareAdditionalDropConditions();
    }

    public static void initAcrobatics() {
        ACROBATICS
                .addTrait("graceful_landing", 0)
                .addTrait("extended_block_reach_0", 500, player -> player.getDataTracker().set(DexterityEntityTrackers.Player.REAL_BLOCK_REACH, 6.0F))
                .addTrait("extended_block_reach_1", 1000, player -> player.getDataTracker().set(DexterityEntityTrackers.Player.REAL_BLOCK_REACH, 7.0F));
    }

    private static void initArchery() {
        // QUICK_FIRE = Press and Hold
        ARCHERY
                .addPerk(QUICK_FIRE)
                .addTool(item -> item instanceof RangedWeaponItem)
                .addEntries(builder -> builder.put(EntityType.CREEPER, 10));
    }

    private static void initAxes() {
        AXES
                .addTool(FabricToolTags.AXES);
    }

    private static void initCarpentry() {
        CARPENTRY
                .addTool(FabricToolTags.AXES)
                .addMaterials(BAMBOO, BAMBOO_SAPLING, NETHER_WOOD, WOOD)
                .addTagEntries(builder -> builder
                        .put(BlockTags.OAK_LOGS, 50)
                        .put(BlockTags.SPRUCE_LOGS, 60)
                        .put(BlockTags.ACACIA_LOGS, 70)
                        .put(BlockTags.BIRCH_LOGS, 70)
                        .put(BlockTags.PLANKS, 80)
                        .put(BlockTags.DARK_OAK_LOGS, 85));
    }

    private static void initExcavation() {
        EXCAVATION
                .addTrait("archaeology", 100)
                .addPerk(GIGA_DRILL_BREAK)
                .addTool(FabricToolTags.SHOVELS)
                .addMaterials(AGGREGATE, ORGANIC_PRODUCT, SNOW_BLOCK, SNOW_LAYER, SOIL)
                .addEntries(builder -> builder
                        .put(Blocks.SNOW, 1)
                        .put(Blocks.DIRT, 20)
                        .put(Blocks.COARSE_DIRT, 20)
                        .put(Blocks.PODZOL, 20)
                        .put(Blocks.GRASS_BLOCK, 20)
                        .put(Blocks.SAND, 25)
                        .put(Blocks.RED_SAND, 25)
                        .put(Blocks.SNOW_BLOCK, 25)
                        .put(Blocks.GRAVEL, 30)
                        .put(Blocks.CLAY, 30)
                        .put(Blocks.SOUL_SAND, 40)
                        .put(Blocks.MYCELIUM, 50));
    }

    private static void initMining() {
        MINING
                .addTrait("double_drops", 100)
                .addTrait("blast_mining", 200)
                .addPerk(SUPER_BREAK)
                .addTool(FabricToolTags.PICKAXES)
                .addMaterials(DENSE_ICE, METAL, REDSTONE_LAMP, SHULKER_BOX, STONE, REPAIR_STATION)
                .addTagEntries(builder -> builder.put(BlockTags.ANVIL, 200).put(BlockTags.GOLD_ORES, 50));
    }

    private static void initSwordsmanship() {
        SWORDSMANSHIP
                .addTool(FabricToolTags.SWORDS);
    }

    private static void initUnarmed() {
        UNARMED
                .addTrait("extended_entity_reach_0", 500, player -> player.getDataTracker().set(DexterityEntityTrackers.Player.REAL_ENTITY_REACH, 6.0F))
                .addTrait("extended_entity_reach_1", 1000, player -> player.getDataTracker().set(DexterityEntityTrackers.Player.REAL_ENTITY_REACH, 7.0F))
                .addPerk(BERSERK)
                .addTool(ItemStack.EMPTY);
    }

    /*
    private static void prepareAdditionalDropConditions() {
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
     */

}
