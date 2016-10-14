package com.handlers;

import com.Message;

public interface Handler extends Runnable {
	void processMessage();

	void setMessage(Message message);
}
