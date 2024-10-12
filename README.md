# Steps to setup
1. Install cockroachDB locally
2. Run it as a single node via the command given below:
    cockroach start-single-node --insecure --listen-addr=localhost:26257 --http-port=8080
3. Verify if it's running: 
    lsof -i :26257


