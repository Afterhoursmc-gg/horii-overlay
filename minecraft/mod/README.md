# Horii Overlay Forge 1.8.9 mod

Build with Java 8 and Gradle/ForgeGradle 2.3. The mod uses Right Shift (RSHIFT) to open `https://statsoverlay.horii.dev` and maintains a reconnecting WebSocket bridge. The initial bridge is intentionally conservative: only client-observed events are sent. No Hypixel API key is shipped in the mod.

The web overlay remains the main interface; this mod does not use inventory/chest GUI screens.
