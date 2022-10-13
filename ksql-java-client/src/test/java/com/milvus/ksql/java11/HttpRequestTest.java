package com.milvus.ksql.java11.test;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Value;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

	@Value("${local.server.port}")
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void greetingShouldReturnDefaultMessage() throws Exception {
        System.out.println("port:"+ port);
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/",
				String.class)).contains("Hello");
	}
}
