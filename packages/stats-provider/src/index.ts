import type {PlayerStats} from '@horii-overlay/protocol';
export interface StatsProvider { lookup(username:string):Promise<PlayerStats>; }
export class FixtureStatsProvider implements StatsProvider { constructor(private readonly players:PlayerStats[]){} async lookup(username:string){return this.players.find(p=>p.username.toLowerCase()===username.toLowerCase())??{username};} }
export class HypixelStatsProvider implements StatsProvider { constructor(private readonly apiKey:string){} async lookup(username:string):Promise<PlayerStats>{ if(!this.apiKey) throw new Error('HYPIXEL_API_KEY is not configured'); throw new Error('Hypixel provider adapter requires a server-side implementation'); } }
