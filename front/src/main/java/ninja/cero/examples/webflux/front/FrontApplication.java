package ninja.cero.examples.webflux.front;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class FrontApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrontApplication.class, args);
	}

	@Bean
	WebClient webClient() {
		return WebClient.builder().build();
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
