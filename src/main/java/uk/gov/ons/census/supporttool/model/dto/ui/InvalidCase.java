package uk.gov.ons.census.supporttool.model.dto.ui;

import lombok.Data;
import uk.gov.ons.census.supporttool.model.dto.messaging.InvalidAddressReason;

@Data
public class InvalidCase {
  private InvalidAddressReason reason;
}
