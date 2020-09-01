package zone.rong.dexterity.api.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import zone.rong.dexterity.Dexterity;
import zone.rong.dexterity.api.DexterityAPI;
import zone.rong.dexterity.api.capability.skill.IGlintMarker;
import zone.rong.dexterity.api.capability.skill.ISkillsHolder;
import zone.rong.dexterity.api.skill.SkillType;
import zone.rong.dexterity.skill.GlintMarker;
import zone.rong.dexterity.skill.SkillContainer;
import zone.rong.dexterity.skill.SkillsHolder;

public final class DexterityCapabilities {

    public static final ResourceLocation SKILLS_HOLDER_ID = new ResourceLocation(Dexterity.MOD_ID, "skills_holder");
    public static final ResourceLocation GLINT_MARKER_ID = new ResourceLocation(Dexterity.MOD_ID, "glint_marker");

    @CapabilityInject(ISkillsHolder.class)
    public static final Capability<ISkillsHolder> SKILL_HOLDER;

    @CapabilityInject(IGlintMarker.class)
    public static final Capability<IGlintMarker> GLINT_MARKER;

    static {
        SKILL_HOLDER = DexterityAPI.annotationInjection();
        GLINT_MARKER = DexterityAPI.annotationInjection();
    }

    public static <T extends CapabilityProvider<T>, C> LazyOptional<C> get(T type, Capability<C> capability) {
        return type.getCapability(capability, null);
    }

    public static int getSkillLevel(final PlayerEntity player, SkillType skillType) {
        return player.getCapability(SKILL_HOLDER, player.getHorizontalFacing()).map(holder -> holder.getLevel(skillType)).orElseGet(() -> -1);
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
                    CompoundNBT skillTag = ((CompoundNBT) nbt).getCompound(key);
                    SkillType skillType = DexterityAPI.Registries.SKILLS.getValue(ResourceLocation.tryCreate(key));
                    instance.setLevel(skillType, skillTag.getInt(DexterityAPI.NBT.LEVEL_TAG));
                    instance.setCurrentXP(skillType, skillTag.getInt(DexterityAPI.NBT.CURRENT_XP_TAG));
                    instance.setTotalXP(skillType, skillTag.getInt(DexterityAPI.NBT.TOTAL_XP_TAG));
                }
            }

        }, () -> new SkillsHolder(null));

        CapabilityManager.INSTANCE.register(IGlintMarker.class, getVolatileStorage(), GlintMarker::new);

        DexterityAPI.attachPlayerCapability(true, event -> {
            final ISkillsHolder skillsHolder = new SkillsHolder((ServerPlayerEntity) event.getObject());
            event.addCapability(SKILLS_HOLDER_ID, createProvider(SKILL_HOLDER, skillsHolder, false));
        });
        DexterityAPI.onPlayerClone(true, event -> get(event.getOriginal(), SKILL_HOLDER).ifPresent(original -> get(event.getPlayer(), SKILL_HOLDER).ifPresent(current -> {
            current.setLevel(original.getLevel());
            current.setCurrentXP(original.getCurrentXP());
            current.setTotalXP(original.getTotalXP());
            original.getContainers().forEach(container -> {
                current.setLevel(container.skillType, container.level);
                current.setCurrentXP(container.skillType, container.currentXP);
                current.setTotalXP(container.skillType, container.totalXP);
            });
        })));

        DexterityAPI.attachCapability(ItemStack.class, event -> event.addCapability(GLINT_MARKER_ID, createProvider(GLINT_MARKER, new GlintMarker(event.getObject()), true)));

    }

    private static <T> Capability.IStorage<T> getVolatileStorage() {
        return new Capability.IStorage<T>() {
            @Override
            public INBT writeNBT(Capability<T> capability, T instance, Direction side) { return null; }
            @Override
            public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) { }
        };
    }

}
