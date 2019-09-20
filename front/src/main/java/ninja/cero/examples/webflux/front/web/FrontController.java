package ninja.cero.examples.webflux.front.web;

import ninja.cero.examples.webflux.front.Student;
import ninja.cero.examples.webflux.front.Score;
import ninja.cero.examples.webflux.front.StudentScore;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class FrontController {
    WebClient webClient;
    RestTemplate restTemplate;

    public FrontController(WebClient webClient, RestTemplate restTemplate) {
        this.webClient = webClient;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/rest")
    public List<StudentScore> rest() {
        long start = System.currentTimeMillis();

        ParameterizedTypeReference<List<Student>> studentType =
                new ParameterizedTypeReference<>() {
                };
        ParameterizedTypeReference<List<Score>> scoreType =
                new ParameterizedTypeReference<>() {
                };

        List<Student> students = restTemplate
                .exchange("http://localhost:8081/students/list", HttpMethod.GET, null, studentType)
                .getBody();

        List<StudentScore> studentScores = students.stream()
                .map(student -> {
                    String url = "http://localhost:8081/scores/" + student.id;
                    List<Score> scores = restTemplate
                            .exchange(url, HttpMethod.GET, null, scoreType)
                            .getBody();
                    return new StudentScore(student, scores);
                })
                .collect(Collectors.toList());

        System.out.println("rest: " + (System.currentTimeMillis() - start));

        return studentScores;
    }

    @GetMapping(value = "/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StudentScore> getAsFlux() {
        long start = System.currentTimeMillis();

        Flux<Student> students = webClient.get()
                .uri("localhost:8081/students/flux")
                .retrieve()
                .bodyToFlux(Student.class);

        Flux<StudentScore> studentScore = students.flatMap(student ->
                webClient.get()
                        .uri("localhost:8081/scores/" + student.id)
                        .retrieve()
                        .bodyToFlux(Score.class)
                        .collectList()
                        .map(scores -> new StudentScore(student, scores)));

        return studentScore.doOnComplete(() ->
                System.out.println("flux: " + (System.currentTimeMillis() - start)));
    }

    @GetMapping("/map")
    public Mono<Map<Student, List<Score>>> getAsMap() {
        Flux<Student> students = webClient.get()
                .uri("localhost:8081/students/flux")
                .retrieve()
                .bodyToFlux(Student.class);

        Mono<Map<Student, List<Score>>> map = students.flatMap(student ->
                webClient.get()
                        .uri("localhost:8081/scores/" + student.id)
                        .retrieve()
                        .bodyToFlux(Score.class)
                        .collectList()
                        .map(scores -> Tuples.of(student, scores)))
                .collectMap(Tuple2::getT1, Tuple2::getT2);

        return map;
    }

    @GetMapping("/rest2")
    public List<StudentScore> rest2() {
        long start = System.currentTimeMillis();

        ParameterizedTypeReference<List<Student>> studentType =
                new ParameterizedTypeReference<>() {
                };
        ParameterizedTypeReference<List<Score>> scoreType =
                new ParameterizedTypeReference<>() {
                };

        List<Student> students = restTemplate
                .exchange("http://localhost:8081/students/list", HttpMethod.GET, null, studentType)
                .getBody();

        String url = "http://localhost:8081/scores/" + ids(students);
        List<Score> scores = restTemplate
                .exchange(url, HttpMethod.GET, null, scoreType)
                .getBody();
        Map<Integer, List<Score>> scoreMap = scores.stream()
                .collect(Collectors.groupingBy(s -> s.id));

        List<StudentScore> studentScores = students.stream()
                .map(student -> new StudentScore(student, scoreMap.get(student.id)))
                .collect(Collectors.toList());

        System.out.println("rest2: " + (System.currentTimeMillis() - start));

        return studentScores;
    }

    @GetMapping(value = "/flux2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<List<StudentScore>> flux2() {
        long start = System.currentTimeMillis();

        Mono<List<Student>> students = webClient.get()
                .uri("localhost:8081/students/flux")
                .retrieve()
                .bodyToFlux(Student.class)
                .collectList();

        Mono<List<StudentScore>> studentScore = students.flatMap(studentList -> webClient.get()
                .uri("localhost:8081/scores/" + ids(studentList))
                .retrieve()
                .bodyToFlux(Score.class)
                .collectList()
                .map(scores -> {
                    Map<Integer, List<Score>> scoreMap = scores.stream()
                            .collect(Collectors.groupingBy(s -> s.id));
                    return studentList.stream()
                            .map(s -> new StudentScore(s, scoreMap.get(s.id)))
                            .collect(Collectors.toList());
                }));

        return studentScore
                .doOnSuccess(x -> System.out.println("flux2: " + (System.currentTimeMillis() - start)));
    }

    @GetMapping(value = "/flux3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StudentScore> flux3() {
        long start = System.currentTimeMillis();

        Flux<Student> students = webClient.get()
                .uri("localhost:8081/students/flux")
                .retrieve()
                .bodyToFlux(Student.class)
                .cache();

        Flux<StudentScore> studentScore = webClient.post()
                .uri("localhost:8081/scores/flux")
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(students.map(s -> s.id), Integer.class)
                .retrieve()
                .bodyToFlux(Score.class)
                .collectList()
                .map(scores -> scores.stream().collect(Collectors.groupingBy(s -> s.id)))
                .flatMapMany(scoreMap -> students.map(student -> new StudentScore(student, scoreMap.get(student.id))));

        return studentScore.doOnComplete(() ->
                System.out.println("flux3: " + (System.currentTimeMillis() - start)));
    }

    private String ids(List<Student> students) {
        return students.stream()
                .map(s -> String.valueOf(s.id))
                .collect(Collectors.joining(","));
    }
}
