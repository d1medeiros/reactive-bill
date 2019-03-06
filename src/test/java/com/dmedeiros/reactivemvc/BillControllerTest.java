package com.dmedeiros.reactivemvc;

import com.dmedeiros.reactivemvc.bill.Bill;
import com.dmedeiros.reactivemvc.bill.BillRepository;
import com.dmedeiros.reactivemvc.bill.BillService;
import com.dmedeiros.reactivemvc.bill.BillUpdate;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BillControllerTest {

    @Autowired ApplicationContext context;
    @Autowired private BillRepository billRepository;
    @Autowired private BillService billService;
    WebTestClient rest;

    private Bill getBill(String name) {
        return Bill.builder().dateCreated(LocalDateTime.now())
                .lastUpdate(LocalDateTime.now())
                .name(name)
                .payday(LocalDateTime.now())
                .price(new Random().nextDouble() * 100)
                .build();
    }
    private BillUpdate getBillUpdate(Bill billMono) {

        String coxinhaDeGalinha = "coxinha de galinha";
        return BillUpdate.builder()
                .id(billMono.getId())
                .name(coxinhaDeGalinha)
                .payday(LocalDateTime.now().withMonth(6))
                .price(1.0)
                .build();
    }
    private void createThreeBillFluxDateRandomWithTwoJanAndOneFeb() {
        createBillFluxDateRandom(3).subscribe();
    }
    private Flux<Bill> createBillFluxDateRandom(int i) {
        return Flux.range(1, i)
                .map(integer -> {
                            LocalDateTime dateCreated = LocalDateTime.now()
                                    .withMonth(getRandomMonthJanOrFeb(integer))
                                    .withHour(0)
                                    .withMinute(0)
                                    .withSecond(0)
                                    .withNano(0);
                            return Bill.builder()
                                    .dateCreated(dateCreated)
                                    .lastUpdate(LocalDateTime.now())
                                    .name("Conta ".concat(integer.toString()))
                                    .payday(LocalDateTime.now())
                                    .price(new Random().nextDouble() * 100)
                                    .build();
                        }
                ).flatMap(billService::create);
    }
    private int getRandomMonthJanOrFeb(Integer integer) {
        if (integer % 2 == 0) {
            System.out.println("random mes 1");
            return 1;
        } else {
            System.out.println("random mes 2");
            return 2;
        }
    }

    @Before
    public void setup() {
        this.rest = WebTestClient
                .bindToApplicationContext(this.context)
                .configureClient()
                .build();
        this.billRepository.deleteAll().block();
    }

    @Test
    public void create() {
        Bill contaA =  getBill("Conta A");
        this.rest
                .post()
                .uri("/bill")
                .body(Mono.just(contaA), Bill.class)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    public void update() {
        Bill contaA =  getBill("Conta A");
        Bill billMono = Mono.just(contaA).log().flatMap(this.billService::create).block();
        BillUpdate billUpdate = getBillUpdate(billMono);

        this.rest
                .put()
                .uri("/bill")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(billUpdate), BillUpdate.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void findByMonth() {
        createThreeBillFluxDateRandomWithTwoJanAndOneFeb();
        int monthToFind = 2;
        rest.get()
                .uri("/bill/month/" + monthToFind)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Bill.class)
                .consumeWith(listEntityExchangeResult -> {
                    List<Bill> responseBody = listEntityExchangeResult.getResponseBody();
                    responseBody.forEach(bill -> {
                        int value = bill.getDateCreated().getMonth().getValue();
                        MatcherAssert.assertThat(value, Matchers.is(monthToFind));
                    });
                });
    }

    @Test
    public void findById() {
        String name = "Conta B";
        Bill contaA =  getBill(name);
        Bill billMono = Mono.just(contaA).log().flatMap(this.billService::create).block();

        rest.get()
                .uri("/bill/" + billMono.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().jsonPath("$.name").isEqualTo(name);
    }

    @Test
    public void findAll() {
        Bill a = Mono.just(getBill("Conta A")).log().flatMap(this.billService::create).block();
        Bill b = Mono.just(getBill("Conta B")).log().flatMap(this.billService::create).block();
        Bill c = Mono.just(getBill("Conta C")).log().flatMap(this.billService::create).block();
        List<String> collect = Stream.of(a, b, c).map(Bill::getId).collect(Collectors.toList());

        rest.get()
                .uri("/bill")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Bill.class)
                .consumeWith(listEntityExchangeResult -> {
                    List<Bill> responseBody = listEntityExchangeResult.getResponseBody();
                    responseBody.forEach(bill -> {
                        MatcherAssert.assertThat(bill.getId(), Matchers.isIn(collect));
                    });
                });
    }

    @Test
    public void remove() {
        String name = "Conta B";
        Bill contaA =  getBill(name);
        Bill billMono = Mono.just(contaA).log().flatMap(this.billService::create).block();

        rest.delete()
                .uri("/bill/" + billMono.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectStatus().isNoContent();
    }
}