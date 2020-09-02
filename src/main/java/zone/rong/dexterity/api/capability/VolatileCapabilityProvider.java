package zone.rong.dexterity.api.capability;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class VolatileCapabilityProvider<C> implements ICapabilityProvider {

    private final Capability<C> capability;
    protected final LazyOptional<C> lazyCapability;

    public VolatileCapabilityProvider(Capability<C> capability, C instance) {
        this.capability = capability;
        this.lazyCapability = LazyOptional.of(() -> instance);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        return this.capability.orEmpty(capability, this.lazyCapability);
    }

}
