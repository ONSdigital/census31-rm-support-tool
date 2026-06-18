package uk.gov.ons.census.supporttool.model.dto.rest;

@SuppressWarnings("SameNameButDifferent")
@lombok.Data
public class RequestDTO {
  private RequestHeaderDTO header;
  private RequestPayloadDTO payload;
}
