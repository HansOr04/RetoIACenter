function fn() {
  var env = karate.env || 'local'
  karate.log('Environment:', env)

  var config = {
    env: env,
    baseUrl: 'http://localhost:8080',
    coreUrl: 'http://localhost:4000'
  }

  if (env === 'docker') {
    config.baseUrl = 'http://api:8080'
    config.coreUrl = 'http://core-stub:4000'
  }

  if (env === 'ci') {
    config.baseUrl = karate.properties['api.base.url'] || 'http://localhost:8080'
    config.coreUrl = karate.properties['core.base.url'] || 'http://localhost:4000'
  }

  karate.configure('connectTimeout', 5000)
  karate.configure('readTimeout', 10000)
  karate.configure('ssl', true)
  karate.configure('logPrettyRequest', true)
  karate.configure('logPrettyResponse', true)

  return config
}
