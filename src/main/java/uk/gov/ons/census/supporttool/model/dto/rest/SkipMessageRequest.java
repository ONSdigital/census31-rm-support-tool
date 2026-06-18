package uk.gov.ons.census.supporttool.model.dto.rest;

@SuppressWarnings("SameNameButDifferent")
@lombok.Data
public class SkipMessageRequest {
  private String messageHash;
  private String skippingUser;
}
