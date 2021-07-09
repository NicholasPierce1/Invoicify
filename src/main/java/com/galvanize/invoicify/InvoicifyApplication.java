package com.galvanize.invoicify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"com.galvanize"})
public class InvoicifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoicifyApplication.class, args);
	}
}
