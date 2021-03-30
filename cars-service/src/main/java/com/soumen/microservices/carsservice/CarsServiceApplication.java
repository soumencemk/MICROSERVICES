package com.soumen.microservices.carsservice;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.stream.Stream;

@RepositoryRestResource
interface CarRepo extends JpaRepository<Car, Long> {
}

@SpringBootApplication
@EnableDiscoveryClient
public class CarsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarsServiceApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(CarRepo carRepo) {
        return args -> {
            Stream.of("Ferrari", "Jaguar", "Porsche", "Lamborghini", "Bugatti",
                    "AMC Gremlin", "Triumph Stag", "Ford Pinto", "Yugo GV")
                    .forEach(name ->{
                        carRepo.save(new Car(name));
                    });
        };
    }
}

@Entity
@Data
@NoArgsConstructor
class Car {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    public Car(String name) {
        this.name = name;
    }
}
