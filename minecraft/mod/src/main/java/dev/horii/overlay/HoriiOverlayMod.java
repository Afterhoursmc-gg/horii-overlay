package dev.horii.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;
import java.awt.Desktop;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.network.NetworkPlayerInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.UUID;

@Mod(modid="horii_overlay", name="Horii Overlay", version="0.1.0", clientSideOnly=true)
public final class HoriiOverlayMod {
  private static final String WEB_URL="https://statsoverlay.horii.dev";
  private final String sessionId=UUID.randomUUID().toString().replace("-","");
  private final KeyBinding insert=new KeyBinding("Open Horii Overlay", Keyboard.KEY_RSHIFT, "Horii Overlay");
  private BridgeClient bridge;
  private String lastPlayers="";
  private int scanTicks=0;
  @Mod.EventHandler public void init(FMLInitializationEvent event){
    ClientRegistry.registerKeyBinding(insert); MinecraftForge.EVENT_BUS.register(this);
    String token=System.getProperty("horii.overlay.bridgeToken", ""); String endpoint=System.getProperty("horii.overlay.bridge", "wss://statsoverlay.horii.dev/api/bridge");
    endpoint += (endpoint.contains("?") ? "&" : "?") + "session=" + sessionId;
    if(!token.isEmpty()) endpoint += "&token=" + token;
    bridge=new BridgeClient(endpoint); bridge.connect();
  }
  @SubscribeEvent public void onClientTick(TickEvent.ClientTickEvent event){
    if(event.phase!=TickEvent.Phase.END || bridge==null || mc().thePlayer==null || mc().thePlayer.sendQueue==null)return;
    if(++scanTicks<20)return; scanTicks=0;
    List<String> names=new ArrayList<String>();
    for(NetworkPlayerInfo info:mc().thePlayer.sendQueue.getPlayerInfoMap()){
      if(info.getGameProfile()!=null && info.getGameProfile().getName()!=null)names.add(info.getGameProfile().getName());
    }
    Collections.sort(names,String.CASE_INSENSITIVE_ORDER);
    if(names.isEmpty())return;
    String joined=names.toString(); if(joined.equals(lastPlayers))return; lastPlayers=joined;
    StringBuilder json=new StringBuilder("{\\\"type\\\":\\\"GAME_START\\\",\\\"data\\\":{\\\"mode\\\":\\\"UNKNOWN\\\",\\\"players\\\":[");
    for(int i=0;i<names.size()&&i<32;i++){if(i>0)json.append(',');json.append("{\\\"username\\\":\\\"").append(names.get(i)).append("\\\"}");}
    json.append("]}}"); bridge.send(json.toString());
  }
  @SubscribeEvent public void onKey(InputEvent.KeyInputEvent event){ if(insert.isPressed()) openWebOverlay(); }
  private void openWebOverlay(){
    boolean opened=false; String overlayUrl=WEB_URL+"?session="+sessionId;
    try {
      URI uri=new URI(overlayUrl);
      if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)){
        Desktop.getDesktop().browse(uri); opened=true;
      }
      if(!opened && System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")){
        Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", overlayUrl}); opened=true;
      }
      if(!opened) Runtime.getRuntime().exec(new String[]{"xdg-open", WEB_URL});
      mc().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("[Horii Overlay] Opening " + WEB_URL));
    } catch(Exception error){
      mc().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("[Horii Overlay] Could not open browser: " + error.getClass().getSimpleName()));
    }
  }
  public static Minecraft mc(){ return Minecraft.getMinecraft(); }
}
