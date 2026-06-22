package uk.gov.ons.census.supporttool.model.dto.ui;

import java.util.UUID;
import lombok.Data;

@Data
public class CaseSearchResult {
  public UUID id;
  public String caseRef;
  public String addressLine1;
  public String postcode;
  public String caseType;
  public String uprn;
  public String addressType;
  public String collectionExerciseName;
}
