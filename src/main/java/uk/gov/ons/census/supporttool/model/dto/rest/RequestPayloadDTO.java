package uk.gov.ons.census.supporttool.model.dto.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@SuppressWarnings("SameNameButDifferent")
@lombok.Data
@JsonInclude(Include.NON_NULL)
public class RequestPayloadDTO {
  private SmsFulfilment smsFulfilment;
  private EmailFulfilment emailFulfilment;
}
