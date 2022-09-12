package com.jsolution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SpringBootApplication
public class SpringReactorDemoApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringReactorDemoApplication.class);
    private static List<String> dishes = new ArrayList<>();

    public void createMono(){
        Mono<String> m1 = Mono.just("Hello Coders!");
        Mono<Integer> m2 = Mono.just(5);
        m1.subscribe(x-> log.info("Data 0: "+x));
        m2.subscribe(x-> log.info("Data 1: "+5));
        Mono.just(5).subscribe(x-> log.info("Data 2: "+x));
    }

    public void createFlux(){
        Flux<String> fx1 = Flux
                .fromIterable(dishes);
        fx1.subscribe(x-> log.info("Dish: "+x));
        fx1.collectList().subscribe(list -> log.info("Dish: "+list));
    }

    public void doOnNextMethod(){ //Consolidación de un Flux en un Mono
        Flux<String> fx1 = Flux
                .fromIterable(dishes);
        fx1.doOnNext(x -> log.info(x)).subscribe();
    }

    public void mapMethod(){ //Consolidación de un Flux en un Mono
        Flux<String> fx1 = Flux.fromIterable(dishes);
        fx1.map(x -> x.toUpperCase()).subscribe(log::info);
        fx1.map(String::toUpperCase).subscribe(log::info);
    }

    public void flatMapMethod(){ //flatMap para extraer información interna del Mono
        Mono.just("Jiliar").map(x -> 31)
                .subscribe(e->log.info("Data: "+ e));
        Mono.just("Jiliar").map(x -> Mono.just(31))
                .subscribe(e->log.info("Data: "+ e));
        Mono.just("Jiliar").flatMap(x -> Mono.just(31))
                .subscribe(e->log.info("Data: "+ e));
    }

    public void rangeMethod(){
        Flux<Integer> fx1 = Flux.range(0,10);
        fx1.map(x-> x + 1).subscribe(x -> log.info(x.toString()));
    }

    public void delayElementMethod() throws InterruptedException{ //Delay en la transmisión de elementos
          Flux.range(0,10)
                .delayElements(Duration.ofSeconds(2))
                .doOnNext(e->log.info("Data: "+ e))
                .subscribe();

        Thread.sleep(20000);
    }

    public void zipWithMethod(){ //Contraste entre dos datos uno nuevo y uno activo,
        //IMPORTANTE: las dos listas deben tener la misma cantidad de elementos
        List<String> clients = new ArrayList<>();
        clients.add("Client 1");
        clients.add("Client 2");
        Flux<String> fx1 = Flux.fromIterable(dishes);
        Flux<String> fx2 = Flux.fromIterable(clients);
        fx1.zipWith(fx2, (d, c) -> d+"-"+c)
                .subscribe(log::info);
    }

    public void mergeMethod(){
        List<String> clients = new ArrayList<>();
        clients.add("Client 1");
        clients.add("Client 2");
        clients.add("Client 3");

        Flux<String> fx1 = Flux.fromIterable(dishes);
        Flux<String> fx2 = Flux.fromIterable(clients);

        Flux.merge(fx1, fx2)
                .subscribe(log::info);
    }

    public void filterMethod(){
        //Case 1:
        Flux<String> fx1 = Flux.fromIterable(dishes);
        fx1.filter(x -> x.startsWith("B"))
                .subscribe(log::info);
        //Case 2:
        Predicate<String> predicate = p -> p.startsWith("B");
        fx1.filter(predicate)
                .subscribe(log::info);
    }

    public void takeLastMethod(){ //Obtener los dos ultimos
        Flux<String> fx1 = Flux.fromIterable(dishes);
        fx1.takeLast(2)
                .subscribe(log::info);
    }

    public void takeMethod(){ //Obtener los dos primeros
        Flux<String> fx1 = Flux.fromIterable(dishes);
        fx1.take(2)
                .subscribe(log::info);
    }

    public void defaultIfEmptyMethod(){ //Genera metodo EMPTY LIST cuando el FLUX o el MONO esta vacio.
        dishes = new ArrayList<>();
        Flux<String> fx1 = Flux.fromIterable(dishes);
        fx1.map(e -> "P: "+e)
                .defaultIfEmpty("EMPTY LIST")
                .subscribe(log::info);
    }

    public void errorMapMethod(){ //Gestión de excepciones en Flux
        Flux<String> fx1 = Flux.fromIterable(dishes);
        fx1.doOnNext(d ->{
            throw new ArithmeticException("BAD NUMBER");
        }).onErrorMap(ex -> new Exception(ex.getMessage()))
                .subscribe(log::info);
    }

    public void onErrorReturnMethod(){ //Enviar mensaje cuando se obtiene excepción
        Flux<String> fx1 = Flux.fromIterable(dishes);
        fx1.doOnNext(d ->{
                    throw new ArithmeticException("BAD NUMBER");
                }).onErrorReturn("ERROR, PLEASE REBOOT")
                .subscribe(log::info);
    }

    public void retryMethod(){ //Enviar mensaje cuando se obtiene excepción
        Flux<String> fx1 = Flux.fromIterable(dishes);
        fx1.doOnNext(d ->{
                    log.info("conectando...");
                    throw new ArithmeticException("BAD NUMBER");
                })
                .delayElements(Duration.ofSeconds(1))
                .retry(3)
                .onErrorReturn("ERROR, PLEASE REBOOT")
                .subscribe(log::info);
    }

    public static void main(String[] args) {
        dishes.add("Arroz con pollo");
        dishes.add("Bandeja paisa");
        dishes.add("Ceviche");
        SpringApplication.run(SpringReactorDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        createMono();
        createFlux();
        doOnNextMethod();
        mapMethod();
        flatMapMethod();
        rangeMethod();
        delayElementMethod();
        mergeMethod();
        filterMethod();
        zipWithMethod();
        takeLastMethod();
        takeMethod();
        defaultIfEmptyMethod();
        errorMapMethod();
        onErrorReturnMethod();
    }
}
