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

import io.confluent.ksql.api.client.Client;
import io.confluent.ksql.api.client.ClientOptions;
import io.confluent.ksql.api.client.Row;
import io.confluent.ksql.api.client.ServerInfo;

import org.springframework.beans.factory.annotation.Value;

@RestController
public  class KsqlJavaClientController {

  @Value("${ksqlDB.host:localhost}")
  private String ksqlHost;

  @Value("${ksqlDB.host.port:8088}")
  private int ksqlHostPort;

  @Value("${ksqlDB.test.quote.query:select * from ksql_processing_log ;}")
  private String ksqlDBTestQuoteQuery;

	@GetMapping("/ksqlQuery")
  public @ResponseBody String query() throws Exception{
    System.out.println("ksql controller hit by "+ ksqlHost+":"+ksqlHostPort);

    ClientOptions options = ClientOptions.create()
        .setHost(ksqlHost)
        .setPort(ksqlHostPort);
    Client client = Client.create(options);

    ServerInfo serverInfo = client.serverInfo().get();
    System.out.println("The ksqlDB version running on this server is " + serverInfo.getServerVersion());
    System.out.println("The Kafka cluster this server is using is " + serverInfo.getKafkaClusterId());
    System.out.println("The id of this ksqlDB service is " + serverInfo.getKsqlServiceId());
    
    // Send requests with the client by following the other examples
    System.out.println("pullQuery : " + ksqlDBTestQuoteQuery);
    CompletableFuture<List<Row>> ret = client.executeQuery(ksqlDBTestQuoteQuery);
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
} 