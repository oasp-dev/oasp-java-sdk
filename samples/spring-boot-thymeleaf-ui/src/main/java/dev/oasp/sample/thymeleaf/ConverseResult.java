package dev.oasp.sample.thymeleaf;

import java.util.List;

/**
 * Everything the result page needs to render one round-trip: what the user
 * asked, which conversation/session ran it, whether it was the offline demo
 * or a real server call, and the events that came back.
 *
 * @param userMessage    the message the user typed on the form
 * @param conversationId id of the conversation this turn ran on
 * @param sessionId      id of the session the conversation currently rides on
 * @param demo           {@code true} if these events are scripted (offline demo mode)
 * @param events         the collected stream events, flattened for display
 */
public record ConverseResult(
        String userMessage, String conversationId, String sessionId, boolean demo, List<EventView> events) {}
