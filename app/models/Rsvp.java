package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;

@Entity
public class Rsvp {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	@JsonIgnore
	public Long id;
	
	@Required
	@MaxLength(255)
	public String name;
	
	@Enumerated(EnumType.STRING)
	public RsvpType rsvp = null;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="invitee_id")
	public Invitee invitee;
	
	@Column(name="food")
	@MaxLength(255)
	public String foodRequirements;
	
	@Required
	@Min(0)
	@Max(8)
	public int guestNumber;
	
	@Min(0)
	@Max(5)
	public int kids;
	
	@Min(0)
	@Max(5)
	public int toddlers;
	
	@Email
	@MaxLength(64)
	public String email;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date submitted;
	
	@MaxLength(255)
	public String comments;
	
	@Transient
	@JsonIgnore
	public boolean isRsvpEdinburgh() {
		return (rsvp != null && (rsvp == RsvpType.EDINBURGH || rsvp == RsvpType.BOTH));
	}
	
	/**
	 * Enables empty check boxes.  rsvp == null implies (!isNotRsvp && !isRsvp)
	 * @return
	 */
	@Transient
	@JsonIgnore
	public boolean isNotRsvpEdinburgh() {
		return (rsvp != null && (rsvp == RsvpType.AUSTRALIA || rsvp == RsvpType.NEITHER));
	}
	
	public void setRsvpEdinburgh(boolean edinburgh) {
		if (edinburgh) {
			if (rsvp == null || rsvp == RsvpType.EDINBURGH || rsvp == RsvpType.NEITHER) {
				rsvp = RsvpType.EDINBURGH;
			} else {
				rsvp = RsvpType.BOTH;
			}
		} else {
			if (rsvp == RsvpType.BOTH || rsvp == RsvpType.AUSTRALIA) {
				rsvp = RsvpType.AUSTRALIA;
			} else {
				rsvp = RsvpType.NEITHER;
			}
		}
	}
	
	@Transient
	@JsonIgnore
	public boolean isRsvpAustralia() {
		return (rsvp != null && (rsvp == RsvpType.AUSTRALIA || rsvp == RsvpType.BOTH));
	}
	
	@Transient
	@JsonIgnore
	public boolean isNotRsvpAustralia() {
		return (rsvp != null && (rsvp == RsvpType.EDINBURGH || rsvp == RsvpType.NEITHER));
	}
	
	public void setRsvpAustralia(boolean australia) {
		if (australia) {
			if (rsvp == null || rsvp == RsvpType.AUSTRALIA || rsvp == RsvpType.NEITHER) {
				rsvp = RsvpType.AUSTRALIA;
			} else {
				rsvp = RsvpType.BOTH;
			}
		} else {
			if (rsvp == RsvpType.BOTH || rsvp == RsvpType.EDINBURGH) {
				rsvp = RsvpType.EDINBURGH;
			} else {
				rsvp = RsvpType.NEITHER;
			}
		}
	}

	@Override
	public String toString() {
		return "Rsvp [id=" + id + ", name=" + name + ", rsvp=" + rsvp + ", invitee=" + invitee + ", foodRequirements="
				+ foodRequirements + ", submitted=" + submitted + ", edinburgh=" + isRsvpEdinburgh() + ", australia="
				+ isRsvpAustralia() + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RsvpType getRsvp() {
		return rsvp;
	}

	public void setRsvp(RsvpType rsvp) {
		this.rsvp = rsvp;
	}

	public Invitee getInvitee() {
		return invitee;
	}

	public void setInvitee(Invitee invitee) {
		this.invitee = invitee;
	}

	public String getFoodRequirements() {
		return foodRequirements;
	}

	public void setFoodRequirements(String foodRequirements) {
		this.foodRequirements = foodRequirements;
	}

	public int getGuestNumber() {
		return guestNumber;
	}

	public void setGuestNumber(int guestNumber) {
		this.guestNumber = guestNumber;
	}

	public int getKids() {
		return kids;
	}

	public void setKids(int kids) {
		this.kids = kids;
	}

	public int getToddlers() {
		return toddlers;
	}

	public void setToddlers(int toddlers) {
		this.toddlers = toddlers;
	}

	public String getEmail() {
		return email.trim();
	}

	public void setEmail(String email) {
		this.email = email.trim();
	}

	public Date getSubmitted() {
		return submitted;
	}

	public void setSubmitted(Date submitted) {
		this.submitted = submitted;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
}
