package uk.gov.ons.census.supporttool.model.dto.rest;

import java.util.UUID;

@SuppressWarnings("SameNameButDifferent")
@lombok.Data
public class RequestHeaderDTO {
  private String source;
  private String channel;
  private UUID correlationId;
  private String originatingUser;
}
