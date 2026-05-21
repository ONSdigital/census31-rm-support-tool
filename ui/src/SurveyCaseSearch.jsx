import React, { Component } from "react";
import "@fontsource/roboto";
import { Box, CircularProgress, Paper, Typography } from "@material-ui/core";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import SurveySimpleSearchInput from "./SurveySimpleSearchInput";
import SurveySampleSearch from "./SurveySampleSearch";
import CaseDetails from "./CaseDetails";
import { Link } from "react-router-dom";
import { errorAlert } from "./Utils";

class SurveyCaseSearch extends Component {
  state = {
    authorisedActivities: [],
    caseSearchResults: [],
    collectionExercises: [],
    isWaitingForResults: false,
    caseSearchTerm: "",
    caseSearchDesc: "",
  };

  componentDidMount() {
    this.getAuthorisedBackendData();
  }

  getAuthorisedBackendData = async () => {
    const authorisedActivities = await this.getAuthorisedActivities(); // Only need to do this once; don't refresh it repeatedly as it changes infrequently
    this.getSampleColumns(authorisedActivities);
    this.getCollectionExercises(authorisedActivities);
  };

  getAuthorisedActivities = async () => {
    const response = await fetch(`/api/auth?surveyId=${this.props.surveyId}`);

    // TODO: We need more elegant error handling throughout the whole application, but this will at least protect temporarily
    const responseJson = await response.json();
    if (!response.ok) {
      errorAlert(responseJson);
      return;
    }

    this.setState({ authorisedActivities: responseJson });

    return responseJson;
  };

  getCollectionExercises = async (authorisedActivities) => {
    if (!authorisedActivities.includes("LIST_COLLECTION_EXERCISES")) return;

    const response = await fetch(
      `/api/collectionExercises?surveyId=${this.props.surveyId}`,
    );
    const collexJson = await response.json();

    this.setState({
      collectionExercises: collexJson,
    });
  };

  onSearchExecuteAndPopulateList = async (
    searchUrl,
    searchTerm,
    searchDesc,
  ) => {
    this.setState({ isWaitingForResults: true });

    const response = await fetch(searchUrl);
    this.setState({ isWaitingForResults: false });

    // TODO: We need more elegant error handling throughout the whole application, but this will at least protect temporarily
    const responseJson = await response.json();
    if (!response.ok) {
      errorAlert(responseJson);
      return;
    }

    this.setState({
      caseSearchResults: responseJson,
      caseSearchTerm: searchTerm,
      caseSearchDesc: searchDesc,
    });
  };

  checkWhitespace = (valueToValidate) => {
    return valueToValidate.trim();
  };

  isNumeric = (str) => {
    return /^\+?\d+$/.test(str);
  };


  getCaseCells = (caze) => {
    const caseId = caze.id;
    let caseCells = [];
    caseCells.push(
      <TableCell key={0}>
        {this.state.authorisedActivities.includes("VIEW_CASE_DETAILS") && (
          <Link to={`/search?surveyId=${this.props.surveyId}&caseId=${caseId}`}>
            {caze.caseRef}
          </Link>
        )}
        {!this.state.authorisedActivities.includes("VIEW_CASE_DETAILS") && (
          <>{caze.caseRef}</>
        )}
      </TableCell>,
    );
    caseCells.push(
      <TableCell key={1}>{caze.collectionExerciseName}</TableCell>,
    );

    return caseCells;
  };

  getTableHeaderRows() {
    let tableHeaderRows = [];
    tableHeaderRows.push(<TableCell key={0}>Case Ref</TableCell>);

    tableHeaderRows.push(<TableCell key={1}>Collection Exercise</TableCell>);

    return tableHeaderRows;
  }

  render() {
    const tableHeaderRows = this.getTableHeaderRows();

    const caseTableRows = this.state.caseSearchResults.map((caze, index) => (
      <TableRow key={index}>{this.getCaseCells(caze)}</TableRow>
    ));

    const borderStyles = {
      border: "1px solid grey",
      marginTop: "10px",
      paddingBottom: "10px",
    };

    const searchFragment = (
      <div>
        <Link to={`/survey?surveyId=${this.props.surveyId}`}>
          ← Back to survey
        </Link>
        <Typography variant="h4" color="inherit" style={{ marginBottom: 10 }}>
          Search Cases
        </Typography>

        <div style={borderStyles}>
          <SurveySampleSearch
            style={borderStyles}
            surveyId={this.props.surveyId}
            onSearchExecuteAndPopulateList={this.onSearchExecuteAndPopulateList}
            searchTermValidator={this.checkWhitespace}
            collectionExercises={this.state.collectionExercises}
          />
        </div>

        <div style={borderStyles}>
          <SurveySimpleSearchInput
            style={borderStyles}
            surveyId={this.props.surveyId}
            onSearchExecuteAndPopulateList={this.onSearchExecuteAndPopulateList}
            searchTermValidator={this.isNumeric}
            urlpathName="caseRef"
            displayText="Search By Case Ref"
            searchDesc="Case Ref matching"
          />
        </div>

        <div style={borderStyles}>
          <SurveySimpleSearchInput
            style={borderStyles}
            surveyId={this.props.surveyId}
            onSearchExecuteAndPopulateList={this.onSearchExecuteAndPopulateList}
            searchTermValidator={this.isNumeric}
            urlpathName="qid"
            displayText="Search By Qid"
            searchDesc="cases linked to QID"
          />
        </div>
        {this.state.isWaitingForResults && (
          <Box
            margin={4}
            align="center"
            display="flex"
            alignItems="center"
            justifyContent="center"
          >
            <CircularProgress color="inherit" />
          </Box>
        )}
        {this.state.caseSearchTerm && !this.state.isWaitingForResults && (
          <Typography
            variant="h5"
            color="inherit"
            style={{ marginTop: 30, marginBottom: 10 }}
          >
            Results for {this.state.caseSearchDesc} "{this.state.caseSearchTerm}
            ":
          </Typography>
        )}
        {this.state.caseSearchTerm &&
          !this.state.isWaitingForResults &&
          this.state.caseSearchResults.length === 100 && (
            <Typography
              variant="h6"
              color="inherit"
              style={{ marginTop: 10, marginBottom: 10 }}
            >
              Search results are limited to 100, so there may be more matching
              cases
            </Typography>
          )}
        {!this.state.caseSearchTerm && !this.state.isWaitingForResults && (
          <Typography
            variant="h5"
            color="inherit"
            style={{ marginTop: 30, marginBottom: 10 }}
          >
            Make a search
          </Typography>
        )}
        {this.state.caseSearchTerm &&
          !this.state.isWaitingForResults &&
          this.state.caseSearchResults.length > 0 && (
            <TableContainer component={Paper} style={{ marginTop: 20 }}>
              <Table>
                <TableHead>
                  <TableRow>{tableHeaderRows}</TableRow>
                </TableHead>
                <TableBody>{caseTableRows}</TableBody>
              </Table>
            </TableContainer>
          )}
        {this.state.caseSearchTerm &&
          !this.state.isWaitingForResults &&
          !this.state.caseSearchResults.length > 0 && <p>No cases found</p>}
      </div>
    );

    return (
      <div style={{ padding: 20 }}>
        {this.props.caseId && (
          <CaseDetails
            surveyId={this.props.surveyId}
            caseId={this.props.caseId}
          />
        )}
        {!this.props.caseId && searchFragment}
      </div>
    );
  }
}

export default SurveyCaseSearch;
