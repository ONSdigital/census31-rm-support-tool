package uk.gov.ons.census.supporttool.utility;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class JsonHelper {
  private static final ObjectMapper objectMapper = ObjectMapperFactory.objectMapper();

  public static String convertObjectToJson(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JacksonException e) {
      throw new RuntimeException("Failed converting Object To Json", e);
    }
  }
}
