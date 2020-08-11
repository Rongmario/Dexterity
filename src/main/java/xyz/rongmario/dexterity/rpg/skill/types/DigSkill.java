package xyz.rongmario.dexterity.rpg.skill.types;

import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;

public class DigSkill extends AbstractToolSkill<DigSkill, Block> {

    public DigSkill(String path, ItemConvertible displayItem, int colour) {
        super(path, displayItem, colour);
    }

    public DigSkill(String namespace, String path, ItemConvertible displayItem, int colour) {
        super(namespace, path, displayItem, colour);
    }

    /*
    // Apply this if A is BlockState -> this computes all Blocks and gets all their states.
    public DigSkill addBlockEntries(UnaryOperator<ImmutableMap.Builder<Block, Integer>> entries) {
        for (Map.Entry<Block, Integer> entry : entries.apply(new ImmutableMap.Builder<>()).build().entrySet()) {
            for (BlockState entryState : entry.getKey().getStateManager().getStates()) {
                this.affectedEntries.put(entryState, entry.getValue().intValue());
            }
        }
        return self();
    }
     */

}
