
package acme.features.administrator.airline;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airlines.Airline;

@GuiService
public class AdministratorAirlineListService extends AbstractGuiService<Administrator, Airline> {

	@Autowired
	private AdministratorAirlineRepository repository;


	@Override
	public void authorise() {

		Administrator admin;
		boolean status;
		admin = (Administrator) super.getRequest().getPrincipal().getActiveRealm();

		status = super.getRequest().getPrincipal().hasRealm(admin);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Airline> airlines;
		airlines = this.repository.findAllAirlines();
		super.getBuffer().addData(airlines);
	}

	@Override
	public void unbind(final Airline airline) {
		Dataset dataset;
		dataset = super.unbindObject(airline, "iataCode", "name", "type");
		super.getResponse().addData(dataset);

	}

}
