function fn() {
  var env = karate.env; // get system property 'karate.env'
  karate.log('karate.env system property was:', env);

  if (!env) {
    env = 'dev';
  }

  var config = {
    env: env,
    baseUrl: 'http://localhost',
    clientPort: 8081,
    accountPort: 8080
  }

  if (env == 'dev') {
    config.clientBaseUrl = 'http://localhost:8081';
    config.accountBaseUrl = 'http://localhost:8080';
  } else if (env == 'test') {
    config.clientBaseUrl = 'http://localhost:8081';
    config.accountBaseUrl = 'http://localhost:8080';
  }

  karate.configure('connectTimeout', 5000);
  karate.configure('readTimeout', 5000);

  return config;
}

