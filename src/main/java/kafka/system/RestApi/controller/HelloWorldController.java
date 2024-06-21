package kafka.system.RestApi.controller;

import kafka.system.RestApi.model.Greeting;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {

    private static final String template = "Hello,  %s";
    private final AtomicLong mockId = new AtomicLong();
    @GetMapping
    private Greeting returnHello(@RequestParam(value = "name", defaultValue = "world") String name){
        return new Greeting(mockId.incrementAndGet(), String.format(template, name));
    }
}
