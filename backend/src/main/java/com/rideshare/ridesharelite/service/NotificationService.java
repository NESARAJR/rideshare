package com.rideshare.ridesharelite.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rideshare.ridesharelite.entity.RideRequest;

/** Console "notifications" whenever a request is created or changes status. */
@Service
public class NotificationService {

	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

	public void requestCreated(RideRequest q) {
		log.info("[NOTIFY] Driver {} : {} requested a seat on ride #{} ({} -> {})", q.getRide().getDriver().getName(),
				q.getRider().getName(), q.getRide().getId(), q.getRide().getOrigin(), q.getRide().getDestination());
	}

	public void statusChanged(RideRequest q) {
		if (q.getStatus() == com.rideshare.ridesharelite.entity.RequestStatus.APPROVED) {
			log.info("[NOTIFY] Contacts shared for ride #{}: driver {} ({}) <-> rider {} ({}), {} seat(s)",
					q.getRide().getId(), q.getRide().getDriver().getName(), q.getRide().getDriver().getPhone(),
					q.getRider().getName(), q.getRider().getPhone(), q.getSeatsRequested());
		}
		log.info("[NOTIFY] Rider {} : your request #{} for ride #{} is now {}", q.getRider().getName(), q.getId(),
				q.getRide().getId(), q.getStatus());
	}
}
