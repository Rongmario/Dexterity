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

package zone.rong.dexterity.rpg.skill.perk;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Clearable;
import net.minecraft.util.Formatting;
import zone.rong.dexterity.DexterityData;
import zone.rong.dexterity.DexterityHelper;
import zone.rong.dexterity.api.DexterityPackets;
import zone.rong.dexterity.rpg.skill.SkillManager;

import java.util.Iterator;
import java.util.Map;

/**
 * Inspired by vanilla's {@link ItemCooldownManager}
 */
public class PerkManager implements Clearable {

    private final ServerPlayerEntity player;
    private final SkillManager skillManager;
    private final CurrentPerk currentPerk;
    private final Map<Perk, Entry> cooldowns = new Object2ObjectOpenHashMap<>();

    private int tick;

    public PerkManager(ServerPlayerEntity player, SkillManager skillManager) {
        this.player = player;
        this.skillManager = skillManager;
        this.currentPerk = CurrentPerk.create(player);
    }

    public void update() {
        ++this.tick;
        if (this.currentPerk.state == PerkState.READY && (this.currentPerk.entry.endTick <= this.tick || !this.currentPerk.perk.readyCondition.test(player, player.getMainHandStack()))) {
            unready();
        } else if ((this.currentPerk.stack != null && this.currentPerk.stack.getItem() != player.getMainHandStack().getItem()) || (this.currentPerk.state == PerkState.ACTIVE && this.currentPerk.entry.endTick <= this.tick)) {
            endActivePerk();
        }
        // Last operation in the update, so the perk cannot be activated on the refreshed tick
        this.cooldowns.entrySet().removeIf(this::reachedCooldown);
    }

    // TODO - this may need fixing or polishing!
    @Override
    public void clear() {
        if (this.currentPerk.state == PerkState.ACTIVE) {
            CompoundTag tag = this.currentPerk.stack.getTag();
            tag.putBoolean(DexterityHelper.DEXTERITY_GLINT_TAG, false);
        } else if (this.currentPerk.state != PerkState.INACTIVE) {
            this.currentPerk.clear();
        }
    }

    public void readyUp(ItemStack stack) {
        if (this.currentPerk.state == PerkState.INACTIVE || !isPerkActive(stack)) {
            final Iterator<Perk> perkIterator = DexterityData.PERKS.stream().iterator();
            while (perkIterator.hasNext()) {
                Perk perk = perkIterator.next();
                if (stack.isEmpty() && !(perk instanceof EmptyHandPerk)) {
                    continue;
                }
                if (perk.readyCondition.test(player, stack) && isPerkReady(perk)) {
                    this.currentPerk.of(perk, PerkState.READY, new Entry(this.tick, this.tick + 80), stack);
                    player.sendMessage(new TranslatableText("message.dexterity.perk.ready").append(getCorrectName()).formatted(Formatting.RED), true);
                    break;
                } else if (!isPerkReady(perk)) {
                    player.sendMessage(perk.displayName.shallowCopy().append(new TranslatableText("message.dexterity.perk.cooldown", Math.round(getCooldown(perk) / 20F))).formatted(Formatting.GRAY), true);
                } else if (isPerkActive(perk)) {
                    player.sendMessage(perk.displayName.shallowCopy().append(new TranslatableText("message.dexterity.perk.still_active", Math.round(getDurationLeft() / 20F))).formatted(Formatting.GOLD), true);
                }
            }
        }
    }

    public void unready() {
        player.sendMessage(new TranslatableText("message.dexterity.perk.lowered").append(getCorrectName()).formatted(Formatting.GRAY), true);
        this.currentPerk.clear();
    }

    public ActionResult setPerkActive() {
        if (this.currentPerk.state == PerkState.READY) {
            Perk perk = this.currentPerk.perk;
            ItemStack stack = this.currentPerk.stack;
            if (!(perk instanceof EmptyHandPerk)) { // Do something for fists!
                stack.getTag().putBoolean(DexterityHelper.DEXTERITY_GLINT_TAG, true);
            }
            this.currentPerk.of(perk, PerkState.ACTIVE, new Entry(this.tick, this.tick + perk.getDuration().applyAsInt(getPerkSkillLevel(perk))), stack);
            return perk.actionStart.callStart(perk, player, player.world, stack);
        }
        return ActionResult.PASS;
    }

    public void endActivePerk() {
        Perk perk = this.currentPerk.perk;
        ItemStack stack = this.currentPerk.stack;
        perk.actionEnd.callEnd(perk, player, player.world, stack); // Execute the perk's ending action
        if (!(perk instanceof EmptyHandPerk)) { // If the current perk is a not an EmptyHandPerk (empty stack)
            CompoundTag tag = stack.getTag();
            tag.remove(DexterityHelper.DEXTERITY_GLINT_TAG);
        }
        this.cooldowns.put(perk, new Entry(this.tick, this.tick + perk.getCooldown().applyAsInt(getPerkSkillLevel(perk)))); // We set the perk to cooldown - so the player cannot use it again in the next tick ~ until cooldown ends
        this.currentPerk.clear(); // And clear the currentPerk
    }

    public boolean isPerkReady(Perk perk) {
        return !this.cooldowns.containsKey(perk);
    }

    public boolean isPerkActive() {
        return this.currentPerk.state == PerkState.ACTIVE;
    }

    public boolean isPerkActive(ItemStack stack) {
        return isPerkActive() && stack.isItemEqual(this.currentPerk.stack);
    }

    public boolean isPerkActive(Perk perk) {
        return isPerkActive() && this.currentPerk.perk == perk;
    }

    public boolean isPlayerReady() {
        return this.currentPerk.state == PerkState.READY;
    }

    public int getDurationLeft() {
        return currentPerk.state != PerkState.ACTIVE ? 0 : currentPerk.entry.endTick - this.tick;
    }

    public int getCooldown(Perk perk) {
        return this.cooldowns.containsKey(perk) ? this.cooldowns.get(perk).endTick - this.tick : 0;
    }

    private boolean reachedCooldown(Map.Entry<Perk, Entry> pair) {
        boolean reached = pair.getValue().endTick <= this.tick;
        if (reached) {
            player.sendMessage(pair.getKey().getName().shallowCopy().append(new TranslatableText("message.dexterity.perk.refresh")).formatted(Formatting.GREEN), true);
        }
        return reached;
    }

    private int getPerkSkillLevel(Perk perk) {
        return skillManager.getSkillEntry(perk.parentSkill).getLevel();
    }

    private Text getCorrectName() {
        return this.currentPerk.stack.isEmpty() ? new TranslatableText("message.dexterity.fist") : this.currentPerk.stack.getName();
    }

    class Entry {

        private final int startTick;
        private final int endTick;

        private Entry(int startTick, int endTick) {
            this.startTick = startTick;
            this.endTick = endTick;
        }

    }

    static class CurrentPerk {

        private final ServerPlayerEntity player;

        private Perk perk;
        private PerkState state;
        private Entry entry;
        private ItemStack stack;

        public static CurrentPerk create(ServerPlayerEntity player) {
            return new CurrentPerk(player, null, PerkState.INACTIVE, null, null);
        }

        private CurrentPerk(ServerPlayerEntity player, Perk perk, PerkState state, Entry entry, ItemStack stack) {
            this.player = player;
            this.perk = perk;
            this.state = state;
            this.entry = entry;
            this.stack = stack;
        }

        void of(Perk perk, PerkState state, Entry entry, ItemStack stack) {
            this.perk = perk;
            this.state = state;
            this.entry = entry;
            this.stack = stack;
            packetProcessing();
        }

        void clear() {
            this.perk = null;
            this.state = PerkState.INACTIVE;
            this.entry = null;
            this.stack = null;
            packetProcessing();
        }

        void packetProcessing() {
            if (state != PerkState.ACTIVE) {
                PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
                packet.writeBoolean(state == PerkState.READY);
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, DexterityPackets.S2C_READY, packet);
            }
        }

    }

    enum PerkState {

        INACTIVE,
        READY,
        ACTIVE;

    }

}
