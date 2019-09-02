import React, { Component } from 'react';
import N2O from 'n2o-framework';
import { authProvider } from 'n2o-auth';

const config = {
  security: {
    authProvider,
    externalLoginUrl: '/login'
  }
};

class App extends Component {
  render() {
    return (
        <N2O {...config}  />
    );
  }
}

export default App;
