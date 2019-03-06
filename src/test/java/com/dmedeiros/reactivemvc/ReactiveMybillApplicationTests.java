package com.dmedeiros.reactivemvc;

import com.dmedeiros.reactivemvc.bill.Bill;
import com.dmedeiros.reactivemvc.bill.BillRepository;
import com.dmedeiros.reactivemvc.bill.BillService;
import com.dmedeiros.reactivemvc.bill.BillUpdate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReactiveMybillApplicationTests {

	@Autowired
	private BillService billService;
	@Autowired
	private BillRepository billRepository;
	private static int MAPCOUNT = 0;

	private Flux<Integer> getMonthFromFluxBill(Flux<Bill> byMonth) {
		return byMonth
				.map(bill -> {
					System.out.println(bill.getDateCreated().getMonth());
					return bill;
				})
				.map(Bill::getDateCreated).map(LocalDateTime::getMonth).map(Month::getValue);
	}
	private Flux<Bill> createBillFlux(int i) {
		return Flux.range(1, i)
				.map(integer -> Bill.builder()
						.dateCreated(LocalDateTime.now())
						.lastUpdate(LocalDateTime.now())
						.name("Conta ".concat(integer.toString()))
						.payday(LocalDateTime.now())
						.price(new Random().nextDouble() * 100)
						.build()
				).map(bill -> {
					System.out.println("Criando no teste "+bill);
					return bill;
				})
				.flatMap(billService::create);
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
	private void imp(Object... o) {
		String collect = Stream.of(o).map(Object::toString).collect(Collectors.joining(" \n "));
		System.out.println(collect);
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

	@Before
	public void init() {
		this.billRepository.deleteAll().block();
	}

	@Test
	public void initialTest() {
		String[] strings = {"diego", "marina"};

		Flux<String> just = Flux.just(strings);
		StepVerifier.create(just)
				.expectNext(strings)
				.expectComplete()
				.verify();

		StepVerifier.create(just)
				.expectNext("diego", "marina")
				.expectComplete()
				.verify();

		StepVerifier.create(just)
				.expectNext("diego")
				.expectNext("marina")
				.expectComplete()
				.verify();

	}

	@Test
	public void saveAll() {
		int count = 10;
		Flux<Bill> billFlux = createBillFlux(count);
		StepVerifier.create(billFlux)
				.expectNextCount(count)
				.expectComplete()
				.verify();
	}

	@Test
	public void findAllByMonth() {
		createThreeBillFluxDateRandomWithTwoJanAndOneFeb();
		int monthToFind = 2;
		Flux<Bill> byMonth = this.billService.findByMonth(monthToFind);
		StepVerifier.create(getMonthFromFluxBill(byMonth))
				.expectNext(monthToFind, monthToFind)
				.expectComplete()
				.verify();

	}

	@Test
	public void updateABill() {
		System.out.println("\n\n\n\n1- iniciando update bill");
		Bill contaA = Bill.builder().dateCreated(LocalDateTime.now())
				.lastUpdate(LocalDateTime.now())
				.name("Conta A")
				.payday(LocalDateTime.now())
				.price(new Random().nextDouble() * 100)
				.build();
		System.out.println("aind NAO passou do block");
		Bill billMono = Mono.just(contaA)
				.log()
				.flatMap(this.billService::create)
				.block();

		System.out.println("passou do block "+billMono.getId());
		BillUpdate billUpdate = getBillUpdate(billMono);

		this.billService.update(billUpdate);

	}


}

