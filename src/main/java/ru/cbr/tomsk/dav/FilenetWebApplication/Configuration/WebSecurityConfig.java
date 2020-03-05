package ru.cbr.tomsk.dav.FilenetWebApplication.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	// Create 2 users for demo
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.inMemoryAuthentication()
				.withUser("user").password("{noop}password").roles("USER")
				.and()
				.withUser("admin").password("{noop}password").roles("USER", "ADMIN");

	}

	// Secure the endpoins with HTTP Basic authentication
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				//HTTP Basic authentication
				.httpBasic()
				.and()
					.authorizeRequests()
					.antMatchers(HttpMethod.GET, "/connect").hasRole("ADMIN")
					.antMatchers(HttpMethod.GET,"/domain/**").hasAnyRole("USER")

//					.antMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
//					.antMatchers(HttpMethod.PUT, "/books/**").hasRole("ADMIN")
//					.antMatchers(HttpMethod.PATCH, "/books/**").hasRole("ADMIN")
//					.antMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")
				.and()
					.csrf().disable()
					.formLogin().disable();
	}


}
