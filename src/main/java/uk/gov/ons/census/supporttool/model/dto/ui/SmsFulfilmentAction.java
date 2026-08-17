package uk.gov.ons.census.supporttool.model.dto.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import uk.gov.ons.census.supporttool.model.dto.rest.Contact;

@Data
public class SmsFulfilmentAction {
  private String packCode;
  private String phoneNumber;
  private Object uacMetadata;
  private Map<String, String> personalisation;

  public Contact toContact() {
    Map<String, String> contactValues = toStringMap(personalisation);
    Contact contact = new Contact();

    if (contactValues != null) {
      if (contactValues.get("forename") != null) contact.setForename(contactValues.get("forename"));
      if (contactValues.get("surname") != null) contact.setSurname(contactValues.get("surname"));
      if (contactValues.get("title") != null) contact.setTitle(contactValues.get("title"));
      if (phoneNumber != null) contact.setTelNo(phoneNumber);
    }
    return contact;
  }

  public static Map<String, String> toStringMap(Object obj) {
    if (obj != null) {
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> raw =
          mapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
      return raw.entrySet().stream()
          .collect(
              Collectors.toMap(
                  Map.Entry::getKey, e -> e.getValue() == null ? null : e.getValue().toString()));
    }
    return new HashMap<String, String>();
  }
}
