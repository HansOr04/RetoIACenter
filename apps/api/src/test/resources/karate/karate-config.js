function fn() {
  var env = karate.env || 'local';
  var config = {
    baseUrl: 'http://localhost:8080'
  };
  if (env === 'ci') {
    config.baseUrl = 'http://api:8080';
  }
  return config;
}
