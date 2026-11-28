package com.tl.spring_boot1;

import com.tl.spring_boot1.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfig.class)
@SpringBootTest
class SpringBoot1ApplicationTests {

	@Test
	void contextLoads() {
	}

}
