package com.dmedeiros.reactivemvc;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;


@SpringBootApplication
public class ReactivemvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactivemvcApplication.class, args);
    }

}

@Log4j2
class T {
    public static void main(String[] args) {
        String[] strings = {"diego", "marina"};
        Flux.just(strings)
                .doOnNext(log::info)
                .doOnComplete(() -> log.info("onComplete"))
                .doOnTerminate(() -> log.info("doTerminate"))
                .subscribe();

    }
}