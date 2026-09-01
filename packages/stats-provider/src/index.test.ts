import {describe,expect,it} from 'vitest';
import {FixtureStatsProvider} from './index.js';
describe('stats provider',()=>it('normalizes a fixture lookup',async()=>{const p=await new FixtureStatsProvider([{username:'Notch',fkdr:4.2}]).lookup('notch');expect(p).toEqual({username:'Notch',fkdr:4.2});}));
