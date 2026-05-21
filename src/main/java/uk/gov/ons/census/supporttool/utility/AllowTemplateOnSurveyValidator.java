package uk.gov.ons.census.supporttool.utility;

import static uk.gov.ons.census.supporttool.utility.ColumnHelper.getSurveyColumns;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AllowTemplateOnSurveyValidator {
  private static final Set<String> OTHER_ALLOWABLE_COLUMNS =
      Set.of("__uac__", "__qid__", "__caseref__");

  public static Optional<String> validate(Set<String> templateColumns) {
    Set<String> surveyColumns = getSurveyColumns();
    Set<String> surveyColumnsPlusOtherAllowableColumns = new HashSet<>();

    surveyColumnsPlusOtherAllowableColumns.addAll(surveyColumns);
    surveyColumnsPlusOtherAllowableColumns.addAll(OTHER_ALLOWABLE_COLUMNS);

    // We can't validate the __request__ columns as the caller supplies them not the sample
    Set<String> templateNonRequestColumns = new HashSet<>(templateColumns);
    templateNonRequestColumns.removeIf(column -> column.startsWith("__request__."));

    if (!surveyColumnsPlusOtherAllowableColumns.containsAll(templateNonRequestColumns)) {
      Set<String> printTemplateColumnsNotAllowed = new HashSet<>(templateNonRequestColumns);
      printTemplateColumnsNotAllowed.removeAll(surveyColumnsPlusOtherAllowableColumns);
      String errorMessage =
          "Survey is missing columns: " + String.join(", ", printTemplateColumnsNotAllowed);
      return Optional.of(errorMessage);
    }

    return Optional.empty();
  }
}
