# Database

PostgreSQL schema migrations live in `migrations/`. Apply them with the deployment migration runner. The API currently uses an in-memory fixture snapshot for deterministic MVP development; persistence is isolated behind this schema for the next slice.
