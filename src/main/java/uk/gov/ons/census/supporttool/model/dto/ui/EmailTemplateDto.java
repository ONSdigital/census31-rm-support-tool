package uk.gov.ons.census.supporttool.model.dto.ui;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.census.common.model.entity.EmailTemplate;

@Data
@NoArgsConstructor
public class EmailTemplateDto {
  private String packCode;
  private String[] template;
  private UUID notifyTemplateId;
  private String description;
  private Object metadata;
  private String notifyServiceRef;
  private Integer questionnaireType;

  public EmailTemplateDto(EmailTemplate emailTemplate) {
    packCode = emailTemplate.getPackCode();
    template = emailTemplate.getTemplate();
    notifyTemplateId = emailTemplate.getNotifyTemplateId();
    description = emailTemplate.getDescription();
    metadata = emailTemplate.getMetadata();
    notifyServiceRef = emailTemplate.getNotifyServiceRef();
    questionnaireType = emailTemplate.getQuestionnaireType();
  }
}
