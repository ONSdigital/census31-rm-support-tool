package uk.gov.ons.census.supporttool.utility;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import uk.gov.ons.census.supporttool.model.dto.rest.Contact;
import uk.gov.ons.census.supporttool.model.dto.ui.SmsFulfilmentAction;

public class SmsFulfilmentActionTest {

  void testToContact_AllFieldsPresent() {
    SmsFulfilmentAction action = new SmsFulfilmentAction();
    action.setPhoneNumber("07700900123");
    action.setPersonalisation(
        Map.of(
            "forename", "John",
            "surname", "Doe",
            "title", "Mr"));

    Contact contact = action.toContact();

    assertEquals("John", contact.getForename());
    assertEquals("Doe", contact.getSurname());
    assertEquals("Mr", contact.getTitle());
    assertEquals("07700900123", contact.getTelNo());
  }

  void testToContact_MissingSomeFields() {
    SmsFulfilmentAction action = new SmsFulfilmentAction();
    action.setPhoneNumber("07700900123");
    action.setPersonalisation(
        Map.of(
            "forename", "Alice"
            // surname missing
            // title missing
            ));

    Contact contact = action.toContact();

    assertEquals("Alice", contact.getForename());
    assertNull(contact.getSurname());
    assertNull(contact.getTitle());
    assertEquals("07700900123", contact.getTelNo());
  }

  void testToContact_NullPersonalisation() {
    SmsFulfilmentAction action = new SmsFulfilmentAction();
    action.setPhoneNumber("07700900123");
    action.setPersonalisation(null);

    Contact contact = action.toContact();

    assertNull(contact.getForename());
    assertNull(contact.getSurname());
    assertNull(contact.getTitle());
    assertNull(contact.getTelNo()); // phoneNumber only set if personalisation != null
  }

  void testToStringMap_ConvertsObjectToStringMap() {
    Map<String, Object> input = Map.of("key1", 123, "key2", true, "key3", "value");

    Map<String, String> result = SmsFulfilmentAction.toStringMap(input);

    assertEquals("123", result.get("key1"));
    assertEquals("true", result.get("key2"));
    assertEquals("value", result.get("key3"));
  }

  void testToStringMap_NullObjectReturnsEmptyMap() {
    Map<String, String> result = SmsFulfilmentAction.toStringMap(null);
    assertTrue(result.isEmpty());
  }
}
