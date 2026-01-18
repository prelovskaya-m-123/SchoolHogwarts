package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.LongStream;

@RestController
@RequestMapping("/math")
public class MathController {


    @GetMapping("/sum-parallel")
    public ResponseEntity<Long> getSumParallel() {
        long n = 1_000_000L;

        long sum = LongStream.rangeClosed(1, n)
                .parallel()
                .sum();


        return ResponseEntity.ok(sum);
    }
}

