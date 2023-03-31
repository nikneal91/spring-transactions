package com.example.springtransactions;

import java.sql.SQLException;

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
 	
	@GetMapping("/01")
	public String index01() {
		service.createProduct();
		return "Hello world";
	}

	@GetMapping("/02")
	public String index02() throws Exception {
		service.createProductCheckException();
		return "Hello world";
	}

	

}


//02 after adding transactional rollbacks
@Service
class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	/**
	 * 01 service without transactional doesnot rollback
	 */
	@Transactional
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
	
	
	@Transactional
	public void createProductCheckException() throws Exception{  
	    System.out.println("------ createProduct ------");
	    Product prod = new Product();
	    prod.setDescription("This is an example with checked exception and transactional annotation.");
	    prod.setPrice(10.0);
	    prod.setTitle("Second Product");
	    productRepository.save(prod);
	    System.out.println("Second Product inserted.");
	    throw new SQLException();
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
