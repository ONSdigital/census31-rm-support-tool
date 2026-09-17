package uk.gov.ons.census.supporttool.utility;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

public class ObjectMapperFactory {
  public static ObjectMapper objectMapper() {

    return JsonMapper.builder()
        .findAndAddModules()
        // Jackson 3 turns alphabetical property sorting ON by default.
        // This mapper feeds JacksonPubSubMessageConverter, so leaving it on
        // would reorder the properties in every event published to all nine
        // topics in queueconfig. Frozen deliberately.
        .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
        .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
        .build();
  }
}
