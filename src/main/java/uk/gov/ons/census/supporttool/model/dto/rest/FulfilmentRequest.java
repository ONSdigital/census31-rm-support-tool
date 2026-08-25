package uk.gov.ons.census.supporttool.model.dto.rest;

import java.util.UUID;
import lombok.Data;

@Data
public class FulfilmentRequest {
  private UUID caseId;
  private String fulfilmentCode;
  private Contact contact;
}
