package com.milvus.ksql.java11.model;

import io.confluent.ksql.api.client.Row;

public class EventProceeding {
    public static boolean isAlarming(Row row){
        if(Double.parseDouble(row.getString("LATEST_QUOTE")) < 0.0000000000001)
            return true;
        else 
            return false;
    }
}