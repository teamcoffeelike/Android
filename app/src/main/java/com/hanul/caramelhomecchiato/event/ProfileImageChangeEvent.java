package com.hanul.caramelhomecchiato.event;

import androidx.annotation.MainThread;

import java.util.Objects;
import java.util.WeakHashMap;

public final class ProfileImageChangeEvent{
	private ProfileImageChangeEvent(){}

	@MainThread public static Ticket subscribe(OnProfileImageChanged listener){
		return ProfileImageChangeEventBus.INSTANCE.subscribe(listener);
	}

	@MainThread public static void dispatch(){
		ProfileImageChangeEventBus.INSTANCE.dispatch();
	}

	private enum ProfileImageChangeEventBus implements Bus{
		INSTANCE;

		private final WeakHashMap<Ticket, OnProfileImageChanged> listenerMap = new WeakHashMap<>();

		private int idIncrement;

		public Ticket subscribe(OnProfileImageChanged listener){
			int id = idIncrement++;
			Ticket ticket = new Ticket(this, id);
			if(listenerMap.put(ticket, Objects.requireNonNull(listener))!=null) throw new IllegalStateException("Unexpected");
			return ticket;
		}

		public void dispatch(){
			for(OnProfileImageChanged listener : listenerMap.values()){
				listener.onProfileImageChanged();
			}
		}

		@Override public void unsubscribe(Ticket ticket){
			if(ticket.bus!=this) throw new IllegalArgumentException("Ticket not registered here");
			listenerMap.remove(ticket);
		}
	}
}
