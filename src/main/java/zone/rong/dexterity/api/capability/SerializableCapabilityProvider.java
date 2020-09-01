package zone.rong.dexterity.api.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class SerializableCapabilityProvider<C> implements ICapabilitySerializable<INBT> {

    private final Capability<C> capability;
    private final C instance;
    protected final LazyOptional<C> lazyCapability;

    public SerializableCapabilityProvider(Capability<C> capability, C instance) {
        this.capability = capability;
        this.instance = instance;
        this.lazyCapability = LazyOptional.of(() -> instance);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        return this.capability.orEmpty(capability, this.lazyCapability);
    }

    @Override
    public INBT serializeNBT() {
        return instance == null ? null : capability.writeNBT(instance, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        if (instance != null) {
            capability.readNBT(instance, null, nbt);
        }
    }

}
