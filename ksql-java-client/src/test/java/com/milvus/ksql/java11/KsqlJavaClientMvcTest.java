package com.milvus.ksql.java11.test;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


/*
 * MVC layer testing - not start the server at all but to test only the layer below that
 * 	Spring handles the incoming HTTP request and hands it off to your controller. 
 * 	That way, almost of the full stack is used, and your code will be called in exactly the same way. 
 * 	To do that, use Spring’s MockMvc and ask for that to be injected for you by using the @AutoConfigureMockMvc
 */
@SpringBootTest
@AutoConfigureMockMvc
public class KsqlJavaClientMvcTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Value("${spring.profiles.active}")
    private String springProfilesActive;

	/**
	 * @throws Exception
	 */
	@Test
	public void testPullQuery() throws Exception {
		System.out.println("springProfilesActive="+springProfilesActive);
		if ("springNative".equalsIgnoreCase(springProfilesActive)){
			this.mockMvc.perform(get("/"))
				.andExpect(status().isOk());
		}else
			this.mockMvc.perform(get("/ksqlPullQuery")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(startsWith("[")));
		
	}

	@Test
	public void testPollingStream() throws Exception {
		System.out.println("springProfilesActive="+springProfilesActive);
		if ("springNative".equalsIgnoreCase(springProfilesActive)){
			System.out.println(" disable a test... ");
			this.mockMvc.perform(get("/"))
				.andExpect(status().isOk());
		}else
			this.mockMvc.perform(get("/ksqlPollingStream")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(startsWith("[")));
	}

	@Test
	public void testReactiveStream() throws Exception {
		System.out.println("springProfilesActive="+springProfilesActive);
		if ("springNative".equalsIgnoreCase(springProfilesActive))
			this.mockMvc.perform(get("/"))
				.andExpect(status().isOk());
		else
			this.mockMvc.perform(get("/ksqlReactiveStream")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(startsWith("[")));
	}
}