package dev.horii.overlay;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

final class BridgeClient {
 private final URI uri; private final AtomicBoolean connecting=new AtomicBoolean(false); private volatile WebSocketClient socket; private long retryMs=1000;
 BridgeClient(String endpoint){this.uri=URI.create(endpoint);}
 void connect(){if(!connecting.compareAndSet(false,true))return; new Thread(()->{try{socket=new WebSocketClient(uri){public void onOpen(ServerHandshake h){retryMs=1000;connecting.set(false);send("{\"type\":\"LOBBY_CHANGE\",\"data\":{\"name\":\"hypixel\"}}");} public void onMessage(String m){} public void onClose(int c,String r,boolean remote){connecting.set(false);schedule();} public void onError(Exception e){connecting.set(false);schedule();}}; socket.connect();}catch(Exception e){connecting.set(false);schedule();}}).start();}
 private void schedule(){long wait=retryMs; retryMs=Math.min(30000,retryMs*2);new Thread(()->{try{Thread.sleep(wait);}catch(InterruptedException ignored){}connect();}).start();}
}
