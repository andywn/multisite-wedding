package models;

import java.util.function.Predicate;

import com.avaje.ebean.Model;

public class Report extends Model {

	public int toddlerCount;
	public int underEighteenCount;
	public int adultCount;
	public int cannotCome;
	public int noResponse;

	
	public Report() {
		
	}
	
	public Report(Invitee i, Predicate<Invitee> rsvped) {
		if (i.rsvps != null && i.rsvps.size() > 0) {
			if (rsvped.test(i)) {
				Rsvp r = i.rsvps.get(i.rsvps.size() - 1);
				toddlerCount = r.toddlers;
				underEighteenCount = r.kids;
				adultCount = r.guestNumber;
			} else {
				cannotCome = i.numberInvited + i.numberKidsInvited;
			}
		} else {
			noResponse = i.numberKidsInvited + i.numberInvited;
		}
	}
	
	public static Report add(Report a, Report b) {
		a.toddlerCount += b.toddlerCount;
		a.underEighteenCount += b.underEighteenCount;
		a.adultCount += b.adultCount;
		a.noResponse += b.noResponse;
		a.cannotCome += b.cannotCome;
		return a;
	}
	
}
