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

package zone.rong.dexterity;

import com.mojang.serialization.Lifecycle;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import zone.rong.dexterity.rpg.skill.perk.Perk;
import zone.rong.dexterity.rpg.skill.types.Skill;
import zone.rong.dexterity.rpg.skill.common.api.cultivation.Nutrient;

public class DexterityData {

    public static final String PLAYER_UUID_KEY = "DexterityUUID";
    public static final String PLAYER_UUID_XP_KEY = "DexterityUUIDXPStore";

    public static final Identifier MANA_BAR = new Identifier("dexterity", "textures/gui/manabar_new.png");

    public static final RegistryKey<Registry<Skill<?>>> SKILL_KEY = RegistryKey.ofRegistry(new Identifier("dexterity", "skills"));
    public static final RegistryKey<Registry<Perk>> PERK_KEY = RegistryKey.ofRegistry(new Identifier("dexterity", "perks"));
    public static final RegistryKey<Registry<Nutrient>> NUTRIENT_KEY = RegistryKey.ofRegistry(new Identifier("dexterity", "nutrients"));
    public static final SimpleRegistry<Skill<?>> SKILLS = new SimpleRegistry<>(SKILL_KEY, Lifecycle.stable());
    public static final SimpleRegistry<Perk> PERKS = new SimpleRegistry<>(PERK_KEY, Lifecycle.stable());
    public static final SimpleRegistry<Nutrient> NUTRIENTS = new SimpleRegistry<>(NUTRIENT_KEY, Lifecycle.stable());

    public static void init() {

    }

}
