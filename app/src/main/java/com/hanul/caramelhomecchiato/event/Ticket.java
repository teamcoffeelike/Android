package com.hanul.caramelhomecchiato.event;

import androidx.annotation.MainThread;

/**
 * 이벤트의 Dispatcher 내부에서 해당 listener를 구분해 주는 오브젝트. 이벤트가 유효하지 않게 될 때까지 참조를 유지하십시오.<br>
 * {@link Ticket#unsubscribe()} 호출은 필수는 아니지만, 호출하지 않으면 GC로 dispatcher가 정리되기 이전까지 리스너가 유효하지 않은 시기에 이벤트를 받을 수 있습니다.
 */
public final class Ticket{
	final Bus bus;
	private final int id;

	private boolean unsubscribed = false;

	Ticket(Bus bus, int id){
		this.bus = bus;
		this.id = id;
	}

	@MainThread
	public void unsubscribe(){
		if(!unsubscribed){
			bus.unsubscribe(this);
			unsubscribed = true;
		}
	}

	@Override public boolean equals(Object o){
		if(this==o) return true;
		if(o==null||getClass()!=o.getClass()) return false;

		Ticket ticket = (Ticket)o;

		return id==ticket.id;
	}

	@Override public int hashCode(){
		return id;
	}
}
