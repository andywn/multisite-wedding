package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import com.google.inject.Inject;

import listeners.DropboxListener;
import listeners.EmailListener;
import listeners.RsvpListener;
import models.Invitee;
import models.Rsvp;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Main Invite Page Controller
 * 
 * @author andrew
 *
 */
public class Invitation extends Controller {
	
	private Form<Rsvp> rsvpForm = Form.form(Rsvp.class);
	@Inject private Set<RsvpListener> listeners;
	
	public Invitation() {
		
	}
	
	
	public Result success(String irn) {
		return invite(irn, true);
	}
	
	public Result invite(String irn) {
		return invite(irn, false);
	}
	
	/**
	 * Main Invite Page
	 * @param irn
	 * @return
	 */
    public Result invite(String irn, boolean success) {
    	Logger.info("Access attempt to invitee " + irn);
    	Invitee i = Invitee.find.byId(irn);
    	if (i == null) {
    		Logger.error("Failed access to invitee " + irn);
    		return ok(views.html.error.invitemissing.render(irn));
    	}
    	if (i.email != null) {
    		i.email = i.email.trim();
    	}
    	i.setLastViewed(new Date());
    	i.save();
    	return ok(views.html.invite.invite.render(i, rsvpForm.fill(i.getLatestRsvp()), success));
    }
    
    public Result save(String irn) throws Exception {
    	Logger.info("Save attempt on rsvp for invitee " + irn);
    	Form<Rsvp> boundForm = rsvpForm.bindFromRequest();
    	if (boundForm.hasErrors()) {
    		Invitee i = Invitee.find.byId(irn);
    		if (i == null) {
    			Logger.error("Save attempt failed on rsvp with no valid irn: " + irn);
    			return badRequest();
    		}
    		Logger.error("Save attempt failed on rsvp for invitee " + irn);
    		for (String field: boundForm.errors().keySet()) {
    			Logger.error(irn + " > " + field + " field contains errors (" + boundForm.errors().get(field).get(0).toString() + ")");
    		}
    		flash("error", "Please correct the form below");
    		return ok(views.html.invite.invite.render(i, boundForm, false));
    	}
    	Rsvp r = boundForm.get();
    	r.submitted = new Date();
    	Invitee fromDb = Invitee.find.byId(irn);
    	if (fromDb.rsvps == null) {
    		fromDb.rsvps = new ArrayList<>();
    	}
    	fromDb.lastViewed = new Date();
    	fromDb.rsvps.add(r);
    	fromDb.save();
    	flash("success", "Thank you for RSVPing");
    	for (RsvpListener listener: this.listeners) {
    		if (listener.isEnabled())
    			listener.announceRsvp(r);
    	}
    	return redirect(routes.Invitation.success(fromDb.irn));
    	
    }
    
    public Result defaultInvite() {
    	Invitee i = new Invitee();
    	i.edinburgh = false;
    	i.australia = false;
    	i.irn = "";
    	i.rsvpByDate = new Date();
    	Logger.info("Default invite view");
    	return ok(views.html.invite.invite.render(i, rsvpForm.fill(i.getLatestRsvp()), false));
    }
    
}