#!/usr/bin/bash

# Run the staging backend server

fuser -k 9499/tcp || true
java -jar staging-backend/libs/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=staging #--server.port=9499
