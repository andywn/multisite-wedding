package listeners;

import java.util.concurrent.CompletableFuture;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import com.google.inject.Singleton;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import models.Rsvp;
import play.Logger;

@Singleton
public class EmailListener implements RsvpListener {
	
	private EmailConfig config;
	
	private boolean enabled;
	
	public EmailListener() {
		Config config = ConfigFactory.load("wedding.conf").getConfig("wedding.mail");
		enabled = config.getBoolean("enabled");
		this.config = new EmailConfig(config);
	}
	
	public CompletableFuture<Void> announceRsvp(Rsvp rsvp) throws Exception {
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		new Thread(() -> {
			try {
				Email email = new SimpleEmail();
				email.setHostName(config.smtpServer);
				email.setSmtpPort(config.port);
				email.setAuthenticator(new DefaultAuthenticator(config.username, config.password));
				email.setSSLOnConnect(true);
				email.setFrom(config.from);
				email.setSubject("New RSVP from " + rsvp.name);
				email.setMsg(rsvpToString(rsvp));
				email.addTo(config.to);
				email.send();
				Logger.info("Email sent");
			} catch (Exception e) {
				Logger.error("Error sending rsvp email " + e.getMessage());
				e.printStackTrace();
			}
			future.complete(null);
		}).start();
		return future;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	private String rsvpToString(Rsvp rsvp) {
		StringBuilder builder = new StringBuilder();
		builder.append(rsvp.name)
			.append(" (irn ")
			.append(rsvp.invitee.irn)
			.append(")")
			.append(" has RSVPed, and will be coming to ");
		switch(rsvp.rsvp) {
		case AUSTRALIA:
			builder.append("Australia only");
			break;
		case EDINBURGH:
			builder.append("Edinburgh only");
			break;
		case BOTH:
			builder.append("both Australia and Edinburgh");
			break;
		case NEITHER:
			builder.append("neither Australia nor Edinburgh");
			break;
		default:
			builder.append("not specified");
		}
		builder.append("\n")
			.append("Totals: ")
			.append(rsvp.guestNumber)
			.append(" adults")
			.append((rsvp.kids > 0)?rsvp.kids + " kids":"")
			.append((rsvp.toddlers > 0)?rsvp.toddlers + " toddlers":"")
			.append(".\n")
			.append("Food requirements:\n")
			.append(rsvp.foodRequirements)
			.append("\n")
			.append("Comments:\n")
			.append(rsvp.comments)
			.append("\n")
			.append("all-details:")
			.append(rsvp.toString());
		
		return builder.toString();
			
	}

}

class EmailConfig {
	
	String username;
	String password;
	String smtpServer;
	String from;
	String[] to;
	int port;
	
	public EmailConfig(Config config) {
		username = config.getString("username");
		password = config.getString("password");
		smtpServer = config.getString("smtp-server");
		port = config.getInt("port");
		from = config.getString("from");
		to = config.getString("to").split(";");
	}
	
}
