package io.agistep.event;

import java.time.LocalDateTime;

public final class Events {

	public static final long BEGIN_ORDER = 1L;

	static EventBuilder builder() {
		return new EventBuilder();
	}

	static Event create(long aggregateIdValue, Object payload) {
		return Events.builder()
				.name(payload.getClass().getName())
				.order(BEGIN_ORDER) //TODO fix 이전 order 를 알아야한다.
				.aggregateIdValue(aggregateIdValue)
				.payload(payload)
				.occurredAt(LocalDateTime.now())
				.build();
	}

}
