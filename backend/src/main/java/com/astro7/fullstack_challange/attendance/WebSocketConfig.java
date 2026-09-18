package com.astro7.fullstack_challange.attendance;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	private final AttendanceBroadcaster broadcaster;
	private final String[] allowedOrigins;

	public WebSocketConfig(AttendanceBroadcaster broadcaster, @Value("${cors.allowed-origins}") String[] allowedOrigins) {
		this.broadcaster = broadcaster;
		this.allowedOrigins = allowedOrigins;
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(broadcaster, "/ws/attendances").setAllowedOriginPatterns(allowedOrigins);
	}

}
