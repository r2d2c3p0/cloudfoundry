package org.r2d2c3p0.pivotal.cloudcache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CloudCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                CloudCacheApplication.class, args
        );
    }
}