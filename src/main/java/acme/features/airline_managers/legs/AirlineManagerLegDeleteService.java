
package acme.features.airline_managers.legs;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.claims.Claim;
import acme.entities.flight_assignments.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.airline_managers.AirlineManager;

@GuiService
public class AirlineManagerLegDeleteService extends AbstractGuiService<AirlineManager, Leg> {

	@Autowired
	private AirlineManagerLegRepository repository;


	@Override
	public void authorise() {
		boolean status = false;

		if (super.getRequest().hasData("id")) {
			int legId = super.getRequest().getData("id", int.class);
			int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();

			Optional<Leg> optionalLeg = this.repository.findByLegId(legId);

			if (optionalLeg.isPresent()) {
				Leg leg = optionalLeg.get();

				if (leg.isDraftMode() && this.repository.findByIdAndManagerId(leg.getFlight().getId(), managerId).isPresent()) {
					status = true;

					if (super.getRequest().hasData("aircraft")) {
						int aircraftId = super.getRequest().getData("aircraft", int.class);
						Aircraft aircraft = this.repository.findAircraftByAircraftId(aircraftId);
						List<Aircraft> aircrafts = this.repository.findAllAircraftsByManagerId(managerId);

						if (aircraftId != 0 && aircraft == null || aircraft != null && !aircrafts.contains(aircraft))
							status = false;
					}

					List<Airport> airports = this.repository.findAllAirports();

					if (super.getRequest().hasData("departureAirport")) {
						int departureId = super.getRequest().getData("departureAirport", int.class);
						Airport departure = this.repository.findAirportByAirportId(departureId);

						if (departureId != 0 && departure == null || departure != null && !airports.contains(departure))
							status = false;
					}

					if (super.getRequest().hasData("arrivalAirport")) {
						int arrivalId = super.getRequest().getData("arrivalAirport", int.class);
						Airport arrival = this.repository.findAirportByAirportId(arrivalId);

						if (arrivalId != 0 && arrival == null || arrival != null && !airports.contains(arrival))
							status = false;
					}
				}
			}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Leg leg;
		int id;

		id = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegByLegId(id);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		int aircraftId;
		int airportArrivalId;
		int airportDepartureId;
		Aircraft aircraft;
		Airport departure;
		Airport arrival;

		aircraftId = super.getRequest().getData("aircraft", int.class);
		aircraft = this.repository.findAircraftByAircraftId(aircraftId);
		airportArrivalId = super.getRequest().getData("arrivalAirport", int.class);
		departure = this.repository.findAirportByAirportId(airportArrivalId);
		airportDepartureId = super.getRequest().getData("departureAirport", int.class);
		arrival = this.repository.findAirportByAirportId(airportDepartureId);

		super.bindObject(leg, "flightNumber", "scheduledDeparture", "scheduledArrival", "status");
		leg.setAircraft(aircraft);
		leg.setDepartureAirport(departure);
		leg.setArrivalAirport(arrival);
		leg.durationInHours();
	}

	@Override
	public void validate(final Leg leg) {
		;
	}

	@Override
	public void perform(final Leg leg) {
		List<FlightAssignment> flightAssignments;
		List<Claim> claims;

		flightAssignments = this.repository.findFlightAssignmentsByLegId(leg.getId());
		flightAssignments.stream().forEach(f -> this.repository.deleteAll(this.repository.findActivityLogsByFlightAssignmentId(f.getId())));
		claims = this.repository.findClaimsByLegId(leg.getId());
		claims.stream().forEach(c -> this.repository.deleteAll(this.repository.findTrackingLogByClaimId(c.getId())));

		this.repository.deleteAll(flightAssignments);
		this.repository.deleteAll(claims);
		this.repository.delete(leg);
	}

	// Este servicio no tiene unbind, debido a que no se utilizaría en ninguna condición ya que no debes volver a renderizar la página tras eliminar un tramo. 

}
