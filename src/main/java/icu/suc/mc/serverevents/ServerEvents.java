package icu.suc.mc.serverevents;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.*;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class ServerEvents implements ModInitializer {

    @Override
    public void onInitialize() {}

    public static abstract class Player {
        /**
         * @see ServerPlayerEvents#COPY_FROM
         */
        @FabricAPI public static final Event<ServerPlayerEvents.CopyFrom> COPY_FROM = ServerPlayerEvents.COPY_FROM;

        /**
         * @see ServerPlayerEvents#AFTER_RESPAWN
         */
        @FabricAPI public static final Event<ServerPlayerEvents.AfterRespawn> AFTER_RESPAWN = ServerPlayerEvents.AFTER_RESPAWN;
        
        /**
         * An event that can be used to provide the player's join message.
         */
        public static final Event<ServerEvents.Player.ModifyJoinMessage> MODIFY_JOIN_MESSAGE = EventFactory.createArrayBacked(ServerEvents.Player.ModifyJoinMessage.class, callbacks -> (player, message) -> {
            for (ServerEvents.Player.ModifyJoinMessage callback : callbacks) {
                message = callback.modifyJoinMessage(player, message);
            }
            return message;
        });

        /**
         * An event that can be used to provide the player's leave message.
         */
        public static final Event<ServerEvents.Player.ModifyLeaveMessage> MODIFY_LEAVE_MESSAGE = EventFactory.createArrayBacked(ServerEvents.Player.ModifyLeaveMessage.class, callbacks -> (player, message) -> {
            for (ServerEvents.Player.ModifyLeaveMessage callback : callbacks) {
                message = callback.modifyLeaveMessage(player, message);
            }
            return message;
        });

        @FunctionalInterface
        public interface ModifyJoinMessage {
            /**
             * Modifies or provides a message for a player joined.
             *
             * @param player the player
             * @param message the join message
             * @return the new join message
             */
            Component modifyJoinMessage(ServerPlayer player, Component message);
        }

        @FunctionalInterface
        public interface ModifyLeaveMessage {
            /**
             * Modifies or provides a message for a player left.
             *
             * @param player the player
             * @param message the leave message
             * @return the new leave message
             */
            Component modifyLeaveMessage(ServerPlayer player, Component message);
        }

        public static abstract class Kick {
            /**
             * An event that allows the player should be kicked.
             */
            public static final Event<ServerEvents.Player.Kick.Allow> ALLOW = EventFactory.createArrayBacked(ServerEvents.Player.Kick.Allow.class, callbacks -> (player, reason) -> {
                for (ServerEvents.Player.Kick.Allow callback : callbacks) {
                    if (!callback.allowKick(player, reason)) {
                        return false;
                    }
                }
                return true;
            });

            /**
             * An event that can be used to provide the player's kick reason.
             */
            public static final Event<ServerEvents.Player.Kick.ModifyReason> MODIFY_REASON = EventFactory.createArrayBacked(ServerEvents.Player.Kick.ModifyReason.class, callbacks -> (player, message) -> {
                for (ServerEvents.Player.Kick.ModifyReason callback : callbacks) {
                    message = callback.modifyKickReason(player, message);
                }
                return message;
            });

            @FunctionalInterface
            public interface Allow {
                /**
                 * Called when a player kicks a server
                 *
                 * @param player the player
                 * @param reason the kick reason
                 * @return {@code true} if the player should be kicked, otherwise {@code false}
                 */
                boolean allowKick(ServerPlayer player, Component reason);
            }

            @FunctionalInterface
            public interface ModifyReason {
                /**
                 * Modifies or provides a reason for a player kicked.
                 *
                 * @param player the player
                 * @param reason the kick reason
                 * @return the new kick reason
                 */
                Component modifyKickReason(ServerPlayer player, Component reason);
            }
        }

        public static abstract class Interact {
            public static abstract class Use {
                /**
                 * @see UseBlockCallback#EVENT
                 */
                @FabricAPI public static final Event<UseBlockCallback> BLOCK = UseBlockCallback.EVENT;

                /**
                 * @see UseEntityCallback#EVENT
                 */
                @FabricAPI public static final Event<UseEntityCallback> ENTITY = UseEntityCallback.EVENT;

                /**
                 * @see UseItemCallback#EVENT
                 */
                @FabricAPI public static final Event<UseItemCallback> ITEM = UseItemCallback.EVENT;
            }

            public static abstract class Attack {
                /**
                 * @see AttackBlockCallback#EVENT
                 */
                @FabricAPI public static final Event<AttackBlockCallback> BLOCK = AttackBlockCallback.EVENT;

                /**
                 * @see AttackEntityCallback#EVENT
                 */
                @FabricAPI public static final Event<AttackEntityCallback> ENTITY = AttackEntityCallback.EVENT;
            }

            public static abstract class Break {
                /**
                 * @see PlayerBlockBreakEvents#BEFORE
                 */
                @FabricAPI public static final Event<PlayerBlockBreakEvents.Before> BEFORE = PlayerBlockBreakEvents.BEFORE;

                /**
                 * @see PlayerBlockBreakEvents#AFTER
                 */
                @FabricAPI public static final Event<PlayerBlockBreakEvents.After> AFTER = PlayerBlockBreakEvents.AFTER;

                /**
                 * @see PlayerBlockBreakEvents#CANCELED
                 */
                @FabricAPI public static final Event<PlayerBlockBreakEvents.Canceled> CANCELED = PlayerBlockBreakEvents.CANCELED;
            }

            public static abstract class Pick {
                /**
                 * @see PlayerPickItemEvents#BLOCK
                 */
                @FabricAPI public static final Event<PlayerPickItemEvents.PickItemFromBlock> BLOCK = PlayerPickItemEvents.BLOCK;

                /**
                 * @see PlayerPickItemEvents#ENTITY
                 */
                @FabricAPI public static final Event<PlayerPickItemEvents.PickItemFromEntity> ENTITY = PlayerPickItemEvents.ENTITY;
            }
        }
    }

    public static abstract class Item {
        /**
         * @see DefaultItemComponentEvents#MODIFY
         */
        @FabricAPI public static final Event<DefaultItemComponentEvents.ModifyCallback> DEFAULT_COMPONENT_MODIFY = DefaultItemComponentEvents.MODIFY;

        public static abstract class Enchanting {
            /**
             * @see EnchantmentEvents#ALLOW_ENCHANTING
             */
            @FabricAPI public static final Event<EnchantmentEvents.AllowEnchanting> ALLOW = EnchantmentEvents.ALLOW_ENCHANTING;

            /**
             * @see EnchantmentEvents#MODIFY
             */
            @FabricAPI public static final Event<EnchantmentEvents.Modify> MODIFY = EnchantmentEvents.MODIFY;
        }
    }

    public static abstract class Connection {
        public static abstract class Configuration {
            /**
             * @see ServerConfigurationConnectionEvents#BEFORE_CONFIGURE
             */
            @FabricAPI public static final Event<ServerConfigurationConnectionEvents.Configure> BEFORE = ServerConfigurationConnectionEvents.BEFORE_CONFIGURE;

            /**
             * @see ServerConfigurationConnectionEvents#CONFIGURE
             */
            @FabricAPI public static final Event<ServerConfigurationConnectionEvents.Configure> CONFIGURE = ServerConfigurationConnectionEvents.CONFIGURE;

            /**
             * @see ServerConfigurationConnectionEvents#DISCONNECT
             */
            @FabricAPI public static final Event<ServerConfigurationConnectionEvents.Disconnect> DISCONNECT = ServerConfigurationConnectionEvents.DISCONNECT;
        }

        public static abstract class Login {
            /**
             * @see ServerLoginConnectionEvents#INIT
             */
            @FabricAPI public static final Event<ServerLoginConnectionEvents.Init> INIT = ServerLoginConnectionEvents.INIT;

            /**
             * @see ServerLoginConnectionEvents#QUERY_START
             */
            @FabricAPI public static final Event<ServerLoginConnectionEvents.QueryStart> QUERY_START = ServerLoginConnectionEvents.QUERY_START;

            /**
             * @see ServerLoginConnectionEvents#DISCONNECT
             */
            @FabricAPI public static final Event<ServerLoginConnectionEvents.Disconnect> DISCONNECT = ServerLoginConnectionEvents.DISCONNECT;
        }

        public static abstract class Play {
            /**
             * @see ServerPlayConnectionEvents#INIT
             */
            @FabricAPI public static final Event<ServerPlayConnectionEvents.Init> INIT = ServerPlayConnectionEvents.INIT;

            /**
             * @see ServerPlayConnectionEvents#JOIN
             */
            @FabricAPI public static final Event<ServerPlayConnectionEvents.Join> JOIN = ServerPlayConnectionEvents.JOIN;

            /**
             * @see ServerPlayConnectionEvents#DISCONNECT
             */
            @FabricAPI public static final Event<ServerPlayConnectionEvents.Disconnect> DISCONNECT = ServerPlayConnectionEvents.DISCONNECT;
        }
    }

    public static abstract class LivingEntity {
        /**
         * @see ServerLivingEntityEvents#MOB_CONVERSION
         */
        @FabricAPI public static final Event<ServerLivingEntityEvents.MobConversion> MOB_CONVERSION = ServerLivingEntityEvents.MOB_CONVERSION;

        public static abstract class Damage {
            /**
             * @see ServerLivingEntityEvents#ALLOW_DAMAGE
             */
            @FabricAPI public static final Event<ServerLivingEntityEvents.AllowDamage> ALLOW = ServerLivingEntityEvents.ALLOW_DAMAGE;

            /**
             * @see ServerLivingEntityEvents#AFTER_DAMAGE
             */
            @FabricAPI public static final Event<ServerLivingEntityEvents.AfterDamage> AFTER = ServerLivingEntityEvents.AFTER_DAMAGE;
        }

        public static abstract class Death {
            /**
             * @see ServerLivingEntityEvents#ALLOW_DEATH
             */
            @FabricAPI public static final Event<ServerLivingEntityEvents.AllowDeath> ALLOW = ServerLivingEntityEvents.ALLOW_DEATH;

            /**
             * @see ServerLivingEntityEvents#AFTER_DEATH
             */
            @FabricAPI public static final Event<ServerLivingEntityEvents.AfterDeath> AFTER = ServerLivingEntityEvents.AFTER_DEATH;
        }
    }

    public static abstract class Chunk {
        /**
         * @see ServerChunkEvents#CHUNK_LOAD
         */
        @FabricAPI public static final Event<ServerChunkEvents.Load> LOAD = ServerChunkEvents.CHUNK_LOAD;

        /**
         * @see ServerChunkEvents#CHUNK_GENERATE
         */
        @FabricAPI public static final Event<ServerChunkEvents.Generate> GENERATE = ServerChunkEvents.CHUNK_GENERATE;

        /**
         * @see ServerChunkEvents#CHUNK_UNLOAD
         */
        @FabricAPI public static final Event<ServerChunkEvents.Unload> UNLOAD = ServerChunkEvents.CHUNK_UNLOAD;

        /**
         * @see ServerChunkEvents#CHUNK_LEVEL_TYPE_CHANGE
         */
        @FabricAPI public static final Event<ServerChunkEvents.LevelTypeChange> LEVEL_TYPE_CHANGE = ServerChunkEvents.CHUNK_LEVEL_TYPE_CHANGE;
    }

    public static abstract class Entity {
        /**
         * @see ServerEntityEvents#ENTITY_LOAD
         */
        @FabricAPI public static final Event<ServerEntityEvents.Load> LOAD = ServerEntityEvents.ENTITY_LOAD;

        /**
         * @see ServerEntityEvents#ENTITY_UNLOAD
         */
        @FabricAPI public static final Event<ServerEntityEvents.Unload> UNLOAD = ServerEntityEvents.ENTITY_UNLOAD;

        /**
         * @see ServerEntityEvents#EQUIPMENT_CHANGE
         */
        @FabricAPI public static final Event<ServerEntityEvents.EquipmentChange> EQUIPMENT_CHANGE = ServerEntityEvents.EQUIPMENT_CHANGE;

        /**
         * @see ServerEntityCombatEvents#AFTER_KILLED_OTHER_ENTITY
         */
        @FabricAPI public static final Event<ServerEntityCombatEvents.AfterKilledOtherEntity> AFTER_KILLED_OTHER_ENTITY = ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY;

        public static abstract class Elytra {
            /**
             * @see EntityElytraEvents#ALLOW
             */
            @FabricAPI public static final Event<EntityElytraEvents.Allow> ALLOW = EntityElytraEvents.ALLOW;

            /**
             * @see EntityElytraEvents#CUSTOM
             */
            @FabricAPI public static final Event<EntityElytraEvents.Custom> CUSTOM = EntityElytraEvents.CUSTOM;
        }

        public static abstract class Sleep {
            /**
             * @see EntitySleepEvents#ALLOW_SLEEPING
             */
            @FabricAPI public static final Event<EntitySleepEvents.AllowSleeping> ALLOW_SLEEPING = EntitySleepEvents.ALLOW_SLEEPING;

            /**
             * @see EntitySleepEvents#START_SLEEPING
             */
            @FabricAPI public static final Event<EntitySleepEvents.StartSleeping> START_SLEEPING = EntitySleepEvents.START_SLEEPING;

            /**
             * @see EntitySleepEvents#STOP_SLEEPING
             */
            @FabricAPI public static final Event<EntitySleepEvents.StopSleeping> STOP_SLEEPING = EntitySleepEvents.STOP_SLEEPING;

            /**
             * @see EntitySleepEvents#ALLOW_BED
             */
            @FabricAPI public static final Event<EntitySleepEvents.AllowBed> ALLOW_BED = EntitySleepEvents.ALLOW_BED;

            /**
             * @see EntitySleepEvents#ALLOW_SLEEP_TIME
             */
            @FabricAPI public static final Event<EntitySleepEvents.AllowSleepTime> ALLOW_SLEEP_TIME = EntitySleepEvents.ALLOW_SLEEP_TIME;

            /**
             * @see EntitySleepEvents#ALLOW_NEARBY_MONSTERS
             */
            @FabricAPI public static final Event<EntitySleepEvents.AllowNearbyMonsters> ALLOW_NEARBY_MONSTERS = EntitySleepEvents.ALLOW_NEARBY_MONSTERS;

            /**
             * @see EntitySleepEvents#ALLOW_RESETTING_TIME
             */
            @FabricAPI public static final Event<EntitySleepEvents.AllowResettingTime> ALLOW_RESETTING_TIME = EntitySleepEvents.ALLOW_RESETTING_TIME;

            /**
             * @see EntitySleepEvents#MODIFY_SLEEPING_DIRECTION
             */
            @FabricAPI public static final Event<EntitySleepEvents.ModifySleepingDirection> MODIFY_SLEEPING_DIRECTION = EntitySleepEvents.MODIFY_SLEEPING_DIRECTION;

            /**
             * @see EntitySleepEvents#ALLOW_SETTING_SPAWN
             */
            @FabricAPI public static final Event<EntitySleepEvents.AllowSettingSpawn> ALLOW_SETTING_SPAWN = EntitySleepEvents.ALLOW_SETTING_SPAWN;

            /**
             * @see EntitySleepEvents#SET_BED_OCCUPATION_STATE
             */
            @FabricAPI public static final Event<EntitySleepEvents.SetBedOccupationState> SET_BED_OCCUPATION_STATE = EntitySleepEvents.SET_BED_OCCUPATION_STATE;

            /**
             * @see EntitySleepEvents#MODIFY_WAKE_UP_POSITION
             */
            @FabricAPI public static final Event<EntitySleepEvents.ModifyWakeUpPosition> MODIFY_WAKE_UP_POSITION = EntitySleepEvents.MODIFY_WAKE_UP_POSITION;
        }

        public static abstract class Tracking {
            /**
             * @see EntityTrackingEvents#START_TRACKING
             */
            @FabricAPI public static final Event<EntityTrackingEvents.StartTracking> START = EntityTrackingEvents.START_TRACKING;

            /**
             * @see EntityTrackingEvents#STOP_TRACKING
             */
            @FabricAPI public static final Event<EntityTrackingEvents.StopTracking> STOP = EntityTrackingEvents.STOP_TRACKING;
        }
    }

    public static abstract class Lifecycle {
        /**
         * @see ServerLifecycleEvents#SERVER_STARTING
         */
        @FabricAPI public static final Event<ServerLifecycleEvents.ServerStarting> STARTING = ServerLifecycleEvents.SERVER_STARTING;

        /**
         * @see ServerLifecycleEvents#SERVER_STARTED
         */
        @FabricAPI public static final Event<ServerLifecycleEvents.ServerStarted> STARTED = ServerLifecycleEvents.SERVER_STARTED;

        /**
         * @see ServerLifecycleEvents#SERVER_STOPPING
         */
        @FabricAPI public static final Event<ServerLifecycleEvents.ServerStopping> STOPPING = ServerLifecycleEvents.SERVER_STOPPING;

        /**
         * @see ServerLifecycleEvents#SERVER_STOPPED
         */
        @FabricAPI public static final Event<ServerLifecycleEvents.ServerStopped> STOPPED = ServerLifecycleEvents.SERVER_STOPPED;

        /**
         * @see CommonLifecycleEvents#TAGS_LOADED
         */
        @FabricAPI public static final Event<CommonLifecycleEvents.TagsLoaded> TAGS_LOADED = CommonLifecycleEvents.TAGS_LOADED;

        public static abstract class DataPack {
            /**
             * @see ServerLifecycleEvents#SYNC_DATA_PACK_CONTENTS
             */
            @FabricAPI public static final Event<ServerLifecycleEvents.SyncDataPackContents> SYNC_CONTENTS = ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS;

            public static abstract class Reload {
                /**
                 * @see ServerLifecycleEvents#START_DATA_PACK_RELOAD
                 */
                @FabricAPI public static final Event<ServerLifecycleEvents.StartDataPackReload> START = ServerLifecycleEvents.START_DATA_PACK_RELOAD;

                /**
                 * @see ServerLifecycleEvents#END_DATA_PACK_RELOAD
                 */
                @FabricAPI public static final Event<ServerLifecycleEvents.EndDataPackReload> END = ServerLifecycleEvents.END_DATA_PACK_RELOAD;
            }
        }

        public static abstract class Save {
            /**
             * @see ServerLifecycleEvents#BEFORE_SAVE
             */
            @FabricAPI public static final Event<ServerLifecycleEvents.BeforeSave> BEFORE = ServerLifecycleEvents.BEFORE_SAVE;

            /**
             * @see ServerLifecycleEvents#AFTER_SAVE
             */
            @FabricAPI public static final Event<ServerLifecycleEvents.AfterSave> AFTER = ServerLifecycleEvents.AFTER_SAVE;
        }
    }

    public static abstract class Tick {
        public static abstract class Server {
            /**
             * @see ServerTickEvents#START_SERVER_TICK
             */
            @FabricAPI public static final Event<ServerTickEvents.StartTick> START = ServerTickEvents.START_SERVER_TICK;

            /**
             * @see ServerTickEvents#END_SERVER_TICK
             */
            @FabricAPI public static final Event<ServerTickEvents.EndTick> END = ServerTickEvents.END_SERVER_TICK;
        }

        public static abstract class World {
            /**
             * @see ServerTickEvents#START_WORLD_TICK
             */
            @FabricAPI public static final Event<ServerTickEvents.StartWorldTick> START = ServerTickEvents.START_WORLD_TICK;

            /**
             * @see ServerTickEvents#END_WORLD_TICK
             */
            @FabricAPI public static final Event<ServerTickEvents.EndWorldTick> END = ServerTickEvents.END_WORLD_TICK;
        }
    }

    public static abstract class Message {
        public static abstract class Chat {
            /**
             * @see ServerMessageEvents#ALLOW_CHAT_MESSAGE
             */
            @FabricAPI public static final Event<ServerMessageEvents.AllowChatMessage> ALLOW = ServerMessageEvents.ALLOW_CHAT_MESSAGE;

            /**
             * @see ServerMessageEvents#CHAT_MESSAGE
             */
            @FabricAPI public static final Event<ServerMessageEvents.ChatMessage> ON = ServerMessageEvents.CHAT_MESSAGE;
        }

        public static abstract class Game {
            /**
             * @see ServerMessageEvents#ALLOW_GAME_MESSAGE
             */
            @FabricAPI public static final Event<ServerMessageEvents.AllowGameMessage> ALLOW = ServerMessageEvents.ALLOW_GAME_MESSAGE;

            /**
             * @see ServerMessageEvents#GAME_MESSAGE
             */
            @FabricAPI public static final Event<ServerMessageEvents.GameMessage> ON = ServerMessageEvents.GAME_MESSAGE;
        }

        public static abstract class Command {
            /**
             * @see ServerMessageEvents#ALLOW_COMMAND_MESSAGE
             */
            @FabricAPI public static final Event<ServerMessageEvents.AllowCommandMessage> ALLOW = ServerMessageEvents.ALLOW_COMMAND_MESSAGE;

            /**
             * @see ServerMessageEvents#COMMAND_MESSAGE
             */
            @FabricAPI public static final Event<ServerMessageEvents.CommandMessage> ON = ServerMessageEvents.COMMAND_MESSAGE;
        }
    }

    public static abstract class World {
        /**
         * @see ServerWorldEvents#LOAD
         */
        @FabricAPI public static final Event<ServerWorldEvents.Load> LOAD = ServerWorldEvents.LOAD;

        /**
         * @see ServerWorldEvents#UNLOAD
         */
        @FabricAPI public static final Event<ServerWorldEvents.Unload> UNLOAD = ServerWorldEvents.UNLOAD;

        public static abstract class Change {
            /**
             * @see ServerEntityWorldChangeEvents#AFTER_ENTITY_CHANGE_WORLD
             */
            @FabricAPI public static final Event<ServerEntityWorldChangeEvents.AfterEntityChange> ENTITY = ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD;

            /**
             * @see ServerEntityWorldChangeEvents#AFTER_PLAYER_CHANGE_WORLD
             */
            @FabricAPI public static final Event<ServerEntityWorldChangeEvents.AfterPlayerChange> PLAYER = ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD;
        }
    }

    public static abstract class BlockEntity {
        /**
         * @see ServerBlockEntityEvents#BLOCK_ENTITY_LOAD
         */
        @FabricAPI public static final Event<ServerBlockEntityEvents.Load> LOAD = ServerBlockEntityEvents.BLOCK_ENTITY_LOAD;

        /**
         * @see ServerBlockEntityEvents#BLOCK_ENTITY_UNLOAD
         */
        @FabricAPI public static final Event<ServerBlockEntityEvents.Unload> UNLOAD = ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD;
    }

    public static abstract class LootTable {
        /**
         * @see LootTableEvents#REPLACE
         */
        @FabricAPI public static final Event<LootTableEvents.Replace> REPLACE = LootTableEvents.REPLACE;

        /**
         * @see LootTableEvents#MODIFY
         */
        @FabricAPI public static final Event<LootTableEvents.Modify> MODIFY = LootTableEvents.MODIFY;

        /**
         * @see LootTableEvents#ALL_LOADED
         */
        @FabricAPI public static final Event<LootTableEvents.Loaded> ALL_LOADED = LootTableEvents.ALL_LOADED;
    }
}
