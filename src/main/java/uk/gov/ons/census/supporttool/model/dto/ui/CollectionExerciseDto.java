package uk.gov.ons.census.supporttool.model.dto.ui;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class CollectionExerciseDto {
  private UUID id;
  private String name;
  private UUID surveyId;
  private String reference;
  private OffsetDateTime startDate;
  private OffsetDateTime endDate;
  private Object metadata;
}
