package xyz.rongmario.dexterity.rpg.skill.types;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemConvertible;

public class CombatSkill extends AbstractToolSkill<CombatSkill, EntityType<?>> {

    public CombatSkill(String path, ItemConvertible displayItem, int colour) {
        super(path, displayItem, colour);
    }

    public CombatSkill(String namespace, String path, ItemConvertible displayItem, int colour) {
        super(namespace, path, displayItem, colour);
    }

}
