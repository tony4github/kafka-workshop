Install and run Confluent Platform v7.1 on local Mac

1. confluent-7.1.0.tar.gz at http://packages.confluent.io/archive/7.1/

2. confluent cli: create a cli folder 
    ```
        air2020-i7@Tianyuans-Air confluent-cli % pwd
            /Users/air2020-i7/milvus-workspace/confluent-cli
        https://s3-us-west-2.amazonaws.com/confluent.cloud/confluent-cli/archives/latest/confluent_latest_darwin_amd64.tar.gz
        curl -sL --http1.1 https://cnfl.io/cli | sh -s -- latest
    ```

3. Instruction https://docs.confluent.io/confluent-cli/current/install.html
    ```
        export CONFLUENT_HOME=<The directory where Confluent is installed>
        export PATH=$CONFLUENT_HOME/bin:$PATH
    ```

4. From the cli folder, start/stop Confluent services 
    ```
        air2020-i7@Tianyuans-Air confluent-cli % ./confluent local current
            /var/folders/0h/lby64v4n2hnc25tfdqbmzzlm0000gn/T/confluent.163981
        air2020-i7@Tianyuans-Air confluent.163981 % ls -al /var/folders/0h/lby64v4n2hnc25tfdqbmzzlm0000gn/T/confluent.163981 
            total 0
            drwxr-xr-x    9 air2020-i7  staff    288 Jun 10 09:48 .
            drwx------@ 446 air2020-i7  staff  14272 Jun 10 11:53 ..
            drwxr-xr-x    7 air2020-i7  staff    224 Jun 10 10:41 connect
            drwxr-xr-x    6 air2020-i7  staff    192 Jun 10 10:43 control-center
            drwxr-xr-x    7 air2020-i7  staff    224 Jun 10 10:45 kafka
            drwxr-xr-x    7 air2020-i7  staff    224 Jun 10 10:02 kafka-rest
            drwxr-xr-x    6 air2020-i7  staff    192 Jun 10 10:43 ksql-server
            drwxr-xr-x    6 air2020-i7  staff    192 Jun 10 10:43 schema-registry
            drwxr-xr-x    7 air2020-i7  staff    224 Jun 10 10:53 zookeeper
        Kill confluent.current and refresh all service instances from the current config
                air2020-i7@Tianyuans-Air T % pwd
                    /var/folders/0h/lby64v4n2hnc25tfdqbmzzlm0000gn/T    
                air2020-i7@Tianyuans-Air T % mv confluent.current confluent.current.backup
        I found confluent services running into timeout issue during start up. And, can be fixed if the following multiple times
                air2020-i7@Tianyuans-MacBook-Air confluent-cli % ./confluent local services start
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
    ```
5. FileStream source/sink connectors - premature product, not much usable! pls skip this section
    ```
    As Mac standalone mode (or maybe cli launch), plugin.path is not able to change by connect-standalone.properties. 
    So, under share/ folder, just copy ./filestream-connectors/connect-file-7.1.0-ce.jar to ./java/
    cli command for the connectors, which reaches to etc/kafak/connect-file-source.properties and etc/kafak/connect-file-sink.properties
        air2020-i7@Tianyuans-MacBook-Air confluent-cli % confluent local services connect status
        air2020-i7@Tianyuans-MacBook-Air confluent-cli % confluent local services connect connector load file-source 
        air2020-i7@Tianyuans-MacBook-Air confluent-cli % confluent local services connect connector unload file-source 
        air2020-i7@Tianyuans-MacBook-Air confluent-cli % confluent local services connect connector status file-source
        air2020-i7@Tianyuans-MacBook-Air confluent-cli % confluent local services connect connector load file-sink 
        air2020-i7@Tianyuans-MacBook-Air confluent-cli % confluent local services connect connector unload file-sink
        air2020-i7@Tianyuans-MacBook-Air confluent-cli % confluent local services connect connector status file-sink 
        air2020-i7@Tianyuans-MacBook-Air confluent-cli % confluent local services connect connector status
    There is not schema engaged, so a message key/value needs to be a converter
        "key.converter": "org.apache.kafka.connect.storage.StringConverter",
        "value.converter": "org.apache.kafka.connect.storage.StringConverter",
    Since started by cli, supposedly, the worker's working dir is the cli launching folder. At my local, /Users/air2020-i7/milvus-workspace/confluent-cli. For some reasons, it does not work. I have to assign an absolute folder in connect-file-source.properties or file-source.json 
        "file": "/Users/air2020-i7/milvus-workspace/confluent-cli/test.txt"
    There are 3 files and 1 topic created
        a backup file with cusips feed - previousClose-cusips.json
        source file - /Users/air2020-i7/milvus-workspace/confluent-cli/test.txt
        sink file - /Users/air2020-i7/milvus-workspace/confluent-cli/test.sink.txt
        "topic": "connect-test"
        air2020-i7@Tianyuans-Air confluent-cli % cat /Users/air2020-i7/milvus-workspace/kafka-workshop/previousClose-cusips.json >>test.txt
        air2020-i7@Tianyuans-Air confluent-cli % more test.sink.txt
    ```
6. Datagen - customized feed for intraday pricing 
    ```
    installation - https://docs.confluent.io/kafka-connect-datagen/current/index.html
    connector config - https://github.com/confluentinc/kafka-connect-datagen/tree/master/config
    FactSet intraday connector
            "name": "datagen-intraday-pricing",
            "kafka.topic": "feed-intraday-pricing",  
        Key is a String, plus message value takes Json format
            "key.converter": "org.apache.kafka.connect.storage.StringConverter",
            "value.converter": "org.apache.kafka.connect.json.JsonConverter",
        Schema is used by this connector
            "schema.filename": "/Users/air2020-i7/milvus-workspace/kafka-workshop/datagen-intradayPricing.avro",
            "schema.keyfield": "identifier"
    Trust cusips+previous close connector
            "name": "datagen-previousClose",
            "kafka.topic": "feed-previousClose",
        Key is a String, plus message value takes Json format
            "key.converter": "org.apache.kafka.connect.storage.StringConverter",
            "value.converter": "org.apache.kafka.connect.json.JsonConverter",
        Schema setting
            "schema.filename": "/Users/air2020-i7/milvus-workspace/kafka-workshop/datagen-previousClose.avro",
            "schema.keyfield": "identifier",
    ```
7. May not use this anymore ... Create a compacted log for cusips topic
    ```
    uid/pwd : a@b.com/123
    air2020-i7@Tianyuans-Air confluent-cli % ./confluent kafka topic create feed-previousClose-compacted --url http://localhost:8082 --config cleanup.policy=compact --replication-factor 1 
    ```
8. ksqlDB
    ```
    a.  'intraday_pricing_stream' - Factset intraday pricing
        CREATE OR REPLACE stream intraday_pricing_stream 
            (IDENTIFIER STRING , last STRING, lastTIMESTAMP STRING) 
                WITH (KAFKA_TOPIC='feed-intraday-pricing', KEY_FORMAT='KAFKA', VALUE_FORMAT='JSON');
        select * from intraday_pricing_stream  EMIT CHANGES;
    b.  'QUERYABLE_PREVIOUS_CLOSE_TABLE' - AMG trust cusips with previous close
        CREATE OR REPLACE stream PREVIOUS_CLOSE_stream 
            (IDENTIFIER STRING , PREVIOUSCLOSE STRING, PREVIOUSCLOSETIMESTAMP STRING) 
                WITH (KAFKA_TOPIC='feed-previousClose', KEY_FORMAT='KAFKA', VALUE_FORMAT='JSON');
        CREATE OR REPLACE TABLE PREVIOUS_CLOSE_TABLE 
            (IDENTIFIER STRING PRIMARY KEY, PREVIOUSCLOSE STRING, PREVIOUSCLOSETIMESTAMP STRING) 
                WITH (KAFKA_TOPIC='feed-previousClose', KEY_FORMAT='KAFKA', VALUE_FORMAT='JSON');
        CREATE OR REPLACE TABLE QUERYABLE_PREVIOUS_CLOSE_TABLE 
            WITH (KAFKA_TOPIC='QUERYABLE_PREVIOUS_CLOSE') 
                AS SELECT * FROM PREVIOUS_CLOSE_TABLE PREVIOUS_CLOSE_TABLE 
                    EMIT CHANGES;
        SELECT * FROM  PREVIOUS_CLOSE_TABLE emit changes;
        SELECT * FROM  QUERYABLE_PREVIOUS_CLOSE_TABLE where IDENTIFIER in ('US:000304105', 'US:00032Q104','US:000360206', 'US:000375204', 'US:000380204', 'US:000255109', 'US:000361105');
    c0. The best approach to resolve the stream/table join and the last-mile-delivery issues
        -   ksql table to show the latest quote
                CREATE OR REPLACE STREAM security_quote_stream
                    WITH (kafka_topic='security_quote',
                        value_format='json') AS
                    SELECT intraday_pricing_stream.identifier, intraday_pricing_stream.LAST, intraday_pricing_stream.LASTTIMESTAMP, PREVIOUSCLOSE, PREVIOUSCLOSETIMESTAMP
                        FROM intraday_pricing_stream
                            JOIN QUERYABLE_PREVIOUS_CLOSE_TABLE ON intraday_pricing_stream.identifier = QUERYABLE_PREVIOUS_CLOSE_TABLE.identifier;
                CREATE TABLE SECURITY_QUOTE_last_TABLE 
                        AS SELECT 	SECURITY_QUOTE_STREAM.INTRADAY_PRICING_STREAM_IDENTIFIER cusip,
                                LATEST_BY_OFFSET(SECURITY_QUOTE_STREAM.LAST) LATEST_QUOTE
                            FROM SECURITY_QUOTE_STREAM
                                GROUP BY SECURITY_QUOTE_STREAM.INTRADAY_PRICING_STREAM_IDENTIFIER
                        EMIT CHANGES;
                select * from SECURITY_QUOTE_last_TABLE where cusip in ('US:000304105', 'US:00032Q104','US:000360206', 'US:000375204', 'US:000380204', 'US:000255109', 'US:000361105');
        -   Java client to connect a ksql table 
            - Pls check up the 'ksql-java-client' project
            - Reference 
            ```
                https://docs.ksqldb.io/en/latest/developer-guide/ksqldb-clients/java-client/
                    String pullQuery = "SELECT * FROM MY_MATERIALIZED_TABLE WHERE KEY_FIELD='some_key';";
                    BatchedQueryResult batchedQueryResult = client.executeQuery(pullQuery);

                    // Wait for query result
                    List<Row> resultRows = batchedQueryResult.get();

                    System.out.println("Received results. Num rows: " + resultRows.size());
                    for (Row row : resultRows) {
                        System.out.println("Row: " + row.values());
                    }
            ```
    c1.  'QUERYABLE_security_quote_TABLE' - Stream/Table Join (key=cusips identifier) between feed-intraday-pricing and previous-close 
        CREATE OR REPLACE STREAM security_quote_stream
            WITH (kafka_topic='security_quote',
                value_format='json') AS
            SELECT intraday_pricing_stream.identifier, intraday_pricing_stream.LAST, intraday_pricing_stream.LASTTIMESTAMP, PREVIOUSCLOSE, PREVIOUSCLOSETIMESTAMP
                FROM intraday_pricing_stream
                    JOIN QUERYABLE_PREVIOUS_CLOSE_TABLE ON intraday_pricing_stream.identifier = QUERYABLE_PREVIOUS_CLOSE_TABLE.identifier;
        CREATE OR REPLACE TABLE security_quote_TABLE 
            (IDENTIFIER STRING PRIMARY KEY, PREVIOUSCLOSE STRING, PREVIOUSCLOSETIMESTAMP STRING, LAST STRING, LASTTIMESTAMP STRING) 
                WITH (KAFKA_TOPIC='security_quote', KEY_FORMAT='KAFKA', VALUE_FORMAT='JSON');
        CREATE OR REPLACE TABLE QUERYABLE_security_quote_TABLE 
            WITH (KAFKA_TOPIC='security_quote_TABLE', PARTITIONS=1, REPLICAS=1) 
                AS SELECT * FROM security_quote_TABLE security_quote_TABLE 
                    EMIT CHANGES;
        select IDENTIFIER as cusip , PREVIOUSCLOSE as pre_Close, LAST , PREVIOUSCLOSETIMESTAMP as pre_TS, LASTTIMESTAMP last_TS  
            from QUERYABLE_security_quote_TABLE ;
        if you want to save messages from the join, file sink connector is able to help 
            {
                "_comment":"A Json format of connect-file-sink.properties. Surprisingly, no need to designate an absolute folder for the sink file. And, StringConverter is better here ",
                "name": "file-sink",
                "config": {
                    "name": "file-sink",
                    "connector.class": "FileStreamSink",
                    "tasks.max": "1",
                    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
                    "value.converter": "org.apache.kafka.connect.storage.StringConverter",
                    "topics": "security_quote_TABLE",
                    "file": "test.sink.txt"
                }
            }
    d.  ksql REST API
        i.  emit changes in a real-time fashing
            curl --http1.1 \
                -X "POST" "http://localhost:8088/query" \
                -H "Accept: application/vnd.ksql.v1+json" \
                -d $'{
            "ksql": "SELECT * FROM SECURITY_QUOTE_last_TABLE EMIT CHANGES;",
            "streamsProperties": {}
            }'
        ii. pull query for the latest - one shot at a time. 
            curl --http1.1 \
                -X "POST" "http://localhost:8088/query" \
                -H "Accept: application/vnd.ksql.v1+json" \
                -d $'{
            "ksql": "SELECT * FROM SECURITY_QUOTE_last_TABLE ;",
            "streamsProperties": {}
            }'
    ```
9. Schema registry - http://localhost:8081/subjects/cusip-value/versions/
    ```
    air2020-i7@Tianyuans-Air jq % curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" -d @./cusipKey.avro http://localhost:8081/subjects/feed-previousClose-key/versions
    air2020-i7@Tianyuans-Air jq % curl -X DELETE http://localhost:8081/subjects/feed-previousClose-key/
        {
            "subject": "feed-previousClose-key",
            "version": 3,
            "id": 5,
            "schema": "{\"type\":\"record\",\"name\":\"cusipKey\",\"namespace\":\"com.pnc.amg.security\",\"fields\":[{\"name\":\"identifier\",\"type\":\"string\"}]}"
        }
    air2020-i7@Tianyuans-Air jq % curl http://localhost:8081/subjects/feed-previousClose-key/versions/3 |jq .
    Under Confluent Kafka, a schema is registed under a 'subject' by naming as topicName-key/topicName-value
    schema definition in previousCloseValue.avro 
        { 
            "schema": 
            "{
                \"type\": \"record\",
                \"name\": \"previousCloseValue\",
                \"namespace\": \"com.pnc.amg.security\",
                \"fields\": [
                    {
                        \"name\": \"identifier\",
                        \"type\": \"string\"
                    },
                    {
                        \"name\": \"previousClose\",
                        \"type\": \"string\"
                    },
                    {
                        \"name\": \"previousCloseTimestamp\",
                        \"type\": \"string\"
                    }
                ]
            }"
        }
    schema definition in cusipKey.avro 
         { 
            "schema": 
            "{
                \"type\": \"record\",
                \"name\": \"cusipKey\",
                \"namespace\": \"com.pnc.amg.security\",
                \"fields\": [
                    {
                        \"name\": \"identifier\",
                        \"type\": \"string\"
                    }
                ]
            }"
        }
    ```
10. Demo steps
    ```
    a.  Confluent local
        i.  % confluent local services start
        ii. % confluent local services stop
        iii.% confluent local current
    b.  C3 - http://localhost:9021/clusters/
    c.  Feed Connectors 
        i.  Construct schemas and mocking data
            datagen-previousClose.avro      - 3 cusips
            datagen-intradayPricing.avro    - 5 cusips 
            Later, we will grow test cases to 5+5 cusips(inner join) and 7+5 cusips (right join)
        ii. launch FactSet feeding via datagen-intradayPricing.json
        iii.launch AM Trust feeding via datagen-previousClose.json
    d.  ksqlDB
        i.  create a stream of 'intraday pricing', which can be verified by a push query - 'emit changes;'
        ii. create a querable table of 'previous close' that is verified by a pull query - NO emit
        ii. create a stream/table join between 'intraday pricing' and 'previous close'. And, it is querable via a pull query - NO emit
    e.  Sink connector - FileStream sink connector can save the joined message into a file
    f.  Visualization - data pipeline and flow
    g.  Flexibility
        i. inner join 
            5 cusips of 'previousClose' 
            5 cusips of 'intraday pricing'
            change the prices and show the changes in seconds
        ii. left join - need to kill the existing persistent query joining previously
            5 cusips of 'previousClose' 
            7 cusips of 'intraday pricing'
    h.  schema application
    ```
        



    
    
    







