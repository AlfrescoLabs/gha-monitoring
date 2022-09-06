package org.alfresco.badges;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BadgeGeneratorApplicationTests {

	@Autowired
	private BadgeGeneratorApplication app;

	@Test
	void contextLoads() {
		assertNotNull(app);
	}

	@Test
	void doesNotThrow() {
		assertDoesNotThrow(() -> BadgeGeneratorApplication.main(new String[0]));
	}

}
