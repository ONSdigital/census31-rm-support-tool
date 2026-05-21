package uk.gov.ons.census.supporttool.utility;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.common.model.entity.CollectionExercise;
import uk.gov.ons.census.common.model.entity.JobType;
import uk.gov.ons.census.common.model.entity.UserGroupAuthorisedActivityType;
import uk.gov.ons.census.common.validation.ColumnValidator;
import uk.gov.ons.census.common.validation.SampleFieldValidators;

@Component
public class JobTypeHelper {
  public String[] getExpectedColumns(JobType jobType, CollectionExercise collectionExercise) {
    switch (jobType) {
      case SAMPLE:
        ColumnValidator[] columnValidators = SampleFieldValidators.getValidators();
        return Arrays.stream(columnValidators)
            .map(columnValidator -> columnValidator.getColumnName())
            .collect(Collectors.toList())
            .toArray(new String[0]);

      case BULK_REFUSAL:
        return new String[] {"caseId", "refusalType"};

      case BULK_INVALID:
        return new String[] {"caseId", "reason"};

      default:
        // This code should be unreachable, providing we have a case for every JobType
        throw new RuntimeException(
            String.format("In getJobTypeSettings the jobType %s wasn't matched", jobType));
    }
  }

  public UserGroupAuthorisedActivityType getFileLoadPermission(JobType jobType) {
    switch (jobType) {
      case SAMPLE:
        return UserGroupAuthorisedActivityType.LOAD_SAMPLE;
      case BULK_REFUSAL:
        return UserGroupAuthorisedActivityType.LOAD_BULK_REFUSAL;
      case BULK_INVALID:
        return UserGroupAuthorisedActivityType.LOAD_BULK_INVALID;
      default:
        // This code should be unreachable, providing we have a case for every JobType
        throw new RuntimeException(
            String.format("In getJobTypeSettings the jobType %s wasn't matched", jobType));
    }
  }

  public UserGroupAuthorisedActivityType getFileViewProgressPermission(JobType jobType) {
    switch (jobType) {
      case SAMPLE:
        return UserGroupAuthorisedActivityType.VIEW_SAMPLE_LOAD_PROGRESS;
      case BULK_REFUSAL:
        return UserGroupAuthorisedActivityType.VIEW_BULK_REFUSAL_PROGRESS;
      case BULK_INVALID:
        return UserGroupAuthorisedActivityType.VIEW_BULK_INVALID_PROGRESS;
      default:
        // This code should be unreachable, providing we have a case for every JobType
        throw new RuntimeException(
            String.format("In getJobTypeSettings the jobType %s wasn't matched", jobType));
    }
  }
}
