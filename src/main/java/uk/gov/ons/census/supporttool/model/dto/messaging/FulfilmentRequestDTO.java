package uk.gov.ons.census.supporttool.model.dto.messaging;

import java.util.UUID;
import lombok.Data;
import uk.gov.ons.census.supporttool.model.dto.rest.Contact;

@Data
public class FulfilmentRequestDTO {
  private UUID caseId;
  private String fulfilmentCode;
  private Contact contact;
}
