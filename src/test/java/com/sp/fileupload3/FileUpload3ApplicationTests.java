package com.sp.fileupload3;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootTest
class FileUpload3ApplicationTests {

	@Test
	void contextLoads() {
	}

	@TestConfiguration
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	static class SecurityConfig extends WebSecurityConfigurerAdapter {

	}

}
