package ninja.cero.examples.webflux.resource.student;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    @GetMapping(value = "/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Student> getAsFlux() {
        return Flux.interval(Duration.ofMillis(100))
                .map(i -> students[i.intValue()])
                .take(students.length);
    }

    @GetMapping(value = "/list")
    public List<Student> getAsList() throws InterruptedException {
        Thread.sleep(students.length * 100L);
        return Arrays.asList(students);
    }

    Student[] students = {
            new Student(1, "Muto"),
            new Student(2, "Miyoshi"),
            new Student(3, "Matsui"),
            new Student(4, "Nakamoto"),
            new Student(5, "Iida"),
            new Student(6, "Horiuchi"),
            new Student(7, "Sugisaki"),
            new Student(8, "Sato"),
            new Student(9, "Mizuno"),
            new Student(10, "Kikuchi"),
            new Student(11, "Notsu"),
            new Student(12, "Taguchi"),
            new Student(13, "Ooga"),
            new Student(14, "Sugimoto"),
            new Student(15, "Shirai"),
            new Student(16, "Isono"),
            new Student(17, "Kurosawa"),
            new Student(18, "Kurashima"),
            new Student(19, "Okada"),
            new Student(20, "Yamaide"),
            new Student(21, "Okazaki"),
            new Student(22, "Shintani"),
            new Student(23, "Aso"),
            new Student(24, "Hidaka"),
            new Student(25, "Yoshida"),
            new Student(26, "Fujihira"),
            new Student(27, "Aritomo"),
            new Student(28, "Mori"),
            new Student(29, "Shiratori"),
            new Student(30, "Nonaka"),
            new Student(31, "Tanaka"),
            new Student(32, "Yagi"),
            new Student(33, "Nozaki"),
    };
}
