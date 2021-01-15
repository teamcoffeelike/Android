package com.hanul.caramelhomecchiato.event;

import java.util.Objects;
import java.util.WeakHashMap;

public abstract class BooleanStateBus<LISTENER> implements Bus{
	private final WeakHashMap<Ticket, LISTENER> listenerMap = new WeakHashMap<>();

	private boolean state = false;
	private int idIncrement;

	public Ticket subscribe(LISTENER listener){
		int id = idIncrement++;
		Ticket ticket = new Ticket(this, id);
		if(listenerMap.put(ticket, Objects.requireNonNull(listener))!=null) throw new IllegalStateException("Unexpected");
		dispatch(listener, state);
		return ticket;
	}

	public void dispatch(boolean newState){
		if(state!=newState){
			state = newState;
			for(LISTENER listener : listenerMap.values()){
				dispatch(listener, newState);
			}
		}
	}

	protected abstract void dispatch(LISTENER listener, boolean newState);

	@Override public void unsubscribe(Ticket ticket){
		if(ticket.bus!=this) throw new IllegalArgumentException("Ticket not registered here");
		listenerMap.remove(ticket);
	}
}
