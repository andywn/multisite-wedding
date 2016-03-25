package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import play.Logger;
import play.data.validation.Constraints.Required;

@Entity
public class Invitee extends Model {
	
	@Id
	public String irn;
	
	@Required
	public String displayName;
	
	public boolean plusOne;
	public boolean edinburgh;
	public boolean australia;
	public int numberInvited;
	public boolean kids;
	public int numberKidsInvited;
	public Date rsvpByDate;
	public String email;
	public boolean inviteSent;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy="invitee", cascade=CascadeType.ALL)
	@OrderBy("id")
	public List<Rsvp> rsvps = new ArrayList<>();
	
	@Temporal(TemporalType.TIMESTAMP)
	@UpdatedTimestamp
	public Date lastViewed;
	
	public static Finder<String, Invitee> find = new Finder<>(Invitee.class);
	
	public Invitee() {
		
	}
	
	public Invitee(String irn, String displayName, boolean plusOne, int numberInvited, boolean edinburgh, boolean australia, Date rsvpDate) {
		super();
		this.irn = irn;
		this.displayName = displayName;
		this.plusOne = plusOne;
		this.edinburgh = edinburgh;
		this.australia = australia;
		this.numberInvited = numberInvited;
		this.rsvpByDate = rsvpDate;
	}
	
	@Transient
	@JsonIgnore
	public Rsvp getLatestRsvp() {
		if (rsvps == null) {
			rsvps = new ArrayList<>();
		}
		if (rsvps.size() == 0) {
			Rsvp rsvp = new Rsvp();
			rsvp.invitee = this;
			rsvp.name = this.displayName;
			rsvp.rsvp = null;
			rsvp.guestNumber = this.numberInvited;
			rsvp.email = this.email;
			rsvp.kids = this.numberKidsInvited;
			return rsvp;
		} else {
			Logger.debug("Size: " + rsvps.get(rsvps.size()-1));
			return rsvps.get(rsvps.size()-1);
		}
	}
	
	@Transient
	@JsonIgnore
	public Optional<Rsvp> getLastSubmittedRsvp() {
		if (rsvps.size() == 0)
			return Optional.empty();
		return Optional.ofNullable(rsvps.get(rsvps.size()-1));
	}
	
	public String getIrn() {
		return irn;
	}

	public void setIrn(String irn) {
		this.irn = irn;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isPlusOne() {
		return plusOne;
	}

	public void setPlusOne(boolean plusOne) {
		this.plusOne = plusOne;
	}

	public boolean isEdinburgh() {
		return edinburgh;
	}

	public void setEdinburgh(boolean edinburgh) {
		this.edinburgh = edinburgh;
	}

	public boolean isAustralia() {
		return australia;
	}

	public void setAustralia(boolean australia) {
		this.australia = australia;
	}

	public int getNumberInvited() {
		return numberInvited;
	}

	public void setNumberInvited(int numberInvited) {
		this.numberInvited = numberInvited;
	}

	public boolean isKids() {
		return kids;
	}

	public void setKids(boolean kids) {
		this.kids = kids;
	}

	public int getNumberKidsInvited() {
		return numberKidsInvited;
	}

	public void setNumberKidsInvited(int numberKidsInvited) {
		this.numberKidsInvited = numberKidsInvited;
	}

	public Date getRsvpByDate() {
		return rsvpByDate;
	}

	public void setRsvpByDate(Date rsvpByDate) {
		this.rsvpByDate = rsvpByDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isInviteSent() {
		return inviteSent;
	}

	public void setInviteSent(boolean inviteSent) {
		this.inviteSent = inviteSent;
	}

	public List<Rsvp> getRsvps() {
		return rsvps;
	}

	public void setRsvps(List<Rsvp> rsvps) {
		this.rsvps = rsvps;
	}

	public Date getLastViewed() {
		return lastViewed;
	}

	public void setLastViewed(Date lastViewed) {
		this.lastViewed = lastViewed;
	}

}
