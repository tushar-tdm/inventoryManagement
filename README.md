# Steps to setup
1. Install cockroachDB locally
2. Run it as a single node via the command given below:
    cockroach start-single-node --insecure --listen-addr=localhost:26257 --http-port=8080
3. Verify if it's running: 
    lsof -i :26257

# To run on a specific profile:
./gradlew bootRun -D spring.profiles.active=prod

# To run prometheus
prometheus --config.file=/Users/tusharm/Documents/java/inventoryManagement/prometheus.yml
## Verify by running:
http://localhost:9090

# To run Grafana:
brew services start grafana
## Verify by running:
http://localhost:3000