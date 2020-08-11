package xyz.rongmario.dexterity.api;

public class DexterityNBT {

    private static final String PREFIX = "Dexterity";

    public static class Skills {

        public static final String RANGED_QUICK_FIRE = PREFIX.concat("RangedQuickFire");

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
