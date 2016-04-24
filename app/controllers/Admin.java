package controllers;

import java.io.FileInputStream;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.inject.Inject;

import actions.BasicAuth;
import builders.InviteListBuilder;
import listeners.DropboxListener;
import models.Invitee;
import models.Report;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Administration Page Controller
 * 
 * @author andrew
 *
 */
public class Admin extends Controller {
	
	private Form<Invitee> inviteForm = Form.form(Invitee.class);
	
	private SecureRandom random = new SecureRandom();
	
	@Inject
	private DropboxListener dropboxBackup;
	
	
	@BasicAuth
	public Result main() {
		Logger.info("Viewing admin page");
		List<Invitee> all = Invitee.find.all();
		Collections.sort(all);
		Invitee invite = generateInvitee(this::generateIrn);
		return ok(views.html.invite.admin.render(all, inviteForm.fill(invite), 
				buildReport(all, Invitee::isEdinburgh, (i) -> i.getLastSubmittedRsvp().map((r) -> r.isRsvpEdinburgh()).orElse(false)),
				buildReport(all, Invitee::isAustralia, (i) -> i.getLastSubmittedRsvp().map((r) -> r.isRsvpAustralia()).orElse(false))));
	}
	
	private String generateIrn() {
		return new BigInteger(130, random)
		.toString(32)
		.replace("0", "")
		.replace("o", "")
		.replace("l", "")
		.substring(0, 5);
	}
	
	@BasicAuth
	public Result add() {
		Form<Invitee> boundForm = inviteForm.bindFromRequest();
		Invitee invite = boundForm.get();
		if (invite.numberKidsInvited > 0) {
			invite.kids = true;
		}
		invite.save();
		Logger.info("Adding Invite " + invite.irn);
		return redirect(routes.Admin.main());
	}
	
	@BasicAuth
	public Result delete(String irn) {
		Logger.info("Deleting Invite " + irn);
		Invitee.find.byId(irn).delete();
		return redirect(routes.Admin.main());
	}
	
	private Invitee generateInvitee(Supplier<String> irnGenerator) {
		Invitee invite = new Invitee();
		invite.irn = irnGenerator.get();
		invite.rsvpByDate = new Date();
		return invite;
	}
	
	@BasicAuth
	public Result upload() {
		Logger.info("Uploading backup json");
	    play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
	    if (body.getFiles().size() > 0) {
	    	play.mvc.Http.MultipartFormData.FilePart backup = body.getFiles().get(0);
	    	if (backup != null) {
	    		String fileName = backup.getFilename();
	    		Logger.info("Backing up with file " + fileName);
	    		try {
	    			FileInputStream file = new FileInputStream(backup.getFile());
					List<Invitee> invites = new ObjectMapper().readValue(file, TypeFactory.defaultInstance().constructCollectionType(List.class,
									   Invitee.class)); 
	    			Ebean.beginTransaction();
	    			try {
	    			    for (Invitee inv: invites) {
	    			    	if (Invitee.find.byId(inv.irn) != null) {
	    			    		inv.update();
	    			    	} else {
	    			    		inv.save();
	    			    	}
	    			    }
	    			    Ebean.commitTransaction();
	    			} finally {
	    			    Ebean.endTransaction();
	    			}
	    			return redirect(routes.Admin.main());
	    		} catch (Exception e) {
	    			Logger.error("Error uploading backup: " + e.getMessage());
	    			return badRequest();
	    		}
	    	}
	    } 
	    flash("error", "Missing file");
	    return badRequest();
	}
	
	@BasicAuth
	public Result uploadCsv() {
		Logger.info("Uploading CSV");
		// RSVP By date for all invites.
		DynamicForm requestData = Form.form().bindFromRequest();
		LocalDate rsvpByDate = LocalDate.parse(requestData.get("rsvpByDate"));
		
	    play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
	    if (body.getFile("csvfile") != null) {
	    	play.mvc.Http.MultipartFormData.FilePart csv = body.getFile("csvfile");
	    	if (csv != null) {
	    		String fileName = csv.getFilename();
	    		Logger.info("Upload csv file " + fileName);
	    		try {
	    			FileReader file = new FileReader(csv.getFile());
	    			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(file);
	    			// Upload csv, in column order name, adult count, children count, first event (true or false), second event, email. 
					List<Invitee> invites = new InviteListBuilder<CSVRecord>(records)
							.setNameFunction(r -> r.get(0))
							.setAdultCount(r -> Integer.parseInt(r.get(1)))
							.setChildCount(r -> Integer.parseInt(r.get(2)))
							.setFirstEvent(r -> Boolean.parseBoolean(r.get(3)))
							.setSecondEvent(r -> Boolean.parseBoolean(r.get(4)))
							.setEmail(r -> (r.size() >= 6)?r.get(5):"") // email is optional
							.setRsvpBy(Date.from(rsvpByDate.atStartOfDay()
									.atZone(ZoneId.systemDefault()).toInstant()))
							.setIrnGenerator(this::generateIrn)
							.build();
					Logger.info("Uploading " + invites.size() + " files");
	    			Ebean.beginTransaction();
	    			try {
	    			    for (Invitee inv: invites) {
	    			    	inv.save();
	    			    }
	    			    Ebean.commitTransaction();
	    			} finally {
	    			    Ebean.endTransaction();
	    			}
	    			return redirect(routes.Admin.main());
	    		} catch (Exception e) {
	    			Logger.error("Error uploading CSV: " + e.getMessage() + " -> " + e.getClass().getName());
	    			return badRequest();
	    		}
	    	}
	    } 
	    flash("error", "Missing file");
	    return badRequest();
	}
	
	@BasicAuth
	public Result toggleSent(String irn) {
		Logger.info("Toggling " + irn + " sent toggle");
		Invitee inv = Invitee.find.byId(irn);
		if (inv != null) {
			inv.setInviteSent(!inv.isInviteSent());
			inv.save();
		}
		return redirect(routes.Admin.main());
	}
	
	@BasicAuth
	public Result backup() {
		DynamicForm requestData = Form.form().bindFromRequest();
		String filename = requestData.get("backupFilename");
		if (filename != null && filename.length() > 0) {
			if (!filename.startsWith("/")) {
				filename = "/" + filename;
			}
			Logger.info("Backing up to file " + filename);
			dropboxBackup.backup(filename);
		} else {
			Logger.error("No backup file listed, not backing up");
		}
		return redirect(routes.Admin.main());
	}
	
	private Report buildReport(List<Invitee> invites, Predicate<Invitee> invited, Predicate<Invitee> rsvped) {
		if (invites == null || invites.size() == 0) {
			return new Report();
		}
		return invites.stream().filter(invited).map((i) -> new Report(i, rsvped)).reduce(Report::add).get();
	}
	
}
