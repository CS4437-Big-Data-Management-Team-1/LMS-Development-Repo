package com.example.LMS;

import org.springframework.boot.SpringApplication;
import org.springfrmework.web.bind.annotation.GetMapping
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LmsApplication {

	@GetMapping("/")
	public String home(){
		return "Hello from the other side :)";
	}
	public static void main(String[] args) {
		SpringApplication.run(LmsApplication.class, args);
	}


}
