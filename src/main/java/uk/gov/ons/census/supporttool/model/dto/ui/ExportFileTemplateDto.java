package uk.gov.ons.census.supporttool.model.dto.ui;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.census.common.model.entity.ExportFileTemplate;

@Data
@NoArgsConstructor
public class ExportFileTemplateDto {
  private String packCode;
  private String[] template;
  private String exportFileDestination;
  private String description;
  private Object metadata;
  private Integer questionnaireType;
  private Integer welshQuestionnaireType;

  public ExportFileTemplateDto(ExportFileTemplate exportFileTemplate) {
    packCode = exportFileTemplate.getPackCode();
    template = exportFileTemplate.getTemplate();
    exportFileDestination = exportFileTemplate.getExportFileDestination();
    description = exportFileTemplate.getDescription();
    metadata = exportFileTemplate.getMetadata();
    questionnaireType = exportFileTemplate.getQuestionnaireType();
    welshQuestionnaireType = exportFileTemplate.getWelshQuestionnaireType();
  }
}
