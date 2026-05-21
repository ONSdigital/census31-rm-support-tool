package uk.gov.ons.census.supporttool.model.dto.messaging;

import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class SurveyUpdateDto {
  private UUID surveyId;
  private String name;
  private Object metadata;
  private List<AllowedFulfilmentDto> allowedPrintFulfilments;
  private List<AllowedFulfilmentDto> allowedSmsFulfilments;
  private List<AllowedFulfilmentDto> allowedEmailFulfilments;
}
