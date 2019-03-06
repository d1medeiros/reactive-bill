package com.dmedeiros.reactivemvc.bill;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;

@Component
public class BillHandler {

    private final BillService billService;

    public BillHandler(BillService billService) {
        this.billService = billService;
    }


    public Mono<ServerResponse> findById(ServerRequest request) {
       return this.billService.findById(request.pathVariable("id"))
                .flatMap(BillHandler::defaultResponse)
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return defaultResponse(this.billService.findAll());
    }

    public Mono<ServerResponse> findByMonth(ServerRequest request) {
        Integer month = Optional.ofNullable(request.pathVariable("month"))
                .map(Integer::valueOf)
                .orElseThrow(RuntimeException::new);
        return defaultResponse(this.billService.findByMonth(month));
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Bill.class)
                .flatMap(billService::create)
                .flatMap(BillHandler::defaultCreate);
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        return request.bodyToMono(BillUpdate.class)
                .flatMap(billService::update)
                .flatMap(bill -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> remove(ServerRequest request) {
        String id = request.pathVariable("id");
        this.billService.remove(id);
        return ServerResponse.noContent().build();
    }


    private static Mono<ServerResponse> defaultCreate(Bill bill) {
        return ServerResponse.created(URI.create("/bill/".concat(bill.getId()))).build();
    }

    private static Mono<ServerResponse> defaultResponse(Bill mono) {
        return ServerResponse.ok().body(Mono.just(mono), Bill.class);
    }

    private static Mono<ServerResponse> defaultResponse(Mono<Bill> mono) {
        return ServerResponse.ok().body(mono, Bill.class);
    }

    private static Mono<ServerResponse> defaultResponse(Flux<Bill> mono) {
        return ServerResponse.ok().body(mono, Bill.class);
    }
}
