package uk.gov.ons.census.supporttool.model.dto.ui;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;
import uk.gov.ons.census.common.model.entity.ActionRuleStatus;
import uk.gov.ons.census.common.model.entity.ActionRuleType;

@Data
public class ActionRuleDto {

  private UUID actionRuleId;

  private UUID collectionExerciseId;

  private String packCode;

  private String classifiers;

  private String description;

  private ActionRuleType type;

  private OffsetDateTime triggerDateTime;

  private Object uacMetadata;

  private boolean hasTriggered;

  private ActionRuleStatus actionRuleStatus;

  private Integer selectedCaseCount;
}
