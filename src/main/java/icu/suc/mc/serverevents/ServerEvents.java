package icu.suc.mc.serverevents;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.mutable.MutableObject;

public final class ServerEvents implements ModInitializer {

    @Override
    public void onInitialize() {}

    public final static class Player {
        /**
         * @see ServerPlayerEvents#COPY_FROM
         */
        @FabricAPI
        public static final Event<ServerPlayerEvents.CopyFrom> COPY_FROM = ServerPlayerEvents.COPY_FROM;

        /**
         * @see ServerPlayerEvents#AFTER_RESPAWN
         */
        @FabricAPI
        public static final Event<ServerPlayerEvents.AfterRespawn> AFTER_RESPAWN = ServerPlayerEvents.AFTER_RESPAWN;

        /**
         * @see ServerPlayerEvents#ALLOW_DEATH
         */
        @Deprecated
        @FabricAPI
        public static final Event<ServerPlayerEvents.AllowDeath> ALLOW_DEATH = ServerPlayerEvents.ALLOW_DEATH;

        /**
         * An event that is called when a player joins a server
         */
        public static final Event<ServerEvents.Player.Join> JOIN = EventFactory.createArrayBacked(ServerEvents.Player.Join.class, callbacks -> (player, message) -> {
            for (Join callback : callbacks) {
                callback.onJoin(player, message);
            }
        });

        @FunctionalInterface
        public interface Join {
            /**
             * Called when a player joins a server
             *
             * @param player the player
             * @param message the join message
             */
            void onJoin(ServerPlayer player, MutableObject<Component> message);
        }
    }

    public final static class PlayConnection {
        /**
         * @see ServerPlayConnectionEvents#INIT
         */
        @FabricAPI
        public static final Event<ServerPlayConnectionEvents.Init> INIT = ServerPlayConnectionEvents.INIT;

        /**
         * @see ServerPlayConnectionEvents#JOIN
         */
        @FabricAPI
        public static final Event<ServerPlayConnectionEvents.Join> JOIN = ServerPlayConnectionEvents.JOIN;

        /**
         * @see ServerPlayConnectionEvents#DISCONNECT
         */
        @FabricAPI
        public static final Event<ServerPlayConnectionEvents.Disconnect> DISCONNECT = ServerPlayConnectionEvents.DISCONNECT;
    }

    public final static class LivingEntity {
        /**
         * @see ServerLivingEntityEvents#ALLOW_DAMAGE
         */
        @FabricAPI
        public static final Event<ServerLivingEntityEvents.AllowDamage> ALLOW_DAMAGE = ServerLivingEntityEvents.ALLOW_DAMAGE;

        /**
         * @see ServerLivingEntityEvents#AFTER_DAMAGE
         */
        @FabricAPI
        public static final Event<ServerLivingEntityEvents.AfterDamage> AFTER_DAMAGE = ServerLivingEntityEvents.AFTER_DAMAGE;

        /**
         * @see ServerLivingEntityEvents#ALLOW_DEATH
         */
        @FabricAPI
        public static final Event<ServerLivingEntityEvents.AllowDeath> ALLOW_DEATH = ServerLivingEntityEvents.ALLOW_DEATH;

        /**
         * @see ServerLivingEntityEvents#AFTER_DEATH
         */
        @FabricAPI
        public static final Event<ServerLivingEntityEvents.AfterDeath> AFTER_DEATH = ServerLivingEntityEvents.AFTER_DEATH;

        /**
         * @see ServerLivingEntityEvents#MOB_CONVERSION
         */
        @FabricAPI
        public static final Event<ServerLivingEntityEvents.MobConversion> MOB_CONVERSION = ServerLivingEntityEvents.MOB_CONVERSION;
    }

    public final static class Chunk {
        /**
         * @see ServerChunkEvents#CHUNK_LOAD
         */
        @FabricAPI
        public static final Event<ServerChunkEvents.Load> CHUNK_LOAD = ServerChunkEvents.CHUNK_LOAD;

        /**
         * @see ServerChunkEvents#CHUNK_GENERATE
         */
        @FabricAPI
        public static final Event<ServerChunkEvents.Generate> CHUNK_GENERATE = ServerChunkEvents.CHUNK_GENERATE;

        /**
         * @see ServerChunkEvents#CHUNK_UNLOAD
         */
        @FabricAPI
        public static final Event<ServerChunkEvents.Unload> CHUNK_UNLOAD = ServerChunkEvents.CHUNK_UNLOAD;

        /**
         * @see ServerChunkEvents#CHUNK_LEVEL_TYPE_CHANGE
         */
        @FabricAPI
        public static final Event<ServerChunkEvents.LevelTypeChange> CHUNK_LEVEL_TYPE_CHANGE = ServerChunkEvents.CHUNK_LEVEL_TYPE_CHANGE;
    }

    public final static class Entity {
        /**
         * @see ServerEntityEvents#ENTITY_LOAD
         */
        @FabricAPI
        public static final Event<ServerEntityEvents.Load> ENTITY_LOAD = ServerEntityEvents.ENTITY_LOAD;

        /**
         * @see ServerEntityEvents#ENTITY_UNLOAD
         */
        @FabricAPI
        public static final Event<ServerEntityEvents.Unload> ENTITY_UNLOAD = ServerEntityEvents.ENTITY_UNLOAD;

        /**
         * @see ServerEntityEvents#EQUIPMENT_CHANGE
         */
        @FabricAPI
        public static final Event<ServerEntityEvents.EquipmentChange> EQUIPMENT_CHANGE = ServerEntityEvents.EQUIPMENT_CHANGE;
    }

    public final static class Lifecycle {
        /**
         * @see ServerLifecycleEvents#SERVER_STARTING
         */
        @FabricAPI
        public static final Event<ServerLifecycleEvents.ServerStarting> SERVER_STARTING = ServerLifecycleEvents.SERVER_STARTING;

        /**
         * @see ServerLifecycleEvents#SERVER_STARTED
         */
        @FabricAPI
        public static final Event<ServerLifecycleEvents.ServerStarted> SERVER_STARTED = ServerLifecycleEvents.SERVER_STARTED;

        /**
         * @see ServerLifecycleEvents#SERVER_STOPPING
         */
        @FabricAPI
        public static final Event<ServerLifecycleEvents.ServerStopping> SERVER_STOPPING = ServerLifecycleEvents.SERVER_STOPPING;

        /**
         * @see ServerLifecycleEvents#SERVER_STOPPED
         */
        @FabricAPI
        public static final Event<ServerLifecycleEvents.ServerStopped> SERVER_STOPPED = ServerLifecycleEvents.SERVER_STOPPED;

        /**
         * @see ServerLifecycleEvents#SYNC_DATA_PACK_CONTENTS
         */
        @FabricAPI
        public static final Event<ServerLifecycleEvents.SyncDataPackContents> SYNC_DATA_PACK_CONTENTS = ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS;

        /**
         * @see ServerLifecycleEvents#START_DATA_PACK_RELOAD
         */
        @FabricAPI
        public static final Event<ServerLifecycleEvents.StartDataPackReload> START_DATA_PACK_RELOAD = ServerLifecycleEvents.START_DATA_PACK_RELOAD;

        /**
         * @see ServerLifecycleEvents#END_DATA_PACK_RELOAD
         */
        @FabricAPI
        public static final Event<ServerLifecycleEvents.EndDataPackReload> END_DATA_PACK_RELOAD = ServerLifecycleEvents.END_DATA_PACK_RELOAD;

        /**
         * @see ServerLifecycleEvents#BEFORE_SAVE
         */
        @FabricAPI
        public static final Event<ServerLifecycleEvents.BeforeSave> BEFORE_SAVE = ServerLifecycleEvents.BEFORE_SAVE;

        /**
         * @see ServerLifecycleEvents#AFTER_SAVE
         */
        @FabricAPI
        public static final Event<ServerLifecycleEvents.AfterSave> AFTER_SAVE = ServerLifecycleEvents.AFTER_SAVE;
    }

    public final static class Tick {
        /**
         * @see ServerTickEvents#START_SERVER_TICK
         */
        @FabricAPI
        public static final Event<ServerTickEvents.StartTick> START_SERVER_TICK = ServerTickEvents.START_SERVER_TICK;

        /**
         * @see ServerTickEvents#END_SERVER_TICK
         */
        @FabricAPI
        public static final Event<ServerTickEvents.EndTick> END_SERVER_TICK = ServerTickEvents.END_SERVER_TICK;

        /**
         * @see ServerTickEvents#START_WORLD_TICK
         */
        @FabricAPI
        public static final Event<ServerTickEvents.StartWorldTick> START_WORLD_TICK = ServerTickEvents.START_WORLD_TICK;

        /**
         * @see ServerTickEvents#END_WORLD_TICK
         */
        @FabricAPI
        public static final Event<ServerTickEvents.EndWorldTick> END_WORLD_TICK = ServerTickEvents.END_WORLD_TICK;
    }

    public final static class Message {
        /**
         * @see ServerMessageEvents#ALLOW_CHAT_MESSAGE
         */
        @FabricAPI
        public static final Event<ServerMessageEvents.AllowChatMessage> ALLOW_CHAT_MESSAGE = ServerMessageEvents.ALLOW_CHAT_MESSAGE;

        /**
         * @see ServerMessageEvents#ALLOW_GAME_MESSAGE
         */
        @FabricAPI
        public static final Event<ServerMessageEvents.AllowGameMessage> ALLOW_GAME_MESSAGE = ServerMessageEvents.ALLOW_GAME_MESSAGE;

        /**
         * @see ServerMessageEvents#ALLOW_COMMAND_MESSAGE
         */
        @FabricAPI
        public static final Event<ServerMessageEvents.AllowCommandMessage> ALLOW_COMMAND_MESSAGE = ServerMessageEvents.ALLOW_COMMAND_MESSAGE;

        /**
         * @see ServerMessageEvents#CHAT_MESSAGE
         */
        @FabricAPI
        public static final Event<ServerMessageEvents.ChatMessage> CHAT_MESSAGE = ServerMessageEvents.CHAT_MESSAGE;

        /**
         * @see ServerMessageEvents#GAME_MESSAGE
         */
        @FabricAPI
        public static final Event<ServerMessageEvents.GameMessage> GAME_MESSAGE = ServerMessageEvents.GAME_MESSAGE;

        /**
         * @see ServerMessageEvents#COMMAND_MESSAGE
         */
        @FabricAPI
        public static final Event<ServerMessageEvents.CommandMessage> COMMAND_MESSAGE = ServerMessageEvents.COMMAND_MESSAGE;
    }

    public final static class World {
        /**
         * @see ServerWorldEvents#LOAD
         */
        @FabricAPI
        public static final Event<ServerWorldEvents.Load> LOAD = ServerWorldEvents.LOAD;

        /**
         * @see ServerWorldEvents#UNLOAD
         */
        @FabricAPI
        public static final Event<ServerWorldEvents.Unload> UNLOAD = ServerWorldEvents.UNLOAD;
    }

    public final static class BlockEntity {
        /**
         * @see ServerBlockEntityEvents#BLOCK_ENTITY_LOAD
         */
        @FabricAPI
        public static final Event<ServerBlockEntityEvents.Load> BLOCK_ENTITY_LOAD = ServerBlockEntityEvents.BLOCK_ENTITY_LOAD;

        /**
         * @see ServerBlockEntityEvents#BLOCK_ENTITY_UNLOAD
         */
        @FabricAPI
        public static final Event<ServerBlockEntityEvents.Unload> BLOCK_ENTITY_UNLOAD = ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD;
    }

    public final static class ConfigurationConnection {
        /**
         * @see ServerConfigurationConnectionEvents#BEFORE_CONFIGURE
         */
        @FabricAPI
        public static final Event<ServerConfigurationConnectionEvents.Configure> BEFORE_CONFIGURE = ServerConfigurationConnectionEvents.BEFORE_CONFIGURE;

        /**
         * @see ServerConfigurationConnectionEvents#CONFIGURE
         */
        @FabricAPI
        public static final Event<ServerConfigurationConnectionEvents.Configure> CONFIGURE = ServerConfigurationConnectionEvents.CONFIGURE;

        /**
         * @see ServerConfigurationConnectionEvents#DISCONNECT
         */
        @FabricAPI
        public static final Event<ServerConfigurationConnectionEvents.Disconnect> DISCONNECT = ServerConfigurationConnectionEvents.DISCONNECT;
    }

    public final static class EntityCombat {
        /**
         * @see ServerEntityCombatEvents#AFTER_KILLED_OTHER_ENTITY
         */
        @FabricAPI
        public static final Event<ServerEntityCombatEvents.AfterKilledOtherEntity> AFTER_KILLED_OTHER_ENTITY = ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY;
    }

    public final static class LoginConnection {
        /**
         * @see ServerLoginConnectionEvents#INIT
         */
        @FabricAPI
        public static final Event<ServerLoginConnectionEvents.Init> INIT = ServerLoginConnectionEvents.INIT;

        /**
         * @see ServerLoginConnectionEvents#QUERY_START
         */
        @FabricAPI
        public static final Event<ServerLoginConnectionEvents.QueryStart> QUERY_START = ServerLoginConnectionEvents.QUERY_START;

        /**
         * @see ServerLoginConnectionEvents#DISCONNECT
         */
        @FabricAPI
        public static final Event<ServerLoginConnectionEvents.Disconnect> DISCONNECT = ServerLoginConnectionEvents.DISCONNECT;
    }

    public final static class EntityWorldChange {
        /**
         * @see ServerEntityWorldChangeEvents#AFTER_ENTITY_CHANGE_WORLD
         */
        @FabricAPI
        public static final Event<ServerEntityWorldChangeEvents.AfterEntityChange> AFTER_ENTITY_CHANGE_WORLD = ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD;

        /**
         * @see ServerEntityWorldChangeEvents#AFTER_PLAYER_CHANGE_WORLD
         */
        @FabricAPI
        public static final Event<ServerEntityWorldChangeEvents.AfterPlayerChange> AFTER_PLAYER_CHANGE_WORLD = ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD;
    }
}
