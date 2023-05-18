const proxy = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(
    proxy('/n2o', {
      target: 'https://7-23.n2oapp.net/sandbox/view/prhbC/',
      changeOrigin: true,
    })
  );

  app.use(
    proxy('/login', {
      target: 'http://localhost:8080/',
      changeOrigin: true,
    })
  );
};
