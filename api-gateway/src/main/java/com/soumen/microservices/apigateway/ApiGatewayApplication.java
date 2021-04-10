package com.soumen.microservices.apigateway;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@FeignClient("CARS-SERVICE")
interface CarClient {
    @GetMapping("/cars")
    @CrossOrigin
    CollectionModel<Car> readCars();
}

@SpringBootApplication
@EnableFeignClients
@EnableCircuitBreaker
@EnableDiscoveryClient
@EnableZuulProxy
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Car {
    private String name;
}

@RestController
@RequiredArgsConstructor
class CoolCarController {
    private final CarClient carClient;

    private Collection<Car> fallback() {
        return List.of(new Car("NOT FOUND"));
    }

    @GetMapping("/cool-cars")
    @CrossOrigin
    @HystrixCommand(fallbackMethod = "fallback")
    public Collection<Car> goodCars() {
        return carClient.readCars()
                .getContent()
                .stream()
                .filter(this::isCool)
                .collect(Collectors.toList());
    }

    private boolean isCool(Car car) {
        return !car.getName().equals("AMC Gremlin") &&
                !car.getName().equals("Triumph Stag") &&
                !car.getName().equals("Ford Pinto") &&
                !car.getName().equals("Yugo GV");
    }

}
