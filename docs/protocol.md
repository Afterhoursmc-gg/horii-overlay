# WebSocket Protocol

Client events are validated with `@horii-overlay/protocol`. Client messages include `GAME_START`, `GAME_LEAVE`, `PLAYER_JOIN`, `PLAYER_LEAVE`, `PLAYER_UPDATE`, `LOBBY_CHANGE`, and `GAME_END`. Server messages include `GAME_UPDATE`, `CONNECTION_STATE`, and `ERROR`. Unknown or oversized payloads are rejected.
