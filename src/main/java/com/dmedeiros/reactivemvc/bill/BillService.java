package com.dmedeiros.reactivemvc.bill;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.*;
import java.util.Optional;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final ApplicationEventPublisher publisher;

    public BillService(BillRepository billRepository, ApplicationEventPublisher publisher) {
        this.billRepository = billRepository;
        this.publisher = publisher;
    }

    public Mono<Bill> findById(String id) {
        return this.billRepository.findById(id);
    }

    public Flux<Bill> findByMonth(int month) {
        LocalDate min = LocalDate.of(Year.now().getValue(), Month.of(month), 1);
        LocalDate max = LocalDate.of(Year.now().getValue(), Month.of(month), Month.of(month).minLength());
        LocalDateTime minMouth = LocalDateTime.of(min, LocalTime.MIN);
        LocalDateTime maxMouth = LocalDateTime.of(max, LocalTime.MAX);
        return this.billRepository.findByDateCreatedBetween(minMouth, maxMouth);
    }

    public Flux<Bill> findAll() {
        return this.billRepository.findAll();
    }

    public Mono<Bill> create(Bill bill) {
        return this.billRepository.save(bill);
    }

    public Mono<Bill> update(BillUpdate billUpdate) {
        Bill bill = Optional.ofNullable(billUpdate)
                .map(this::validate)
                .map(this::copyProperties)
                .orElseThrow(RuntimeException::new);
        return this.billRepository.save(bill);
    }

    private Bill copyProperties(BillUpdate billUpdate) {
        Bill bill = new Bill();
        BeanUtils.copyProperties(billUpdate, bill);
        return bill;
    }

    private BillUpdate validate(BillUpdate billUpdate) {
        return billUpdate;
    }
}
