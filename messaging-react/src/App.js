import React from 'react';

import N2O from 'n2o-framework';
import {authProvider} from 'n2o-auth';

const config = {
  security: {
    authProvider,
    externalLoginUrl: '/login'
  }
};

const App = () => (
    <N2O {...createFactoryConfigLight(config)}  />
)

export default App;
