package dev.horii.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import java.awt.Desktop;
import java.net.URI;

@Mod(modid="horii_overlay", name="Horii Overlay", version="0.1.0", clientSideOnly=true)
public final class HoriiOverlayMod {
  private static final String WEB_URL="https://statsoverlay.horii.dev";
  private final KeyBinding insert=new KeyBinding("Open Horii Overlay", Keyboard.KEY_INSERT, "Horii Overlay");
  private BridgeClient bridge;
  @Mod.EventHandler public void init(FMLInitializationEvent event){
    ClientRegistry.registerKeyBinding(insert); MinecraftForge.EVENT_BUS.register(this);
    String token=System.getProperty("horii.overlay.bridgeToken", ""); String endpoint=System.getProperty("horii.overlay.bridge", "ws://127.0.0.1:8787/api/bridge");
    if(!token.isEmpty()) endpoint += (endpoint.contains("?") ? "&" : "?") + "token=" + token;
    bridge=new BridgeClient(endpoint); bridge.connect();
  }
  @SubscribeEvent public void onKey(InputEvent.KeyInputEvent event){ if(insert.isPressed()) openWebOverlay(); }
  private void openWebOverlay(){ try { if(Desktop.isDesktopSupported()) Desktop.getDesktop().browse(new URI(WEB_URL)); } catch(Exception ignored){} }
  public static Minecraft mc(){ return Minecraft.getMinecraft(); }
}
