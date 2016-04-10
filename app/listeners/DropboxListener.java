package listeners;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.users.FullAccount;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import models.Invitee;
import models.Rsvp;
import play.Logger;

/**
 * Dropbox uploader listener.
 * 
 * Requires configuration (see wedding.conf file)
 * 
 * @author andrew
 *
 */
@Singleton
public class DropboxListener implements RsvpListener {
	
	private String rsvpFilename;
	
	private ReentrantLock uploadLock = new ReentrantLock();
	
	private DbxClientV2 client;
	
	private boolean enabled;
	
	public DropboxListener() {
		Config config = ConfigFactory.load("wedding.conf").getConfig("wedding.dbx");
		enabled = config.getBoolean("enabled");
		if (enabled) {
			// Setup Dropbox Client
			String dbxToken = config.getString("token");
			rsvpFilename = config.getString("rsvpFile");
			if (dbxToken == null || dbxToken.length() == 0) {
				Logger.error("Unable to pull Dropbox Token from config");
				enabled = false;
				return;
			}
			DbxRequestConfig dbxConfig = new DbxRequestConfig("WeddingSite/1.0", "en_US");
			this.client = new DbxClientV2(dbxConfig, dbxToken);
		}
	}

	@Override
	public CompletableFuture<Void> announceRsvp(Rsvp rsvp) throws Exception {
		return backup(rsvpFilename);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	public CompletableFuture<Void> backup(String filename) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		new Thread(() -> {
			try {
		        FullAccount account = client.users().getCurrentAccount();
		        Logger.info("Uploading to dropbox account " + account.getName().getDisplayName());
		        
		        List<Invitee> invites = Invitee.find.all();
		        
		        JsonNode val = play.libs.Json.toJson(invites);
		        InputStream in = new ByteArrayInputStream(val.toString().getBytes(StandardCharsets.UTF_8));
		        client.files().uploadBuilder(filename)
		        		.withMode(WriteMode.OVERWRITE)
		        		.uploadAndFinish(in);
		        uploadLock.unlock();
		        
		        future.complete(null);
			} catch (Exception e) {
				Logger.error(e.getMessage());
				future.completeExceptionally(e);
			}
		}).start();
        return future;
	}

}
