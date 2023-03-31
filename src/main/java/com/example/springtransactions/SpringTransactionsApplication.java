package com.example.springtransactions;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@RestController
class HelloController {

	@Autowired
	private ProductService service;
 	
	@GetMapping("/")
	public String index() {
		service.createProduct();
		return "Hello world";
	}
}


@Service
class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	public void createProduct()  {
		System.out.println("------ createProduct ------");
		Product prod = new Product();
		prod.setDescription("This is an example with runtime exception but no rollback.");
		prod.setPrice(10.0);
		prod.setTitle("First Product");
		productRepository.save(prod);
		System.out.println("First Product inserted.");
		throw new RuntimeException();
	}
	
	
}



@Repository
interface ProductRepository extends JpaRepository<Product, Long> {
	
}

@Data
@Entity
class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	private String title;
	private String description;
	private Double price;

}

@SpringBootApplication
@EnableJpaRepositories
public class SpringTransactionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringTransactionsApplication.class, args);
	}

}

@Component
class MyBean implements CommandLineRunner {

	
	

	public void run(String... args) throws RuntimeException {
		System.out.println("do something here");
		
	}
}
