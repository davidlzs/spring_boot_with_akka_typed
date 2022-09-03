package com.dliu.springbootwithakkatyped;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/counter")
public class CounterController {
    private final CounterService counterService;

    public CounterController(CounterService counterService) {
        this.counterService = counterService;
    }

    @GetMapping("/{id}")
    private Mono<Integer> getValue(@PathVariable String id) {
        return counterService.incrementByOne(id);
    }

    @PostMapping("/{id}")
    private void increment(@PathVariable String id) {
        counterService.getValue(id);
    }
}
