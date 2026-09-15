import React, { Component } from "react";
import "@fontsource/roboto";
import {Button, Dialog, DialogContent, MenuItem, Select} from "@material-ui/core";

class InvalidCase extends Component {
  state = {
    reason: "",
    reasonValidationError: false,
    showDialog: false,
  };

  openDialog = () => {
    this.createInProgress = false;

    this.setState({
      showDialog: true,
    });
  };

  closeDialog = () => {
    this.setState({
      reason: "",
      reasonValidationError: false,
      showDialog: false,
    });
  };

  onReasonChange = (event) => {
    this.setState({
      reason: event.target.value,
      reasonValidationError: false,
    });
  };

  onCreate = async () => {
    if (this.createInProgress) {
      return;
    }

    this.createInProgress = true;

    if (!this.state.reason) {
      this.setState({ reasonValidationError: true });

      this.createInProgress = false;
      return;
    }

    const invalidCase = {
      reason: this.state.reason,
    };

    const response = await fetch(
      `/api/cases/${this.props.caseId}/action/invalid-case`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(invalidCase),
      },
    );

    if (response.ok) {
      this.closeDialog();
    }
  };

  render() {
    return (
      <div>
        <Button
          style={{ marginTop: 10 }}
          onClick={this.openDialog}
          variant="contained"
        >
          Invalidate this case
        </Button>
        <Dialog open={this.state.showDialog}>
          <DialogContent style={{ padding: 30 }}>
            <div>
              <Select
                  onChange={this.onReasonChange}
                  value={this.state.reason}
                  error={this.state.reasonValidationError}
              >
                <MenuItem value={"SPLIT_ADDRESS"}>SPLIT ADDRESS</MenuItem>
                <MenuItem value={"DERELICT"}>DERELICT</MenuItem>
                <MenuItem value={"DEMOLISHED"}>DEMOLISHED</MenuItem>
                <MenuItem value={"CANT_FIND"}>CANT FIND</MenuItem>
                <MenuItem value={"UNADDRESSABLE_OBJECT"}>UNADDRESSABLE OBJECT</MenuItem>
                <MenuItem value={"NON_RESIDENTIAL"}>NON RESIDENTIAL</MenuItem>
                <MenuItem value={"DUPLICATE"}>DUPLICATE</MenuItem>
                <MenuItem value={"UNDER_CONSTRUCTION"}>UNDER CONSTRUCTION</MenuItem>
                <MenuItem value={"DOES_NOT_EXIST"}>DOES NOT EXIST</MenuItem>
              </Select>
            </div>
            <div style={{ marginTop: 10 }}>
              <Button
                onClick={this.onCreate}
                variant="contained"
                style={{ margin: 10 }}
              >
                Invalidate this case
              </Button>
              <Button
                onClick={this.closeDialog}
                variant="contained"
                style={{ margin: 10 }}
              >
                Cancel
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>
    );
  }
}

export default InvalidCase;
