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
  @Mod.EventHandler public void init(FMLInitializationEvent event){
    ClientRegistry.registerKeyBinding(insert); MinecraftForge.EVENT_BUS.register(this);
    String token=System.getProperty("horii.overlay.bridgeToken", ""); String endpoint=System.getProperty("horii.overlay.bridge", "wss://statsoverlay.horii.dev/api/bridge");
    endpoint += (endpoint.contains("?") ? "&" : "?") + "session=" + sessionId;
    if(!token.isEmpty()) endpoint += "&token=" + token;
    bridge=new BridgeClient(endpoint); bridge.connect();
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
