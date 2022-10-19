package com.milvus.ksql.java11.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;

import com.milvus.ksql.java11.controller.HelloController;



@SpringBootTest
public class SmokeTest {

	@Autowired
	private HelloController controller;

	@Value("${server.port}")
	private int serverPort;

	@Value("${spring.profiles.active}")
    private String springProfilesActive;

	@Test
	public void contextLoads() throws Exception {
		System.out.println("---via SmokeTest, reading .properties files---- server.port:" + serverPort);
		System.out.println("---via SmokeTest, reading .properties files---- spring.profiles.active:" + springProfilesActive);
		assertThat(controller).isNotNull();
	}
}