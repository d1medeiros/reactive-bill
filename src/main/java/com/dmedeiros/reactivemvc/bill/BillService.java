package com.dmedeiros.reactivemvc.bill;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
                .filter(this::validate)
                .map(this::copyProperties)
                .orElseThrow(RuntimeException::new);
        return findById(bill.getId())
                .map(b -> copyPropertiesWithFieldToIgnore(bill, b))
                .flatMap(this::create);

    }

    private Bill copyPropertiesWithFieldToIgnore(Bill bill, Bill b) {
        BeanUtils.copyProperties(bill, b, propertiesToIgnore(bill));
        return b;
    }

    private String[] propertiesToIgnore(Bill bill) {
       return Stream.of(getFieldName(Bill.class))
               .filter(s -> !containsInClassFields(s, BillUpdate.class))
                .toArray(String[]::new);
    }

    private boolean containsInClassFields(String s, Class<?> c) {
        return List.of(getFieldName(c)).contains(s);
    }

    private String[] getFieldName(Class<?> c) {
        Field[] declaredFields = c.getDeclaredFields();
        return Stream.of(declaredFields)
                .map(Field::getName)
                .toArray(String[]::new);
    }

    private Bill copyProperties(BillUpdate billUpdate) {
        Bill bill = new Bill();
        BeanUtils.copyProperties(billUpdate, bill);
        return bill;
    }

    private boolean validate(BillUpdate billUpdate) {
        return Optional.ofNullable(billUpdate)
                .map(BillUpdate::getId)
                .isPresent();
    }
}
