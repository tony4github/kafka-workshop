1. copy a clean codebase from https://github.com/tony4github/SpringBootInit . A few changes needs to be made.
    - change pom.xml
    ```
        <artifactId>ksql</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <name>ksql.java11</name>
    ```
    - check testing package. It is 'package com.milvus.ksql.java11.test;' following by '.test'
2. Code practices for ksql api client
    - add ksql client lib by mvn
        - pls read via https://docs.ksqldb.io/en/latest/developer-guide/ksqldb-clients/java-client/#getting-started
        - ksqlDB lib crashs springboot dependencies. You need to address exclusions for LogFactory with Logback. 
        ```
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
                <exclusions>
                    <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                    </exclusion>
                </exclusions>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <exclusions>
                    <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                    </exclusion>
                </exclusions>
                <scope>test</scope>
            </dependency>
        ```
        - for some reasons, i need to disable mirror block true from mvn settings.xml
        ```
            <mirror>
                <id>maven-default-http-blocker</id>
                <mirrorOf>external:http:*</mirrorOf>
                <name>Pseudo repository to mirror external repositories initially using HTTP.</name>
                <url>http://0.0.0.0/</url>
                <blocked>true</blocked>
            </mirror>
        ```
        - This example works with Confluent Platform local, so mvn repo goes to
        ```
            <repository>
                <id>confluent</id>
                <name>confluent-repo</name>
                <url>http://packages.confluent.io/maven/</url>
		    </repository>
        ```
        - the ksql client works with v7.0.6 or below, v7.1 causes version conflicts (I guess Confluent v7.1.0 accommodates v7.0.6 client at best)
        ```
            <dependency>
                <groupId>io.confluent.ksql</groupId>
                <artifactId>ksqldb-api-client</artifactId>
                <version>7.0.6</version>
            </dependency>
        ```
        - I have Confluent v7.1.0 local brokers started 
    - Pull query :  mocking is required before hitting the SpringBoot instance 
        - mocking test case set up (dataGen connectors and ksql table)
        ```
            Intraday(datagen-intraday-pricing) and previousClose(datagen-previousClose) connectors is up and running
            Ksql table (SECURITY_QUOTE_last_TABLE) joined by two steams above is ready
        ```
        - hit the url of 'http://localhost:8080/ksqlPullQuery'
        ```
            A new ksql query controller - KsqlJavaClientController.java. 
            % mvn clean package
            % java -jar ./target/ksql-0.0.1-SNAPSHOT.jar
            
        ```
    - Streaming/Push query and terminiation 
    ```

    ```
3. SpringBoot+Maven testing
    - 3 profiles defined
        - springNative
        - kafkaLocal
        - kafkaRemote
    - SpringBoot profile activated via maven profiles in pom.xml
        - SpringBoot property 'spring.profiles.active=@activeProfile@'
        - Maven profile
        ```
            <profiles>
                <profile>
                    <id>springNative</id>
                    <properties>
                        <activeProfile>springNative</activeProfile>
                    </properties>
                    <activation>
                        <activeByDefault>true</activeByDefault>
                    </activation>
                </profile>
                <profile>
                    <id>kafkaLocal</id>
                    <properties>
                        <activeProfile>kafkaLocal</activeProfile>
                    </properties>
                </profile>
                <profile>
                    <id>kafkaRemote</id>
                    <properties>
                        <activeProfile>kafkaRemote</activeProfile>
                    </properties>
                </profile>
            </profiles>
        ```
    - Maven build and test 
        - if springNative is active, ksql test cases will be executed as a native http call to the root path of '/'
        ```
            % mvn clean package -P springNative
        ```
