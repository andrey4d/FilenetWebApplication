package ru.cbr.tomsk.dav.FilenetWebApplication;

import lombok.extern.log4j.Log4j;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Log4j
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		SpringApplicationBuilder sources = application.sources(FilenetWebApplication.class).bannerMode(Banner.Mode.OFF);
		log.debug("Servlet -- init");
		return sources;
	}

}
