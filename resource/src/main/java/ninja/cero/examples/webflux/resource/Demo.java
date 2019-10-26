package ninja.cero.examples.webflux.resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
public class Demo {
    @GetMapping("/demo")
    public Flux<String> demo() {
        return Flux.interval(Duration.ofMillis(300))
                .map(i -> i + " " + LocalDateTime.now())
                .take(10);
    }

    @GetMapping(value = "/resource", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> resource() {
        return Flux.interval(Duration.ofMillis(300))
                .map(i -> i + " " + LocalDateTime.now() + "\n")
                .take(10);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> client() {
        var webClient = WebClient.builder().build();

        return webClient.get()  // HTTP GET method
                .uri("localhost:8081/resource")   // to this URL
                .retrieve()                   // accesses to the server and retrieve
                .bodyToFlux(String.class);   // get response body as Flux
    }
}
