import { buildServer } from './server.js';
const app=await buildServer(); const port=Number(process.env.API_PORT??8787);
app.listen({host:'0.0.0.0',port}).then(()=>console.log(`Horii Overlay API listening on ${port}`)).catch(err=>{console.error(err);process.exit(1)});
