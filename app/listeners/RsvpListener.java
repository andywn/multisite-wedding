package listeners;

import java.util.concurrent.CompletableFuture;

import models.Rsvp;

public interface RsvpListener {

	public CompletableFuture<Void> announceRsvp(Rsvp rsvp) throws Exception;
	public boolean isEnabled();
	
}
