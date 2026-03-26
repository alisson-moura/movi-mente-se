package com.amdev.movimente_se;

import org.junit.jupiter.api.Test;

class HealthCheckControllerE2ETest extends BaseE2ETest {
	@Test
	public void getStatus() {
		client.get().uri("/status")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.status").isEqualTo("OK");
	}
}
