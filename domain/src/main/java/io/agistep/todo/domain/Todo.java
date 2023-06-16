package io.agistep.todo.domain;

import io.agistep.event.DomainEventApplier;
import io.agistep.event.Event;
import io.agistep.event.Events;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;


@Getter
public class Todo {

	public static Todo replay(List<Event> events) {
		if(events == null || events.isEmpty()) {
			return null;
		}
		return replay(events.toArray(new Event[0]));
	}

	public static Todo replay(Event... events) {
		if(events == null || events.length == 0) {
			return null;
		}
		return new Todo(events);
	}


	private TodoIdentity id;
	private String text;
	private boolean done;
	private boolean hold;

	Todo(String text) {
		TodoCreated created = TodoCreated.newBuilder()
				.setText(text)
				.build();
		Event anEvent = Events.begin(created);
		DomainEventApplier.instance().apply(this, anEvent);
	}

	private Todo(Event... anEvent) {
		Arrays.stream(anEvent).forEach(e -> {
			DomainEventApplier.instance().replay(this, e);
		});
	}

	void onCreated(Event anEvent) {
		this.id = new TodoIdentity(anEvent.getAggregateIdValue());
		this.text = ((TodoCreated) anEvent.getPayload()).getText();
		this.done = false;
	}

	public void done() {
		if(isDone()) {
			return;
		}
		Event anEvent = Events.occurs(this, TodoDone.newBuilder().build());
		DomainEventApplier.instance().apply(this, anEvent);
	}

	void onDone(Event anEvent) {
		this.done = true;
	}

	public void updateText(String text) {
		Event anEvent = Events.occurs(this, TodoTextUpdated.newBuilder().setUpdatedText(text).build());
		DomainEventApplier.instance().apply(this, anEvent);
	}

	void onTextUpdated(Event anEvent) {
		this.text = ((TodoTextUpdated) anEvent.getPayload()).getUpdatedText();
	}

	public void hold() {
		if (isDone()) {
			return;
		}
		Event anEvent = Events.occurs(id.getValue(), TodoHeld.newBuilder().build());
		DomainEventApplier.instance().apply(this, anEvent);
	}

	void onHeld(Event anEvent) {
		this.hold = true;
	}


}
