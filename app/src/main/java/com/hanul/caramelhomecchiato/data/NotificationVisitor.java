package com.hanul.caramelhomecchiato.data;

/**
 * https://en.wikipedia.org/wiki/Visitor_pattern
 */
public interface NotificationVisitor{
	void visit(Notification.Reaction reaction);
	void visit(Notification.Like like);
	void visit(Notification.Follow follow);
}
