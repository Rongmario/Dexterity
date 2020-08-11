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

package zone.rong.dexterity.rpg.skill.types;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.ItemConvertible;

import java.util.Arrays;
import java.util.Set;

public class DigSkill extends AbstractToolSkill<DigSkill, Block> {

    // TODO: decide if this is needed or we can call ItemStack#isEffectiveOn
    protected final Set<Material> applicableMaterials = new ObjectOpenHashSet<>();

    public DigSkill(String path, ItemConvertible displayItem, int colour) {
        super(path, displayItem, colour);
    }

    public DigSkill(String namespace, String path, ItemConvertible displayItem, int colour) {
        super(namespace, path, displayItem, colour);
    }

    public DigSkill addMaterials(Material... materials) {
        this.applicableMaterials.addAll(Arrays.asList(materials));
        return self();
    }

    public DigSkill addMaterial(Material material) {
        this.applicableMaterials.add(material);
        return self();
    }

    public boolean isMaterialApplicable(BlockState state) {
        return this.applicableMaterials.contains(state.getMaterial());
    }

}
