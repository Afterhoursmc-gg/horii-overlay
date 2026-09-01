# Deployment

Run PostgreSQL and Redis with Docker Compose. Put the API behind HTTPS/WSS using Nginx. Keep `HYPIXEL_API_KEY`, database credentials, Redis credentials, session secrets, and Discord secrets server-side. Use DNS only for Minecraft records and a separate HTTPS host for the web/API.
