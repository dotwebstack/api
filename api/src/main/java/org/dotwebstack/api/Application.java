package org.dotwebstack.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@Configuration
@ComponentScan(basePackages = { "org.dotwebstack.api", "org.dotwebstack.data" })
@EnableAutoConfiguration
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

<<<<<<< HEAD
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(Application.class);
  }

=======
        public static void main(String[] args) {
                SpringApplication.run(Application.class, args);
        }
>>>>>>> ee09a9b5e7b4897607cf5db00d3c880089cbb63d

        @Override
        protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
                return application.sources(Application.class);
        }
}