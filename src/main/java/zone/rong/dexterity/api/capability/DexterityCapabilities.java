package zone.rong.dexterity.api.capability;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.*;
import zone.rong.dexterity.Dexterity;
import zone.rong.dexterity.api.DexterityAPI;
import zone.rong.dexterity.api.capability.world.IArtificialBlocksStore;
import zone.rong.dexterity.api.capability.skill.IGlintMarker;
import zone.rong.dexterity.api.capability.skill.ISkillsHolder;
import zone.rong.dexterity.api.skill.SkillType;
import zone.rong.dexterity.skill.GlintMarker;
import zone.rong.dexterity.skill.SkillContainer;
import zone.rong.dexterity.skill.SkillsHolder;
import zone.rong.dexterity.world.ArtificialBlocksStore;

public final class DexterityCapabilities {

    public static final ResourceLocation SKILLS_HOLDER_ID = new ResourceLocation(Dexterity.MOD_ID, "skills_holder");
    public static final ResourceLocation GLINT_MARKER_ID = new ResourceLocation(Dexterity.MOD_ID, "glint_marker");
    public static final ResourceLocation ARTIFICIAL_BLOCKS_STORE_ID = new ResourceLocation(Dexterity.MOD_ID, "artificial_blocks_store");

    @CapabilityInject(ISkillsHolder.class)
    public static final Capability<ISkillsHolder> SKILL_HOLDER;

    @CapabilityInject(IGlintMarker.class)
    public static final Capability<IGlintMarker> GLINT_MARKER;

    @CapabilityInject(IArtificialBlocksStore.class)
    public static final Capability<IArtificialBlocksStore> ARTIFICIAL_BLOCKS_STORE;

    static {
        SKILL_HOLDER = DexterityAPI.annotationInjection();
        GLINT_MARKER = DexterityAPI.annotationInjection();
        ARTIFICIAL_BLOCKS_STORE = DexterityAPI.annotationInjection();
    }

    public static <C> ICapabilityProvider createProvider(Capability<C> capability, final C instance, boolean simple) {
        return simple ? new VolatileCapabilityProvider<>(capability, instance) : new SerializableCapabilityProvider<>(capability, instance);
    }

    public static void register() {
        Dexterity.LOGGER.info("Juicy capabilities are being registered right now...");

        CapabilityManager.INSTANCE.register(ISkillsHolder.class, new Capability.IStorage<ISkillsHolder>() {

            @Override
            public INBT writeNBT(Capability<ISkillsHolder> capability, ISkillsHolder instance, Direction side) {
                CompoundNBT holderTag = new CompoundNBT();
                CompoundNBT playerTag = new CompoundNBT();
                playerTag.putInt(DexterityAPI.NBT.LEVEL_TAG, instance.getLevel());
                playerTag.putInt(DexterityAPI.NBT.CURRENT_XP_TAG, instance.getCurrentXP());
                playerTag.putLong(DexterityAPI.NBT.TOTAL_XP_TAG, instance.getTotalXP());
                holderTag.put(DexterityAPI.NBT.PLAYER_STATS_TAG, playerTag);
                for (SkillContainer container : instance.getContainers()) {
                    CompoundNBT skillTag = new CompoundNBT();
                    skillTag.putInt(DexterityAPI.NBT.LEVEL_TAG, container.level);
                    skillTag.putInt(DexterityAPI.NBT.CURRENT_XP_TAG, container.currentXP);
                    skillTag.putLong(DexterityAPI.NBT.TOTAL_XP_TAG, container.totalXP);
                    holderTag.put(container.skillType.getRegistryName().toString(), skillTag);
                }
                return holderTag;
            }

            @Override
            public void readNBT(Capability<ISkillsHolder> capability, ISkillsHolder instance, Direction side, INBT nbt) {
                for (String key : ((CompoundNBT) nbt).keySet()) {
                    if (key.equals(DexterityAPI.NBT.PLAYER_STATS_TAG)) {
                        CompoundNBT playerTag = ((CompoundNBT) nbt).getCompound(key);
                        instance.setLevel(playerTag.getInt(DexterityAPI.NBT.LEVEL_TAG));
                        instance.setCurrentXP(playerTag.getInt(DexterityAPI.NBT.CURRENT_XP_TAG));
                        instance.setTotalXP(playerTag.getInt(DexterityAPI.NBT.TOTAL_XP_TAG));
                    } else {
                        CompoundNBT skillTag = ((CompoundNBT) nbt).getCompound(key);
                        SkillType skillType = DexterityAPI.Registries.SKILLS.getValue(ResourceLocation.tryCreate(key));
                        instance.setLevel(skillType, skillTag.getInt(DexterityAPI.NBT.LEVEL_TAG));
                        instance.setCurrentXP(skillType, skillTag.getInt(DexterityAPI.NBT.CURRENT_XP_TAG));
                        instance.setTotalXP(skillType, skillTag.getInt(DexterityAPI.NBT.TOTAL_XP_TAG));
                    }
                }
            }

        }, () -> new SkillsHolder(null));
        DexterityAPI.attachPlayerCapability(true, event -> {
            final ISkillsHolder skillsHolder = new SkillsHolder((ServerPlayerEntity) event.getObject());
            event.addCapability(SKILLS_HOLDER_ID, createProvider(SKILL_HOLDER, skillsHolder, false));
        });
        DexterityAPI.onPlayerClone(true, event -> event.getOriginal().getCapability(SKILL_HOLDER).ifPresent(original -> event.getPlayer().getCapability(SKILL_HOLDER).ifPresent(current -> {
            current.setLevel(original.getLevel());
            current.setCurrentXP(original.getCurrentXP());
            current.setTotalXP(original.getTotalXP());
            original.getContainers().forEach(container -> {
                current.setLevel(container.skillType, container.level);
                current.setCurrentXP(container.skillType, container.currentXP);
                current.setTotalXP(container.skillType, container.totalXP);
            });
        })));

        CapabilityManager.INSTANCE.register(IGlintMarker.class, getVolatileStorage(), GlintMarker::new);
        DexterityAPI.attachCapability(ItemStack.class, event -> {
            if (DexterityAPI.Registries.SKILLS.getValues().stream().anyMatch(s -> s.isCompatible(Item.class, event.getObject().getItem()))) {
                event.addCapability(GLINT_MARKER_ID, createProvider(GLINT_MARKER, new GlintMarker(event.getObject()), true));
            }
        });

        CapabilityManager.INSTANCE.register(IArtificialBlocksStore.class, new Capability.IStorage<IArtificialBlocksStore>() {

            @Override
            public INBT writeNBT(Capability<IArtificialBlocksStore> capability, IArtificialBlocksStore instance, Direction side) {
                CompoundNBT posStore = new CompoundNBT();
                posStore.putLongArray(DexterityAPI.NBT.ARTIFICIAL_BLOCKS_STORE_TAG, instance.getArtificialPosSet().toLongArray());
                return posStore;
            }

            @Override
            public void readNBT(Capability<IArtificialBlocksStore> capability, IArtificialBlocksStore instance, Direction side, INBT nbt) {
                for (long pos : ((CompoundNBT) nbt).getLongArray(DexterityAPI.NBT.ARTIFICIAL_BLOCKS_STORE_TAG)) {
                    instance.addArtificialEntry(pos);
                }
            }

        }, () -> new ArtificialBlocksStore(null));
        DexterityAPI.attachCapability(World.class, event -> event.addCapability(ARTIFICIAL_BLOCKS_STORE_ID, createProvider(ARTIFICIAL_BLOCKS_STORE, new ArtificialBlocksStore(event.getObject()), false)));

    }

    private static <T> Capability.IStorage<T> getVolatileStorage() {
        return new Capability.IStorage<T>() {
            public INBT writeNBT(Capability<T> capability, T instance, Direction side) { return null; }
            public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) { }
        };
    }

}
