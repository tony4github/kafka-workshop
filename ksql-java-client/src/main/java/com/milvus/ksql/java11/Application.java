package com.milvus.ksql.java11;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class Application implements CommandLineRunner{

	@Value("${server.port}")
    private int serverPort;

	@Value("${spring.profiles.default}")
    private String springProfilesDefault;

	@Value("${spring.profiles.active}")
    private String springProfilesActive;

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		
		System.out.println("Let's inspect the beans provided by Spring Boot:" );

		String[] beanNames = ctx.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (String beanName : beanNames) {
			System.out.println(beanName);
		}
	}

	@Override
   	public void run(String... arg0) throws Exception {
		System.out.println("====From CommandLineRunner==== server.port: " + serverPort);
		System.out.println("====From CommandLineRunner==== spring.profiles.default: " + springProfilesDefault);
		System.out.println("====From CommandLineRunner==== spring.profiles.active: " + springProfilesActive);
	}
}
