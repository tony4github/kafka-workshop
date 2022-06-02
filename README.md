Install and run Confluent Platform v7.1 on local Mac

1. confluent-7.1.0.tar.gz at http://packages.confluent.io/archive/7.1/

2. confluent cli: create a cli folder 
        air2020-i7@Tianyuans-Air confluent-cli % pwd
            /Users/air2020-i7/milvus-workspace/confluent-cli
        https://s3-us-west-2.amazonaws.com/confluent.cloud/confluent-cli/archives/latest/confluent_latest_darwin_amd64.tar.gz
        curl -sL --http1.1 https://cnfl.io/cli | sh -s -- latest

3. Instruction https://docs.confluent.io/confluent-cli/current/install.html
        export CONFLUENT_HOME=<The directory where Confluent is installed>
        export PATH=$CONFLUENT_HOME/bin:$PATH

4. From the cli folder, start/stop Confluent services 
        (I found confluent services running into timeout issue during start up. And, can be fixed if the following multiple times
                air2020-i7@Tianyuans-MacBook-Air confluent-cli % ./confluent local services start)
        air2020-i7@Tianyuans-Air confluent-cli % ./confluent local services list     
            Available Services:
                Connect
                Control Center
                Kafka
                Kafka REST
                ksqlDB Server
                Schema Registry
                ZooKeeper
        air2020-i7@Tianyuans-Air confluent-cli % ./confluent local services start    
            Starting ZooKeeper
                ZooKeeper is [UP]
                Starting Kafka
                Kafka is [UP]
                Starting Schema Registry
                Schema Registry is [UP]
                Starting Kafka REST
                Kafka REST is [UP]
                Starting Connect
                Connect is [UP]
                Starting ksqlDB Server
                ksqlDB Server is [UP]
                Starting Control Center
                Control Center is [UP]
        http://localhost:9021/
        air2020-i7@Tianyuans-Air confluent-cli % ./confluent local services stop                      
            Stopping Control Center
                Control Center is [DOWN]
                Stopping ksqlDB Server
                ksqlDB Server is [DOWN]
                Stopping Connect
                Connect is [DOWN]
                Stopping Kafka REST
                Kafka REST is [DOWN]
                Stopping Schema Registry
                Schema Registry is [DOWN]
                Stopping Kafka
                Kafka is [DOWN]
                Stopping ZooKeeper
                ZooKeeper is [DOWN]
5. FileStream connectors
    As Mac standalone mode (or maybe cli launch), plugin.path is not able to change by connect-standalone.properties. 
    So, under share/ folder, just copy ./filestream-connectors/connect-file-7.1.0-ce.jar to ./java/
    air2020-i7@Tianyuans-MacBook-Air confluent-cli % confluent local services connect connector status
    Since started by cli, the worker's working dir is the cli launching folder. For this case, /Users/air2020-i7/milvus-workspace/confluent-cli





