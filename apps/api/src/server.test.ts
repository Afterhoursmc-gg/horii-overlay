import { afterAll, describe, expect, it } from 'vitest';
import { buildServer } from './server.js';
const app=await buildServer();
describe('overlay API',()=>{it('returns health and a fixture game',async()=>{const health=await app.inject('/health');expect(health.statusCode).toBe(200);expect(health.json().ok).toBe(true);const game=await app.inject('/api/game/current');expect(game.json().mode).toBe('BEDWARS_DOUBLES');expect(game.json().players).toHaveLength(3);});});
afterAll(()=>app.close());
