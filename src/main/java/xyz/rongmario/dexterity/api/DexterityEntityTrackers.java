package xyz.rongmario.dexterity.api;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;

public class DexterityEntityTrackers {

    public static class General {

        public static final TrackedData<Float> WIDTH_MULTIPLIER = DataTracker.registerData(AnimalEntity.class, TrackedDataHandlerRegistry.FLOAT);
        public static final TrackedData<Float> HEIGHT_MULTIPLIER = DataTracker.registerData(AnimalEntity.class, TrackedDataHandlerRegistry.FLOAT);

        public static void init() {

        }

    }

    public static class Player {

        public static final TrackedData<Float> MANA = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
        public static final TrackedData<Float> REAL_BLOCK_REACH = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);
        public static final TrackedData<Float> REAL_ENTITY_REACH = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);

        public static void init() {

        }

    }



}
