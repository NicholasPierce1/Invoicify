package com.example.invoicify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = InvoicifyApplicationTests.class)
@AutoConfigureMockMvc
class InvoicifyApplicationTests {

	@Test
	void contextLoads() {
	}

}
