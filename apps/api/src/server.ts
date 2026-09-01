import Fastify from 'fastify';
import cors from '@fastify/cors';
import websocket from '@fastify/websocket';
import { ClientEventSchema, ServerEvent, GameSnapshot } from '@horii-overlay/protocol';

const now=()=>new Date().toISOString();
const fixturePlayers=[
 {username:'PlayerOne',level:312,fkdr:8.42,wlr:3.21,wins:1240,losses:386,finalKills:2510,finalDeaths:298,bedsBroken:610,kills:3421,deaths:1065},
 {username:'PlayerTwo',level:126,fkdr:2.13,wlr:1.04,wins:341,losses:328,finalKills:690,finalDeaths:324,bedsBroken:142,kills:1203,deaths:1132},
 {username:'PlayerThree',level:53,fkdr:.71,wlr:.44,wins:82,losses:186,finalKills:172,finalDeaths:242,bedsBroken:36,kills:488,deaths:689},
];
export const fixtureSnapshot=():GameSnapshot=>({gameId:'fixture-bedwars-001',mode:'BEDWARS_DOUBLES',status:'IN_GAME',players:fixturePlayers,updatedAt:now()});
export async function buildServer(){
 const app=Fastify({logger:false}); const clients=new Set<any>(); let snapshot=fixtureSnapshot();
 await app.register(cors,{origin:true}); await app.register(websocket);
 app.get('/health',async()=>({ok:true,service:'horii-overlay-api',time:now()}));
 app.get('/api/game/current',async()=>snapshot);
 app.get('/api/player/:username',async(req:any)=>{const username=String(req.params.username); const player=snapshot.players.find(p=>p.username.toLowerCase()===username.toLowerCase()); return player??{username,level:0,fkdr:0,wlr:0,wins:0,losses:0,finalKills:0,finalDeaths:0,bedsBroken:0,kills:0,deaths:0};});
 const bridgeHandler=(socket:any,req:any)=>{const expected=process.env.WS_SHARED_SECRET; const supplied=String(req.query?.token??''); if(expected&&supplied!==expected){socket.close(1008,'unauthorized');return;} clients.add(socket); const send=(event:ServerEvent)=>socket.send(JSON.stringify(event)); send({type:'CONNECTION_STATE',data:{state:'connected'}}); send({type:'GAME_UPDATE',data:snapshot}); socket.on('message',(raw:Buffer)=>{let body:unknown;try{body=JSON.parse(raw.toString())}catch{send({type:'ERROR',data:{code:'INVALID_JSON',message:'Message must be valid JSON'}});return;} const parsed=ClientEventSchema.safeParse(body); if(!parsed.success){send({type:'ERROR',data:{code:'INVALID_EVENT',message:'Invalid client event'}}); return;} if(parsed.data.type==='GAME_START'){snapshot={...snapshot,mode:parsed.data.data.mode,status:'IN_GAME',players:parsed.data.data.players,updatedAt:now()}; broadcast();}}); socket.on('close',()=>clients.delete(socket)); };
 app.get('/api/stream',{websocket:true},(socket:any,req:any)=>bridgeHandler(socket,req));
 app.get('/api/bridge',{websocket:true},(socket:any,req:any)=>bridgeHandler(socket,req));
 function broadcast(){const event={type:'GAME_UPDATE',data:snapshot} as ServerEvent; for(const c of clients) if(c.readyState===1)c.send(JSON.stringify(event));}
 const timer=setInterval(()=>{snapshot={...snapshot,updatedAt:now()};broadcast();},15000); app.addHook('onClose',async()=>clearInterval(timer)); return app;
}
