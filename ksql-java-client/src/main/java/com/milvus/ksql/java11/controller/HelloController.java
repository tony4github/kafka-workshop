package com.milvus.ksql.java11.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milvus.ksql.java11.model.HelloMessage;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class HelloController {

	@GetMapping("/")
	public @ResponseBody String index() {
		System.out.println("Hello Controller");
		HelloMessage msg = new HelloMessage("0", "Hello from Spring Boot! ");
		ObjectMapper objMapper = new ObjectMapper();
		String jsonMsg = null;
		try{
			jsonMsg = objMapper.writeValueAsString (msg);
		} catch (JsonProcessingException ex){
			System.out.println(ex.getMessage());
		}
		return jsonMsg;
	}

}