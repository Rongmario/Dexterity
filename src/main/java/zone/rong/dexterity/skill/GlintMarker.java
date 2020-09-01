package zone.rong.dexterity.skill;

import net.minecraft.item.ItemStack;
import zone.rong.dexterity.api.capability.skill.IGlintMarker;

public class GlintMarker implements IGlintMarker {

    private final ItemStack stack;

    private boolean glint;

    public GlintMarker() {
        this.stack = ItemStack.EMPTY;
    }

    public GlintMarker(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean hasGlint() {
        return glint;
    }

    @Override
    public void setGlint(boolean glint) {
        this.glint = glint;
    }

}
