package it.unibo.clientGui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import pojos.ClientAttributes;

@Configuration
public class SessionConfig {
	
	@Bean
	@Scope(value=WebApplicationContext.SCOPE_SESSION, proxyMode=ScopedProxyMode.TARGET_CLASS)
	public ClientAttributes attributes() {
		return new ClientAttributes();
	}

}
