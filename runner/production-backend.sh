#!/usr/bin/bash

# Run the production backend server

fuser -k 8999/tcp || true
java -jar production-backend/libs/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=production #--server.port=8999
