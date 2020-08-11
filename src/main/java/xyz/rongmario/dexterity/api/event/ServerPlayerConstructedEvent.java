package xyz.rongmario.dexterity.api.event;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;

public interface ServerPlayerConstructedEvent {

    Event<ServerPlayerConstructedEvent> EVENT = EventFactory.createArrayBacked(ServerPlayerConstructedEvent.class,
            listeners -> (player, world, profile, interactionManager) -> {
                for (ServerPlayerConstructedEvent event : listeners) {
                    event.onConstruction(player, world, profile, interactionManager);
                }
            }
    );

    void onConstruction(ServerPlayerEntity player, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager);

}
