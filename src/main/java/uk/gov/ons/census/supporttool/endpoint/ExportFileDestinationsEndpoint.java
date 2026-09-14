package uk.gov.ons.census.supporttool.endpoint;

import static uk.gov.ons.census.common.model.entity.UserGroupAuthorisedActivityType.LIST_EXPORT_FILE_DESTINATIONS;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import uk.gov.ons.census.supporttool.security.AuthUser;
import uk.gov.ons.census.supporttool.utility.ObjectMapperFactory;

@RestController
@RequestMapping(value = "/api/exportFileDestinations")
public class ExportFileDestinationsEndpoint {

  public static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.objectMapper();

  private final AuthUser authUser;

  @Value("${exportfiledestinationconfigfile}")
  private String configFile;

  private Set<String> exportFileDestinations = null;

  public ExportFileDestinationsEndpoint(AuthUser authUser) {
    this.authUser = authUser;
  }

  @GetMapping
  public Set<String> getExportFileDestinations(
      @Value("#{request.getAttribute('userEmail')}") String userEmail) {
    authUser.checkGlobalUserPermission(userEmail, LIST_EXPORT_FILE_DESTINATIONS);

    if (exportFileDestinations != null) {
      return exportFileDestinations;
    }

    try (InputStream configFileStream = new FileInputStream(configFile)) {
      Map map = OBJECT_MAPPER.readValue(configFileStream, Map.class);
      exportFileDestinations = map.keySet();
      return exportFileDestinations;
    } catch (JacksonException | FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
