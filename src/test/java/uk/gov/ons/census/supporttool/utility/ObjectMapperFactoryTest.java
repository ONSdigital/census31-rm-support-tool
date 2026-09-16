package uk.gov.ons.census.supporttool.utility;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import uk.gov.ons.census.common.model.entity.EventType;
import uk.gov.ons.census.supporttool.model.dto.messaging.EventHeaderDTO;

class ObjectMapperFactoryTest {
  private static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.objectMapper();
  private static final String CASE_ID = "10000000001";
  private static final OffsetDateTime EVENT_TIME = OffsetDateTime.parse("2024-06-01T10:15:30Z");
  private static final String ACTION = "CREATE";
  private static final UUID MESSAGE_ID = UUID.fromString("1d811565-41ca-4947-bf77-326ff53d881c");
  private static final UUID CORRELATION_ID =
      UUID.fromString("6ec8f90d-4f36-4747-98c2-e4ebaa9cfbd4");

  @Test
  void shouldPreserveFieldOrderAndWriteDatesAsIsoStrings() {
    CaseEvent caseEvent = new CaseEvent(CASE_ID, EVENT_TIME, ACTION);

    assertThat(OBJECT_MAPPER.writeValueAsString(caseEvent))
        .isEqualTo(
            "{\"caseId\":\"10000000001\",\"eventTime\":\"2024-06-01T10:15:30Z\",\"action\":\"CREATE\"}");
  }

  @Test
  void shouldIgnoreUnknownPropertiesWhenReadingJson() {
    CaseEvent caseEvent =
        OBJECT_MAPPER.readValue(
            "{\"caseId\":\"10000000001\",\"eventTime\":\"2024-06-01T10:15:30Z\","
                + "\"action\":\"CREATE\",\"extraField\":\"ignored\"}",
            CaseEvent.class);

    assertThat(caseEvent).isEqualTo(new CaseEvent(CASE_ID, EVENT_TIME, ACTION));
  }

  @Test
  void shouldIgnoreTrailingTokensWhenReadingJson() {
    CaseEvent caseEvent =
        OBJECT_MAPPER.readValue(
            "{\"caseId\":\"10000000001\",\"eventTime\":\"2024-06-01T10:15:30Z\","
                + "\"action\":\"CREATE\"} true",
            CaseEvent.class);

    assertThat(caseEvent).isEqualTo(new CaseEvent(CASE_ID, EVENT_TIME, ACTION));
  }

  @Test
  void shouldSerializeEventHeaderDtoContainingOffsetDateTime() {
    EventHeaderDTO eventHeaderDTO = new EventHeaderDTO();
    eventHeaderDTO.setVersion(Constants.EVENT_SCHEMA_VERSION);
    eventHeaderDTO.setTopic("event_case-update");
    eventHeaderDTO.setSource("SUPPORT_TOOL");
    eventHeaderDTO.setChannel("RM");
    eventHeaderDTO.setDateTime(EVENT_TIME);
    eventHeaderDTO.setMessageId(MESSAGE_ID);
    eventHeaderDTO.setCorrelationId(CORRELATION_ID);
    eventHeaderDTO.setOriginatingUser("test.user@example.com");
    eventHeaderDTO.setMessageType(EventType.CASE_UPDATE);

    assertThat(OBJECT_MAPPER.writeValueAsString(eventHeaderDTO))
        .isEqualTo(
            "{\"version\":\"1.0.0\",\"topic\":\"event_case-update\",\"source\":\"SUPPORT_TOOL\","
                + "\"channel\":\"RM\",\"dateTime\":\"2024-06-01T10:15:30Z\","
                + "\"messageId\":\"1d811565-41ca-4947-bf77-326ff53d881c\","
                + "\"correlationId\":\"6ec8f90d-4f36-4747-98c2-e4ebaa9cfbd4\","
                + "\"originatingUser\":\"test.user@example.com\",\"messageType\":\"CASE_UPDATE\"}");
  }

  private record CaseEvent(String caseId, OffsetDateTime eventTime, String action) {}
}
