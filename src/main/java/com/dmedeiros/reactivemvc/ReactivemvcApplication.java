package com.dmedeiros.reactivemvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;

@SpringBootApplication
public class ReactivemvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactivemvcApplication.class, args);
	}

}

class T{
	public static void main(String[] args) {
		System.out.println(new Random().nextInt(2)+1);
		System.out.println(new Random().nextInt(2)+1);
		System.out.println(new Random().nextInt(2)+1);
		System.out.println(new Random().nextInt(2)+1);
		System.out.println(new Random().nextInt(2)+1);
		System.out.println(new Random().nextInt(2)+1);
		System.out.println(new Random().nextInt(2)+1);
		System.out.println(new Random().nextInt(2)+1);
	}
}