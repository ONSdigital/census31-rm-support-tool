package uk.gov.ons.census.supporttool.model.dto.ui;

import java.util.UUID;
import lombok.Data;

@Data
public class SurveyDto {
  private UUID id;
  private String name;
  private boolean sampleWithHeaderRow;
  private char sampleSeparator;
  private Object metadata;
}
