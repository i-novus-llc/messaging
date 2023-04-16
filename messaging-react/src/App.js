import React from 'react';

import N2O from 'n2o-framework';
import authProvider from 'n2o-framework/lib/core/auth/authProvider';
import createFactoryConfig from 'n2o-framework/lib/core/factory/createFactoryConfig';

const config = {
  security: {
    authProvider,
    externalLoginUrl: '/login'
  }
};

const App = () => (
    <N2O {...createFactoryConfig(config)}  />
)

export default App;
