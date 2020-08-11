package xyz.rongmario.dexterity.rpg.skill.trait;

import net.minecraft.server.network.ServerPlayerEntity;
import xyz.rongmario.dexterity.rpg.skill.types.Skill;

import java.util.function.Consumer;

public class Trait {

    private final Skill<?> parentSkill;
    private final int levelBarrier;
    private final Consumer<ServerPlayerEntity> modify;

    public Trait(Skill<?> parentSkill, int levelBarrier, Consumer<ServerPlayerEntity> modify) {
        this.parentSkill = parentSkill;
        this.levelBarrier = levelBarrier;
        this.modify = modify;
    }

    public Skill<?> getParentSkill() {
        return parentSkill;
    }

    public int getLevelBarrier() {
        return levelBarrier;
    }

    public void applyModification(ServerPlayerEntity player) {
        modify.accept(player);
    }

}
