import React, { Component } from "react";
import "@fontsource/roboto";
import {
  Paper,
  Typography,
} from "@material-ui/core";
import { Link } from "react-router-dom";
import CollectionExerciseList from "./CollectionExerciseList";
import AllowedExportFileTemplatesActionRulesList from "./AllowedExportFileTemplatesActionRulesList";
import AllowedSMSTemplatesActionRulesList from "./AllowedSMSTemplatesActionRulesList";
import AllowedEmailTemplatesOnActionRulesList from "./AllowedEmailTemplatesOnActionRulesList";
import AllowedExportFileTemplatesOnFulfilmentsList from "./AllowedExportFileTemplatesOnFulfilmentsList";
import AllowedSMSTemplatesOnFulfilmentsList from "./AllowedSMSTemplatesOnFulfilmentsList";
import AllowedEmailTemplatesOnFulfilments from "./AllowedEmailTemplatesOnFulfilments";
import { errorAlert } from "./Utils";
import TableContainer from "@material-ui/core/TableContainer";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";

class SurveyDetails extends Component {
  state = {
    authorisedActivities: [],
    surveyDetails: [],
  };

  componentDidMount() {
    this.getAuthorisedBackendData();
  }

  getAuthorisedBackendData = async () => {
    const authorisedActivities = await this.getAuthorisedActivities(); // Only need to do this once; don't refresh it repeatedly as it changes infrequently
    this.getSurveyDetails(authorisedActivities); // Only need to do this once; don't refresh it repeatedly as it changes infrequently
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

  getSurveyDetails = async (authorisedActivities) => {
    if (!authorisedActivities.includes("VIEW_SURVEY")) return;

    const response = await fetch(`/api/surveys/${this.props.surveyId}`);

    const surveyJson = await response.json();

    this.setState({ surveyDetails: surveyJson });
  };

  render() {
    const surveyDetailsRow = (
      <TableRow>
        <TableCell component="th" scope="row">
          {this.state.surveyDetails.id}
        </TableCell>
        <TableCell component="th" scope="row">
          {JSON.stringify(this.state.surveyDetails.metadata)}
        </TableCell>
        <TableCell component="th" scope="row">
          {this.state.surveyDetails.sampleWithHeaderRow ? "YES" : "NO"}
        </TableCell>
        <TableCell component="th" scope="row">
          {this.state.surveyDetails.sampleSeparator}
        </TableCell>
      </TableRow>
    );

    return (
      <div style={{ padding: 20 }}>
        <Link to="/">← Back to home</Link>
        <Typography variant="h4" color="inherit" style={{ marginBottom: 20 }}>
          Survey: {this.state.surveyDetails.name}
        </Typography>
        {this.state.authorisedActivities.includes("SEARCH_CASES") && (
          <div style={{ marginBottom: 20 }}>
            <Link to={`/search?surveyId=${this.props.surveyId}`}>
              Search cases
            </Link>
          </div>
        )}
        {this.state.authorisedActivities.includes("VIEW_SURVEY") && (
          <div>
            <Typography variant="h6" color="inherit">
              Survey Details
            </Typography>
            <TableContainer component={Paper}>
              <Table id="surveyDetailsTable">
                <TableHead>
                  <TableRow>
                    <TableCell>ID</TableCell>
                    <TableCell>Metadata</TableCell>
                    <TableCell>Sample With Header Row</TableCell>
                    <TableCell>Sample Separator</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>{surveyDetailsRow}</TableBody>
              </Table>
            </TableContainer>
          </div>
        )}
        <CollectionExerciseList surveyId={this.props.surveyId} />
        <AllowedExportFileTemplatesActionRulesList
          surveyId={this.props.surveyId}
        />
        <AllowedSMSTemplatesActionRulesList surveyId={this.props.surveyId} />
        <AllowedEmailTemplatesOnActionRulesList
          surveyId={this.props.surveyId}
        />
        <AllowedExportFileTemplatesOnFulfilmentsList
          surveyId={this.props.surveyId}
        />
        <AllowedSMSTemplatesOnFulfilmentsList surveyId={this.props.surveyId} />
        <AllowedEmailTemplatesOnFulfilments surveyId={this.props.surveyId} />
      </div>
    );
  }
}

export default SurveyDetails;
