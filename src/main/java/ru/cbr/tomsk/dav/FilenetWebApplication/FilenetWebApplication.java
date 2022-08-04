package ru.cbr.tomsk.dav.FilenetWebApplication;

import lombok.extern.log4j.Log4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Log4j
public class FilenetWebApplication {

	public static void main(String[] args) {
		 SpringApplication.run(FilenetWebApplication.class, args);
		 log.debug("FilenetWebApplication-0.0.2-SNAPSHOT.war");
	}
}
