package com.christiancanari.pupe.advance.ticket.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class AdvanceTicketApplicationTests {

	@Test
	void shouldInstantiateApplicationClass() {
		assertNotNull(new PupeAdvanceTicketServiceApplication());
	}

}
