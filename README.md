# Note-The-Movie

## Docker (build single image for frontend + backend)

A Dockerfile is provided to build both the frontend and the Spring Boot backend into a single runnable image. The Dockerfile builds the frontend (Vite) in a node stage, copies the `dist` into the backend `src/main/resources/static`, builds the Spring Boot fat JAR with Gradle, and produces a runtime image that runs the app (the Spring Boot app serves both API and static frontend files).

Build and run:

```bash
# from repository root
docker build -t note-the-movie:latest .
docker run --rm -p 8080:8080 note-the-movie:latest
```

Open http://localhost:8080 to see the frontend served by the backend.

Notes:
- Ensure you have the TMDB API key available at build or runtime. The frontend expects `VITE_TMDB_API_KEY`. For production, you can supply it to the container as an env var and rebuild, or bake it into a config.
- To change allowed CORS origins or JWT secret, set properties in `build/src/main/resources/application.properties` or pass environment variables (JWT secret property is `notethemovie.jwt.secret`).

Docker Compose
----------------

You can also use docker-compose to build and run the app. The compose file accepts build args and environment variables.

Example using environment variables (create a `.env` or export in your shell):

```bash
# .env file example
VITE_TMDB_API_KEY=your_tmdb_key_here
NOTETHEMOVIE_JWT_SECRET=ReplaceWithAStrongSecret
NOTETHEMOVIE_CORS_ALLOWED_ORIGINS=http://localhost:5173

# build & run
docker-compose build
docker-compose up -d
```

This will build the frontend with the provided `VITE_TMDB_API_KEY` and pass runtime environment variables to the Spring Boot app.
