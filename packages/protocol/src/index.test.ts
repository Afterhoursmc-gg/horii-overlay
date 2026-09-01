import { describe, expect, it } from 'vitest';
import { ClientEventSchema, ServerEventSchema } from './index.js';

describe('protocol schemas', () => {
  it('accepts a BedWars game start event', () => {
    const result = ClientEventSchema.safeParse({ type: 'GAME_START', data: { mode: 'BEDWARS_DOUBLES', players: [] } });
    expect(result.success).toBe(true);
  });
  it('rejects an unbounded player stat payload', () => {
    const result = ServerEventSchema.safeParse({ type: 'GAME_UPDATE', data: { players: [{ username: 'x', fkdr: 'not-a-number' }] } });
    expect(result.success).toBe(false);
  });
});
