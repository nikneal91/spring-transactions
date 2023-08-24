package com.example.springtransactions;

import java.sql.Clob;
import java.sql.SQLException;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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

	@GetMapping("/02a")
	public String index02a() throws Exception {
		service.createProductCheckException();
		return "Hello world";
	}
	
	@GetMapping("/02b")
	public String index02b() throws Exception {
		service.createProductRollback();
		return "Hello world";
	}

	
	@GetMapping("/03")
	public String index03() throws Exception {
		service.createProductHandledRuntime();
		return "Hello world";
	}

	@GetMapping("/test")
	public String test() throws Exception {
		service.testObject();
		service.testGetObject();
		return "Hello world";
	}

}


//02 after adding transactional rollbacks
@Service
class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private EntRepository entRepository;

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
	
	/**
	 * 02a
	 * Service with checked exception doesnot rollback db
	 * @throws Exception
	 */
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
	
	/**
	 * 02b
	 * for checked exception it is necessary to add rollbackFor in Transactional annotation
	 * @throws Exception
	 */
	@Transactional( rollbackFor = SQLException.class)
	public void createProductRollback() throws Exception{  
	    System.out.println("------ createProduct ------");
	    Product prod = new Product();
	    prod.setDescription("This is an example with checked exception and transactional annotation with rollbackFor.");
	    prod.setPrice(10d);
	    prod.setTitle("Example 2b Product");
	    productRepository.save(prod);
	    System.out.println("Example 2b inserted.");
	    throw new SQLException();
	}
	
	/**
	 * Handled Runtime Exception
	 */
	@Transactional
	public void createProductHandledRuntime() {
	    try {
	        System.out.println("------ createProduct ------");
	        Product prod = new Product();
	        prod.setDescription("This is an example with runtime exception, transactional annotation and try catch.");
	        prod.setPrice(10d);
	        prod.setTitle("Example 3 Product");
	        productRepository.save(prod);
	        System.out.println("Example 3 Product inserted.");
	        throw new RuntimeException();
	    }catch (Exception e){
	        System.out.println("Here we catch the exception.");
	    }
	}

	@Transactional
	public void testObject() {
		try {
			System.out.println("------ Test En ------");
			Ent ent = new Ent();
			entRepository.save(ent);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Transactional
	public void testGetObject() {
		try {
			System.out.println("------ Test Get En ------");
			Ent ent = entRepository.findById(1l).get();
			En nn = new En(2l,"nikhil","a large character object");
			ent.setObj(nn);
			nn.setName("vipin");
		}catch (Exception e){
			e.printStackTrace();
		}
	}


}


@Repository
interface EntRepository extends JpaRepository<Ent, Long> {

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


@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
class En {
	private Long enid;
	private String name;

	@Lob
	private String warranty;
}

@Data
@NoArgsConstructor
@Entity
class Ent {
	@Id
	@GeneratedValue
	private Long id;

	@Embedded
	private En obj;
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
