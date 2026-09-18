package com.astro7.fullstack_challange.attendance;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import tools.jackson.databind.ObjectMapper;

@Component
public class AttendanceBroadcaster extends TextWebSocketHandler {

	private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
	private final ObjectMapper objectMapper;

	public AttendanceBroadcaster(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		sessions.add(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		sessions.remove(session);
	}

	public void broadcast(Attendance attendance) {
		TextMessage message = new TextMessage(objectMapper.writeValueAsString(AttendanceResponse.from(attendance)));

		for (WebSocketSession session : sessions) {
			if (!session.isOpen()) {
				continue;
			}
			try {
				session.sendMessage(message);
			} catch (IOException e) {
				sessions.remove(session);
			}
		}
	}

}
