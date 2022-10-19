package com.milvus.ksql.java11.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.milvus.ksql.java11.model.PriceQuote;
import com.milvus.ksql.java11.service.RowSubscriber;

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.Row;
import io.confluent.ksql.api.client.ServerInfo;
import io.confluent.ksql.api.client.StreamedQueryResult;

import org.springframework.beans.factory.annotation.Value;

@RestController
public  class KsqlJavaClientController {

  @Value("${ksqlDB.host:localhost}")
  private String ksqlHost;

  @Value("${ksqlDB.host.port:8088}")
  private int ksqlHostPort;

  @Value("${ksqlDB.test.quote.query.pull:select * from ksql_processing_log ;}")
  private String ksqlDBTestQuoteQueryPull;
  
  @Value("${ksqlDB.test.quote.stream.polling.limit:0}")
  private int ksqlDBTestQuoteStreamPollingLimit;

  @Value("${ksqlDB.test.quote.stream.polling:select * from ksql_processing_log emit changes ;}")
  private String ksqlDBTestQuoteStreamPolling;

  @Value("${ksqlDB.test.quote.stream.reactive:select * from ksql_processing_log emit changes ;}")
  private String ksqlDBTestQuoteStreamReactive;

  @GetMapping("/ksqlReactiveStream")
  public @ResponseBody String pushByReactive() throws Exception{
    Client client = connectHost();

    CompletableFuture<Void> futurnVoid = client.streamQuery(ksqlDBTestQuoteStreamReactive)
            .thenAccept(streamedQueryResult -> {
                            System.out.println("Reactive query has started. Query ID: " + streamedQueryResult.queryID());
                            RowSubscriber subscriber = new RowSubscriber();
                            streamedQueryResult.subscribe(subscriber); })
              .exceptionally(e -> {
                            System.out.println("Reactive stream request failed: " + e);
                            return null; });
    Thread.sleep(5000);
    if(futurnVoid.isDone())
      System.out.println("The future is done. " + futurnVoid.toString());
    return "[ Reactive Stream has been activated... The stream stops once an invalid quote consumed, like $0 or below]";

  }

	@GetMapping("/ksqlPollingStream")
  public @ResponseBody String pushByPolling() throws Exception{
    Client client = connectHost();

    ObjectMapper objMapper = new ObjectMapper();
    List<String> jsonList = new ArrayList<String>() ;

    // Send requests with the client by following the other examples
    System.out.println("pollingQuery : " + ksqlDBTestQuoteStreamPolling);
    StreamedQueryResult streamedQueryResult = client.streamQuery(ksqlDBTestQuoteStreamPolling).get();
    for (int i = 0; i < ksqlDBTestQuoteStreamPollingLimit; i++) {
      System.out.println(">>>Polling limit:"+ksqlDBTestQuoteStreamPollingLimit+" , Polling #" + i);
      // Block until a new row is available
      Row row = streamedQueryResult.poll();
      if (row != null) {
        System.out.println("Received a row!" + row.toString());
        System.out.println("Row: " + row.values());
        jsonList.add(getJsonMessage(row, objMapper));
      } else {
        System.out.println("Query has ended.");
      }
    }
    try{
      return objMapper.writeValueAsString(jsonList);
    } catch (JsonProcessingException ex){
      System.out.println(ex.getMessage());
      return null;
    }
  }
  

	@GetMapping("/ksqlPullQuery")
  public @ResponseBody String pullQuery() throws Exception{
    Client client = connectHost();

    // Send requests with the client by following the other examples
    System.out.println("pullQuery : " + ksqlDBTestQuoteQueryPull);
    CompletableFuture<List<Row>> ret = client.executeQuery(ksqlDBTestQuoteQueryPull);
    List<Row> aList = ret.get();
    System.out.println("pullQuery return a list of size: " + aList.size());
    
    System.out.println(aList);
    // Terminate any open connections and close the client
    client.close();
    
    ObjectMapper objMapper = new ObjectMapper();
    List<String> jsonList = new ArrayList<String>() ;
		try{
      for(Row r : aList){
        System.out.println(r.getString("CUSIP") + " <> $"+r.getString("LATEST_QUOTE"));
        jsonList.add(getJsonMessage(r, objMapper));
      }
			return objMapper.writeValueAsString(jsonList);
	  } catch (JsonProcessingException ex){
			System.out.println(ex.getMessage());
      return null;
		}
  }

  private String getJsonMessage (Row row , ObjectMapper objMapper){
    PriceQuote q = new PriceQuote();
    q.setCusip(row.getString("CUSIP"));
    q.setLatestPrice(row.getString("LATEST_QUOTE"));
    String ret = null;
    try {
       ret = objMapper.writeValueAsString(q);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } 
    return ret;
  }

  private Client connectHost() throws InterruptedException, ExecutionException{
    System.out.println("ksql controller hit by "+ ksqlHost+":"+ksqlHostPort);

    ClientOptions options = ClientOptions.create()
        .setHost(ksqlHost)
        .setPort(ksqlHostPort);
    Client client = Client.create(options);

    ServerInfo serverInfo = client.serverInfo().get();
    System.out.println("The ksqlDB version running on this server is " + serverInfo.getServerVersion());
    System.out.println("The Kafka cluster this server is using is " + serverInfo.getKafkaClusterId());
    System.out.println("The id of this ksqlDB service is " + serverInfo.getKsqlServiceId());

    return client;
  }

} 