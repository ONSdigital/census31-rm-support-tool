package uk.gov.ons.census.supporttool.model.dto.ui;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.census.common.model.entity.SmsTemplate;

@Data
@NoArgsConstructor
public class SmsTemplateDto {
  private String packCode;
  private String[] template;
  private UUID notifyTemplateId;
  private String description;
  private Object metadata;
  private String notifyServiceRef;
  private Integer questionnaireType;

  public SmsTemplateDto(SmsTemplate smsTemplate) {
    packCode = smsTemplate.getPackCode();
    template = smsTemplate.getTemplate();
    notifyTemplateId = smsTemplate.getNotifyTemplateId();
    description = smsTemplate.getDescription();
    metadata = smsTemplate.getMetadata();
    notifyServiceRef = smsTemplate.getNotifyServiceRef();
    questionnaireType = smsTemplate.getQuestionnaireType();
  }
}
