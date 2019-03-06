package com.dmedeiros.reactivemvc.bill;

import com.dmedeiros.reactivemvc.util.Util;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.*;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Log
public class BillService {

    private final BillRepository billRepository;

    @Autowired
    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public Mono<Bill> findById(String id) {
        log.info("buscando contas por id");
        return this.billRepository.findById(id);
    }

    public Flux<Bill> findByMonth(int month) {
        log.info("buscando contas por mes");
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
        log.info("criando uma nova conta");
        return this.billRepository.save(bill);
    }

    public Mono<Bill> update(BillUpdate billUpdate) {
        System.out.println("atualizando uma conta");
        Bill billFromUpdate = mapBillUpdate(billUpdate);
        return findById(billFromUpdate.getId())
                .map(bill -> copyPropertiesWithFieldToIgnore(billFromUpdate, bill))
                .flatMap(this::create);
    }

    public Mono<Void> remove(String id) {
        return this.billRepository.deleteById(id);
    }


    private Bill mapBillUpdate(BillUpdate billUpdate) {
        return Optional.ofNullable(billUpdate)
                    .filter(this::validate)
                    .map(this::copyProperties)
                    .orElseThrow(RuntimeException::new);
    }

    private Bill copyPropertiesWithFieldToIgnore(Bill billFromUpdate, Bill bill) {
        BeanUtils.copyProperties(billFromUpdate, bill, propertiesToIgnore());
        return bill;
    }

    private String[] propertiesToIgnore() {
       return Stream.of(Util.getFieldName(Bill.class))
               .filter(s -> !Util.containsInClassFields(s, BillUpdate.class))
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
