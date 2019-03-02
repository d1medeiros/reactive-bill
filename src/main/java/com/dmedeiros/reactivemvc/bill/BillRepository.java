package com.dmedeiros.reactivemvc.bill;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface BillRepository extends ReactiveCrudRepository<Bill, String> {

    Flux<Bill> findByDateCreatedBetween(LocalDateTime start, LocalDateTime end);
}
