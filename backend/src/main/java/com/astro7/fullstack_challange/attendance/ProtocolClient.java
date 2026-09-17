package com.astro7.fullstack_challange.attendance;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProtocolClient {

	private final RestClient restClient;

	public ProtocolClient(@Value("${protocol.url}") String url, @Value("${protocol.timeout}") Duration timeout) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(timeout);
		requestFactory.setReadTimeout(timeout);

		this.restClient = RestClient.builder()
				.baseUrl(url)
				.requestFactory(requestFactory)
				.build();
	}

	public String fetchProtocol() {
		UuidResponse response = restClient.get().retrieve().body(UuidResponse.class);
		if (response == null || response.uuid() == null || response.uuid().isBlank()) {
			throw new IllegalStateException("Serviço externo não retornou protocolo");
		}
		return response.uuid();
	}

	record UuidResponse(String uuid) {
	}

}
