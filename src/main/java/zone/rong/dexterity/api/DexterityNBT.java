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

package zone.rong.dexterity.api;

public class DexterityNBT {

    private static final String PREFIX = "Dexterity";

    public static class Skills {

        public static final String RANGED_QUICK_FIRE = PREFIX.concat("RangedQuickFire");

    }

    public static class XPStore {

        private static final String SUFFIX = "XPStore";

        public static final String BREWING_STAND_XP_STORE_KEY = PREFIX.concat("BrewingStand").concat(SUFFIX);

    }

    public static class EntityStats {

        public static final String ENTRY = PREFIX.concat("EntityStatsEntry");
        public static final String WIDTH_MULTIPLIER = PREFIX.concat("WidthMultiplier");
        public static final String HEIGHT_MULTIPLIER = PREFIX.concat("HeightMultiplier");

    }

    public static class Nutrition {

        public static final String ENTRY = PREFIX.concat("NutritionEntry");
        public static final String DIMINISH = PREFIX.concat("NutritionDiminish");
        public static final String PERCENTAGE = PREFIX.concat("NutritionPercentage");

    }

}
