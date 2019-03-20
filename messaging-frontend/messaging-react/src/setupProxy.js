const proxy = require('http-proxy-middleware');

module.exports = function(app) {
  // app.get('/n2o/config', (req, res) => {
  //   const json = require('./json/config.json');
  //   res.setHeader('Content-Type', 'application/json');
  //   res.send(JSON.stringify(json));
  // });

  // app.get('/n2o/page/payments', (req, res) => {
  //   const json = require('./json/card.json');
  //   res.setHeader('Content-Type', 'application/json');
  //   res.send(JSON.stringify(json));
  // });

  app.use(
    proxy('/n2o', {
      target: 'http://localhost:8081/',
      changeOrigin: true,
    })
  );
};
