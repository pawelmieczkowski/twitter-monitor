import './App.scss';
import React from "react";
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";

import { ChartPage } from './pages/ChartPage';

function App() {
  return (
    <div className="App">
      <Router>
        <div className="page-content">
          <Switch>
          <Route path="/" component={ChartPage} />
          </Switch>
        </div>
      </Router>
    </div>
  );
}

export default App;
