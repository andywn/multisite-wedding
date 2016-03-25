package modules;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import listeners.DropboxListener;
import listeners.EmailListener;
import listeners.RsvpListener;

public class GuiceConfiguration extends AbstractModule {

	@Override
	protected void configure() {
		Multibinder<RsvpListener> listeners = Multibinder.newSetBinder(binder(), RsvpListener.class);
		listeners.addBinding().to(DropboxListener.class);
		listeners.addBinding().to(EmailListener.class);
	}

}
