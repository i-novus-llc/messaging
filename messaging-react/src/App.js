import React, { Component } from 'react';

import { BrowserRouter } from "react-router-dom";

import N2O from 'n2o-framework';
import { authProvider } from 'n2o-auth';
import createFactoryConfigLight from "n2o-framework/lib/core/factory/createFactoryConfigLight";

const config = {
  security: {
    authProvider,
    externalLoginUrl: '/login'
  }
};

class App extends Component {
  render() {
    return (
        <BrowserRouter>
          <N2O {...createFactoryConfigLight(config)}  />
        </BrowserRouter>
    );
  }
}

export default App;
