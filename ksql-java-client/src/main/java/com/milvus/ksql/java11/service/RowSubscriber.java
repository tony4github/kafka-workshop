package com.milvus.ksql.java11.service;
import io.confluent.ksql.api.client.Row;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class RowSubscriber implements Subscriber<Row> {

  private Subscription subscription;

  public RowSubscriber() {
  }

  @Override
  public synchronized void onSubscribe(Subscription subscription) {
    System.out.println("---Reactive--- Subscriber is subscribed.");
    this.subscription = subscription;

    // Request the first row
    subscription.request(1);
  }

  @Override
  public synchronized void onNext(Row row) {
    System.out.println("---Reactive--- Received a row!");
    System.out.println("---Reactive--- Row: " + row.values());

    // Request the next row
    subscription.request(1);
    if(row.toString().contains("55.04")){// This is a trick to stop the async subscription/thread
        subscription.cancel();
        System.out.println("---Reactive--- subscription cancelled.");
    }

  }

  @Override
  public synchronized void onError(Throwable t) {
    System.out.println("---Reactive--- Received an error: " + t);
  }

  @Override
  public synchronized void onComplete() {
    System.out.println("---Reactive--- Query has ended.");
  }
}