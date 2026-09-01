# Architecture

The Forge 1.8.9 client observes only visible client state and sends validated events to the Fastify bridge. The API owns provider credentials, rate limits, cache, sessions, and WebSocket fan-out. The Next.js app receives sanitized snapshots over WebSocket. PostgreSQL stores durable records; Redis stores cache/rate-limit state.

The first implementation uses fixture data for deterministic local development. Hypixel integration is server-side only and requires `HYPIXEL_API_KEY`.
