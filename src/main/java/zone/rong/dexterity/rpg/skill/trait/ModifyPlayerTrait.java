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

package zone.rong.dexterity.rpg.skill.trait;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import zone.rong.dexterity.rpg.skill.types.Skill;

import java.util.function.Consumer;

public class ModifyPlayerTrait implements Trait {

    protected final String name;
    protected final MutableText displayName;
    protected final Skill<?> parentSkill;
    protected final int levelToUnlock;
    protected final Consumer<ServerPlayerEntity> modify;

    public ModifyPlayerTrait(String name, Skill<?> parentSkill, int levelToUnlock, Consumer<ServerPlayerEntity> modify) {
        this.name = name;
        this.displayName = new TranslatableText("trait.dexterity.".concat(name));
        this.parentSkill = parentSkill;
        this.levelToUnlock = levelToUnlock;
        this.modify = modify;
    }

    public Skill<?> getParentSkill() {
        return parentSkill;
    }

    public void applyModification(ServerPlayerEntity player) {
        modify.accept(player);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int getLevelToUnlock() {
        return levelToUnlock;
    }

    @Override
    public MutableText getName() {
        return displayName;
    }

    @Override
    public int getColour() {
        return parentSkill.getColour();
    }

    @Override
    public ItemStack getDisplayItem() {
        return parentSkill.getDisplayItem();
    }

}
