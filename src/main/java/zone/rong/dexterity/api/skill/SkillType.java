package zone.rong.dexterity.api.skill;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import zone.rong.dexterity.Dexterity;

import java.util.function.Supplier;

public class SkillType extends ForgeRegistryEntry<SkillType> {

    private final TextFormatting colour;
    private final Supplier<ItemStack> associatedItem;

    private final TranslationTextComponent name, description;

    public SkillType(String id, TextFormatting colour, IItemProvider associatedItem) {
        this(Dexterity.MOD_ID, id, colour, associatedItem);
    }

    public SkillType(String domain, String id, TextFormatting colour, IItemProvider associatedItem) {
        this.name = new TranslationTextComponent("skill.dexterity." + domain + "_" + id);
        this.description = new TranslationTextComponent("skill.dexterity." + domain + "_" + id);
        this.colour = colour;
        this.associatedItem = () -> new ItemStack(associatedItem);
        setRegistryName(domain, id);
    }

    public TranslationTextComponent getName() {
        return name;
    }

    public TranslationTextComponent getDescription() {
        return description;
    }

    // TODO: change in 1.16+
    public TextFormatting getColour() {
        return colour;
    }

    public Supplier<ItemStack> getAssociatedItem() {
        return associatedItem;
    }

}
