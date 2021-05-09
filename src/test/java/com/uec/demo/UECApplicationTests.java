package com.uec.demo;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UECApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UECApplicationTests {

    @Test
    void test() {
        String a = "hello";
        assertEquals("hello", a);
    }
}
