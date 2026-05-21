package uk.gov.ons.census.supporttool.utility;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import uk.gov.ons.census.common.validation.SampleFieldValidators;

public class ColumnHelper {
  public static Set<String> getSurveyColumns() {
    return Arrays.stream(SampleFieldValidators.getValidators())
        .map(columnValidator -> columnValidator.getColumnName())
        .collect(Collectors.toSet());
  }
}
