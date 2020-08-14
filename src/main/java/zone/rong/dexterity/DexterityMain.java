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

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zone.rong.dexterity.api.DexterityEntityAttributes;
import zone.rong.dexterity.api.DexterityPackets;
import zone.rong.dexterity.api.DexteritySkills;
import zone.rong.dexterity.rpg.land.LandDeedItem;
import zone.rong.dexterity.rpg.skill.perk.Perk;
import zone.rong.dexterity.rpg.skill.types.Skill;

import static zone.rong.dexterity.DexterityData.*;

public class DexterityMain implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Dexterity");

    @Override
    public void onInitialize() {
        DexterityData.init();
        DexteritySkills.init();
        DexterityEntityAttributes.init();
        DexterityPackets.registerC2SPackets();
        SKILLS.stream().map(Skill::getPerks).forEach(perks -> perks.forEach(Perk::registerExtra));
        Registry.register(Registry.ITEM, LandDeedItem.INSTANCE.identifier, LandDeedItem.INSTANCE);
    }

}
