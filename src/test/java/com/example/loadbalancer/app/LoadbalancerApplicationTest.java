package com.example.loadbalancer.app;

import com.example.loadbalancer.domain.model.Response;
import com.example.loadbalancer.domain.registry.InstanceRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// TODO: add more test cases
@SpringBootTest(webEnvironment = RANDOM_PORT)
class LoadbalancerApplicationTest {

	@LocalServerPort
	int port;

	@Autowired
	TestRestTemplate restTemplate;

	@Autowired
	InstanceRegistry instanceRegistry;

	@BeforeEach
	void beforeEach() {
		instanceRegistry.clear();
	}

	@Test
	void should_register_instance() {
		// given
		var instanceId = UUID.randomUUID().toString();

		// when
		var response = post(url("register", "id", instanceId));

		// then
		assertStatusOk(response);

		var activeInstances = instanceRegistry.listActive();
		assertThat(activeInstances).hasSize(1);

		var theInstance = activeInstances.stream().findAny().orElseThrow();
		assertThat(theInstance.instanceId().value()).isEqualTo(instanceId);
	}

	@Test
	void should_unregister_instance() {
		// given
		var instanceId = UUID.randomUUID().toString();
		var registerResponse = post(url("register", "id", instanceId));
		assertStatusOk(registerResponse);

		// when
		var response = post(url("unregister", "id", instanceId));

		// then
		assertStatusOk(response);

		var activeInstances = instanceRegistry.listActive();
		assertThat(activeInstances).isEmpty();
	}

	@Test
	void should_handle_requests_using_round_robin_strategy() {
		// given
		var instanceId1 = "instanceId1";
		var registerResponse = post(url("register", "id", instanceId1));
		assertStatusOk(registerResponse);

		var instanceId2 = "instanceId2";
		registerResponse = post(url("register", "id", instanceId2));
		assertStatusOk(registerResponse);

		// when
		var strategyResponse = post(url("strategy", "name", "ROUND_ROBIN"));
		assertStatusOk(strategyResponse);

		// and
		var request1 = UUID.randomUUID().toString();
		var handleResponse = post(url("handle"), request1);
		assertStatusOk(handleResponse);
		var response1 = handleResponse.getBody();

		// and
		var request2 = UUID.randomUUID().toString();
		handleResponse = post(url("handle"), request2);
		assertStatusOk(handleResponse);
		var response2 = handleResponse.getBody();

		// and
		var request3 = UUID.randomUUID().toString();
		handleResponse = post(url("handle"), request3);
		assertStatusOk(handleResponse);
		var response3 = handleResponse.getBody();

		// then
		assertThat(response1.instanceId().value()).isEqualTo(instanceId1);
		assertThat(response2.instanceId().value()).isEqualTo(instanceId2);
		assertThat(response3.instanceId().value()).isEqualTo(instanceId1);
	}

	private String url(String operation, String key, String value) {
		return "http://localhost:%d/%s?%s=%s".formatted(port, operation, key, value);
	}

	private String url(String operation) {
		return "http://localhost:%d/%s".formatted(port, operation);
	}

	private ResponseEntity<String> post(String url)  {
		return restTemplate.postForEntity(url, null, String.class);
	}

	private ResponseEntity<Response> post(String url, String request)  {
		return restTemplate.postForEntity(url, request, Response.class);
	}

	private static void assertStatusOk(ResponseEntity<?> registerResponse) {
		assertThat(registerResponse.getStatusCode().value()).isEqualTo(200);
	}
}
