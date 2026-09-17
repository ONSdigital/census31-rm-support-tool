package uk.gov.ons.census.supporttool.model.dto.ui;

import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import uk.gov.ons.census.supporttool.model.dto.rest.Contact;

@Data
public class PrintFulfilment {
  private String packCode;
  private Object uacMetadata;
  private Object personalisation;

  public Contact toContact() {
    Map<String, String> contactValues = toStringMap(personalisation);
    Contact contact = new Contact();

    if (contactValues != null) {
      if (contactValues.get("forename") != null) contact.setForename(contactValues.get("forename"));
      if (contactValues.get("surname") != null) contact.setSurname(contactValues.get("surname"));
      if (contactValues.get("title") != null) contact.setTitle(contactValues.get("title"));
    }
    return contact;
  }

  public static Map<String, String> toStringMap(Object obj) {
    final ObjectMapper mapper = JsonMapper.builder().build();
    Map<String, Object> raw = mapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    return raw.entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey, e -> e.getValue() == null ? null : e.getValue().toString()));
  }
}
