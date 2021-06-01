package com.example.counter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

@RestController
@RequestMapping("/counter")
public class CounterController {

    private final ConcurrentMap<String, LongAdder> counters = new ConcurrentHashMap<>();

    @PostMapping("/{name}")
    public ResponseEntity<Void> createCounter(@PathVariable String name) {
        LongAdder value = counters.putIfAbsent(name, new LongAdder());

        if (value != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{name}/inc")
    public ResponseEntity<Void> incrementCounter(@PathVariable String name) {
        LongAdder value = counters.computeIfPresent(name, (k, v) -> {
            v.increment();
            return v;
        });

        if (value == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{name}/value")
    public ResponseEntity<Long> getCounterValue(@PathVariable String name) {
        LongAdder value = counters.get(name);

        if (value == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(value.longValue());
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteCounter(@PathVariable String name) {
        if (!counters.containsKey(name)) {
            return ResponseEntity.notFound().build();
        }

        counters.computeIfPresent(name, (k, v) -> null);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sum")
    public ResponseEntity<Long> getCounterSum() {
        LongAdder sum = new LongAdder();

        counters.forEach((k, v) -> sum.add(v.longValue()));

        return ResponseEntity.ok(sum.longValue());
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> getCounterNames() {
        List<String> names = new ArrayList<>();

        counters.forEach((k, v) -> names.add(k));

        return ResponseEntity.ok(names);
    }
}
