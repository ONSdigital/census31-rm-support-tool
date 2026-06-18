package uk.gov.ons.census.supporttool.endpoint;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.census.common.model.entity.Survey;
import uk.gov.ons.census.common.model.entity.UserGroupAuthorisedActivityType;
import uk.gov.ons.census.supporttool.model.dto.ui.UIRefusalTypeDTO;
import uk.gov.ons.census.supporttool.model.repository.SurveyRepository;
import uk.gov.ons.census.supporttool.security.AuthUser;
import uk.gov.ons.census.supporttool.utility.CaseSearchResultsMapper;

class SurveyCasesEndpointIT {

  private SurveyRepository surveyRepository;
  private NamedParameterJdbcTemplate jdbc;
  private CaseSearchResultsMapper mapper;
  private AuthUser authUser;

  private SurveyCasesEndpoint endpoint;

  private final UUID SURVEY_ID = UUID.randomUUID();

  @BeforeEach
  void setup() {
    surveyRepository = mock(SurveyRepository.class);
    jdbc = mock(NamedParameterJdbcTemplate.class);
    mapper = mock(CaseSearchResultsMapper.class);
    authUser = mock(AuthUser.class);

    endpoint = new SurveyCasesEndpoint(surveyRepository, jdbc, mapper, authUser);
  }

  // -------------------------------------------------------------------------
  // PERMISSION CHECKS
  // -------------------------------------------------------------------------
  @Test
  void searchCasesByColumnNameTerm_throws404_whenSurveyNotFound() {
    when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.empty());

    assertThrows(
        ResponseStatusException.class,
        () ->
            endpoint.searchCasesByColumnNameTerm(
                "user@test.com",
                SURVEY_ID,
                "AB12",
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                "postcode"));
  }

  @Test
  void searchCasesByColumnNameTerm_callsPermissionCheck() {
    Survey survey = new Survey();
    survey.setId(SURVEY_ID);

    when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(survey));
    when(jdbc.query(anyString(), anyMap(), eq(mapper))).thenReturn(Collections.emptyList());

    endpoint.searchCasesByColumnNameTerm(
        "user@test.com",
        SURVEY_ID,
        "AB12",
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        "postcode");

    verify(authUser)
        .checkUserPermission(
            "user@test.com", SURVEY_ID, UserGroupAuthorisedActivityType.SEARCH_CASES);
  }

  // -------------------------------------------------------------------------
  // QUERY BUILDING — BASE CASE
  // -------------------------------------------------------------------------
  @Test
  void searchCasesByColumnNameTerm_buildsCorrectQuery() {
    Survey survey = new Survey();
    survey.setId(SURVEY_ID);

    when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(survey));
    when(jdbc.query(anyString(), anyMap(), eq(mapper))).thenReturn(Collections.emptyList());

    endpoint.searchCasesByColumnNameTerm(
        "user@test.com",
        SURVEY_ID,
        "AB12",
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        "postcode");

    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

    verify(jdbc).query(queryCaptor.capture(), paramsCaptor.capture(), eq(mapper));

    String query = queryCaptor.getValue();

    Map<String, Object> params = paramsCaptor.getValue();

    assertTrue(query.contains("FROM cases.cases c"));
    assertTrue(
        query.contains(
            "AND LOWER(REPLACE(c.postcode, ' ', ''))  LIKE LOWER(REPLACE(:likeSearchTerm, ' ', ''))"));

    assertTrue(query.contains("LIMIT 100"));

    assertEquals("%ab12%", params.get("likeSearchTerm"));
    assertEquals(SURVEY_ID, params.get("surveyId"));
  }

  // -------------------------------------------------------------------------
  // QUERY BUILDING — OPTIONAL PARAMETERS
  // -------------------------------------------------------------------------
  @Test
  void searchCasesByColumnNameTerm_withCollexId() {
    Survey survey = new Survey();
    survey.setId(SURVEY_ID);

    UUID collexId = UUID.randomUUID();

    when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(survey));
    when(jdbc.query(anyString(), anyMap(), eq(mapper))).thenReturn(Collections.emptyList());

    endpoint.searchCasesByColumnNameTerm(
        "user@test.com",
        SURVEY_ID,
        "AB12",
        Optional.of(collexId),
        Optional.empty(),
        Optional.empty(),
        "postcode");

    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

    verify(jdbc).query(queryCaptor.capture(), paramsCaptor.capture(), eq(mapper));

    assertTrue(queryCaptor.getValue().contains("AND e.id = :collexId"));
    assertEquals(collexId, paramsCaptor.getValue().get("collexId"));
  }

  @Test
  void searchCasesByColumnNameTerm_withCaseInvalid() {
    Survey survey = new Survey();
    survey.setId(SURVEY_ID);

    when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(survey));
    when(jdbc.query(anyString(), anyMap(), eq(mapper))).thenReturn(Collections.emptyList());

    endpoint.searchCasesByColumnNameTerm(
        "user@test.com",
        SURVEY_ID,
        "AB12",
        Optional.empty(),
        Optional.of(true),
        Optional.empty(),
        "postcode");

    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    verify(jdbc).query(queryCaptor.capture(), anyMap(), eq(mapper));

    assertTrue(queryCaptor.getValue().contains("AND c.invalid = :caseInvalid"));
  }

  @Test
  void searchCasesByColumnNameTerm_withRefusalReceived_notRefused() {
    Survey survey = new Survey();
    survey.setId(SURVEY_ID);

    when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(survey));
    when(jdbc.query(anyString(), anyMap(), eq(mapper))).thenReturn(Collections.emptyList());

    endpoint.searchCasesByColumnNameTerm(
        "user@test.com",
        SURVEY_ID,
        "AB12",
        Optional.empty(),
        Optional.empty(),
        Optional.of(UIRefusalTypeDTO.NOT_REFUSED),
        "postcode");

    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    verify(jdbc).query(queryCaptor.capture(), anyMap(), eq(mapper));

    assertTrue(queryCaptor.getValue().contains("c.refusal_received IS NULL"));
  }

  @Test
  void searchCasesByColumnNameTerm_withRefusalReceived_specificType() {
    Survey survey = new Survey();
    survey.setId(SURVEY_ID);

    when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(survey));
    when(jdbc.query(anyString(), anyMap(), eq(mapper))).thenReturn(Collections.emptyList());

    endpoint.searchCasesByColumnNameTerm(
        "user@test.com",
        SURVEY_ID,
        "AB12",
        Optional.empty(),
        Optional.empty(),
        Optional.of(UIRefusalTypeDTO.HARD_REFUSAL),
        "postcode");

    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);

    verify(jdbc).query(queryCaptor.capture(), paramsCaptor.capture(), eq(mapper));

    assertTrue(queryCaptor.getValue().contains("c.refusal_received = :refusalReceived"));
    assertEquals("HARD_REFUSAL", paramsCaptor.getValue().get("refusalReceived"));
  }

  // -------------------------------------------------------------------------
  // GET CASE BY CASE REF
  // -------------------------------------------------------------------------
  @Test
  void getCaseByCaseRef_buildsCorrectQuery() {
    Survey survey = new Survey();
    survey.setId(SURVEY_ID);

    when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(survey));
    when(jdbc.query(anyString(), anyMap(), eq(mapper))).thenReturn(Collections.emptyList());

    endpoint.getCaseByCaseRef(SURVEY_ID, 12345L, "user@test.com");

    verify(jdbc).query(argThat(q -> q.contains("c.case_ref = :caseRef")), anyMap(), eq(mapper));
  }

  // -------------------------------------------------------------------------
  // GET CASE BY QID
  // -------------------------------------------------------------------------
  @Test
  void getCaseByQid_buildsCorrectQuery() {
    Survey survey = new Survey();
    survey.setId(SURVEY_ID);

    when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(survey));
    when(jdbc.query(anyString(), anyMap(), eq(mapper))).thenReturn(Collections.emptyList());

    endpoint.getCaseByQid(SURVEY_ID, "Q123", "user@test.com");

    verify(jdbc).query(argThat(q -> q.contains("u.qid = :qid")), anyMap(), eq(mapper));
  }

  @Test
  void searchCasesByColumnNameTerm_invalidNameTerm_throwsBadRequest() {
    Survey survey = new Survey();
    survey.setId(SURVEY_ID);

    when(surveyRepository.findById(SURVEY_ID)).thenReturn(Optional.of(survey));

    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class,
            () ->
                endpoint.searchCasesByColumnNameTerm(
                    "user@test.com",
                    SURVEY_ID,
                    "AB12",
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    "invalid_column" // <-- invalid
                    ));

    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertNotNull(ex.getReason());
    assertTrue(ex.getReason().contains("Invalid nameTerm"));
  }
}
