
package acme.entities.maintenance;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidInspectionDueDateAfterMoment;
import acme.entities.aircrafts.Aircraft;
import acme.realms.technicians.Technician;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@ValidInspectionDueDateAfterMoment
@Table(indexes = {
	@Index(columnList = "technician_id"), @Index(columnList = "draftMode")
})
public class MaintenanceRecord extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				maintenanceMoment;

	@Mandatory
	@Valid
	@Automapped
	private MaintenanceStatus	status;

	@Mandatory
	@ValidMoment(past = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				nextInspectionDue;

	@Mandatory
	@ValidMoney(min = 0)
	@Automapped
	private Money				estimatedCost;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				notes;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Aircraft			aircraft;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Technician			technician;

}
