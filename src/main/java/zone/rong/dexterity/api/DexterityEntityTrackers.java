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
