package uk.gov.ons.census.supporttool.model.dto.rest;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("SameNameButDifferent")
@lombok.Data
public class EmailFulfilment {
  private UUID caseId;
  private String email;
  private String packCode;
  private Object uacMetadata;
  private Map<String, String> personalisation;
}
