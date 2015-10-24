package org.fs.utils.event;

public final class NotifierThread extends Thread {

	private final Notifiable	notifiableInstance;
	private final Thread		watchedInstance;
	private Object				data	= null;

	public NotifierThread(final Notifiable notifiableInstance, final Thread watchedInstance, final Object data) {
		super("NotifierThread");
		this.notifiableInstance = notifiableInstance;
		this.watchedInstance = watchedInstance;
		this.data = data;
		start();
	}

	public NotifierThread(final Notifiable notifiableInstance, final Thread watchedInstance) {
		this(notifiableInstance, watchedInstance, null);
	}

	@Override
	public void run() {
		try {
			watchedInstance.join();
			notifiableInstance.doNotify(data);
		} catch(final Exception e) {
			e.printStackTrace();
		}
	}
}

