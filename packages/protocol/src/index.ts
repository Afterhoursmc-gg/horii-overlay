import { z } from 'zod';

export const GameModeSchema = z.enum(['BEDWARS_SOLO','BEDWARS_DOUBLES','BEDWARS_3V3V3V3','BEDWARS_4V4V4V4','UNKNOWN']);
export const PlayerStatsSchema = z.object({
  username: z.string().min(1).max(16), uuid: z.string().optional(), level: z.number().int().nonnegative().optional(),
  fkdr: z.number().nonnegative().optional(), wlr: z.number().nonnegative().optional(), wins: z.number().int().nonnegative().optional(),
  losses: z.number().int().nonnegative().optional(), kills: z.number().int().nonnegative().optional(), deaths: z.number().int().nonnegative().optional(),
  finalKills: z.number().int().nonnegative().optional(), finalDeaths: z.number().int().nonnegative().optional(), bedsBroken: z.number().int().nonnegative().optional(),
});
export const GameSnapshotSchema = z.object({ gameId: z.string().min(1).max(128), mode: GameModeSchema, status: z.enum(['LOBBY','IN_GAME','ENDED']), players: z.array(PlayerStatsSchema).max(32), updatedAt: z.string().datetime() });
export const ClientEventSchema = z.discriminatedUnion('type', [
  z.object({type:z.literal('GAME_START'),data:z.object({mode:GameModeSchema,players:z.array(PlayerStatsSchema).max(32)})}),
  z.object({type:z.literal('GAME_LEAVE'),data:z.object({gameId:z.string().max(128)})}),
  z.object({type:z.literal('PLAYER_JOIN'),data:PlayerStatsSchema}),
  z.object({type:z.literal('PLAYER_LEAVE'),data:z.object({username:z.string().min(1).max(16)})}),
  z.object({type:z.literal('PLAYER_UPDATE'),data:PlayerStatsSchema}),
  z.object({type:z.literal('LOBBY_CHANGE'),data:z.object({name:z.string().max(64)})}),
  z.object({type:z.literal('GAME_END'),data:z.object({gameId:z.string().max(128)})}),
]);
export const ServerEventSchema = z.discriminatedUnion('type', [
  z.object({type:z.literal('GAME_UPDATE'),data:GameSnapshotSchema}),
  z.object({type:z.literal('CONNECTION_STATE'),data:z.object({state:z.enum(['connected','reconnecting','unauthenticated'])})}),
  z.object({type:z.literal('ERROR'),data:z.object({code:z.string(),message:z.string().max(256)})}),
]);
export type ClientEvent = z.infer<typeof ClientEventSchema>;
export type ServerEvent = z.infer<typeof ServerEventSchema>;
export type PlayerStats = z.infer<typeof PlayerStatsSchema>;
export type GameSnapshot = z.infer<typeof GameSnapshotSchema>;
