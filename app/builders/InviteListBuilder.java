package builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import models.Invitee;
import play.Logger;

public class InviteListBuilder<T> {
	
	Iterable<T> invites;
	Supplier<String> irnGenerator;
	Function<T, String> irnFunction;
	Function<T, String> nameFunction;
	Function<T, Integer> adultCount;
	Function<T, Integer> childCount;
	Function<T, String> email;
	Function<T, Boolean> firstEvent;
	Function<T, Boolean> secondEvent;
	Date rsvpBy;
	
	
	public InviteListBuilder (Iterable<T> invites) {
		this.invites = invites;
	}
	
	public List<Invitee> build() {
		List<Invitee> inviteList = new ArrayList<>();
		int inviteCount = 1;
		for (T t: invites) {
			if (t != null) {
				try {
					Invitee i = new Invitee();
					if (irnGenerator != null)
						i.setIrn(irnGenerator.get());
					if (irnFunction != null)
						i.setIrn(irnFunction.apply(t));
					if (nameFunction != null)
						i.setDisplayName(nameFunction.apply(t));
					if (adultCount != null)
						i.setNumberInvited(adultCount.apply(t));
					if (childCount != null)
						i.setNumberKidsInvited(childCount.apply(t));
					if (firstEvent != null)
						i.setEdinburgh(firstEvent.apply(t));
					if (secondEvent != null)
						i.setAustralia(secondEvent.apply(t));
					if (email != null)
						i.setEmail(email.apply(t));
					if (rsvpBy != null)
						i.setRsvpByDate(rsvpBy);
					if (i.getNumberKidsInvited() > 0)
						i.setKids(true);
					
					inviteList.add(i);
				} catch (Exception e) {
					Logger.error("Error creating invite at acount " + inviteCount);
				}
				inviteCount++;
			}
		}
		
		return inviteList;
	}
	
	public InviteListBuilder<T> setIrnGenerator(Supplier<String> irnGenerator) {
		this.irnGenerator = irnGenerator;
		return this;
	}

	public InviteListBuilder<T> setInvites(Collection<T> invites) {
		this.invites = invites;
		return this;
	}

	public InviteListBuilder<T> setNameFunction(Function<T, String> nameFunction) {
		this.nameFunction = nameFunction;
		return this;
	}

	public InviteListBuilder<T> setAdultCount(Function<T, Integer> adultCount) {
		this.adultCount = adultCount;
		return this;
	}

	public InviteListBuilder<T> setChildCount(Function<T, Integer> childCount) {
		this.childCount = childCount;
		return this;
	}

	public InviteListBuilder<T> setEmail(Function<T, String> email) {
		this.email = email;
		return this;
	}

	public InviteListBuilder<T> setFirstEvent(Function<T, Boolean> firstEvent) {
		this.firstEvent = firstEvent;
		return this;
	}

	public InviteListBuilder<T> setSecondEvent(Function<T, Boolean> secondEvent) {
		this.secondEvent = secondEvent;
		return this;
	}

	public InviteListBuilder<T> setRsvpBy(Date rsvpBy) {
		this.rsvpBy = rsvpBy;
		return this;
	}

	public InviteListBuilder<T> setIrnFunction(Function<T, String> irnFunction) {
		this.irnFunction = irnFunction;
		return this;
	}

}
