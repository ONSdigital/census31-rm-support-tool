package uk.gov.ons.census.supporttool.endpoint;

import static uk.gov.ons.census.common.model.entity.UserGroupAuthorisedActivityType.LIST_EMAIL_TEMPLATES;

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
@RequestMapping(value = "/api/notifyServiceRefs")
public class NotifyServiceRefEndpoint {

  public static final ObjectMapper OBJECT_MAPPER = ObjectMapperFactory.objectMapper();

  private final AuthUser authUser;

  @Value("${notifyserviceconfigfile}")
  private String configFile;

  private Set<String> notifyServiceRefs = null;

  public NotifyServiceRefEndpoint(AuthUser authUser) {
    this.authUser = authUser;
  }

  @GetMapping
  public Set<String> getNotifyServiceRefs(
      @Value("#{request.getAttribute('userEmail')}") String userEmail) {
    authUser.checkGlobalUserPermission(userEmail, LIST_EMAIL_TEMPLATES);

    if (notifyServiceRefs != null) {
      return notifyServiceRefs;
    }

    try (InputStream configFileStream = new FileInputStream(configFile)) {
      Map map = OBJECT_MAPPER.readValue(configFileStream, Map.class);
      notifyServiceRefs = map.keySet();
      return notifyServiceRefs;
    } catch (JacksonException | FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
