package uk.gov.ons.census.supporttool.model.dto.messaging;

public enum InvalidAddressReason {
  SPLIT_ADDRESS,
  DERELICT,
  DEMOLISHED,
  CANT_FIND,
  UNADDRESSABLE_OBJECT,
  NON_RESIDENTIAL,
  DUPLICATE,
  UNDER_CONSTRUCTION,
  DOES_NOT_EXIST
}
