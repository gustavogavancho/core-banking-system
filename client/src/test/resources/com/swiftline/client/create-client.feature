Feature: Create Client Helper

Scenario: Create a single client
  Given url 'http://localhost:8081'
  And path 'clients'
  And header Accept = 'application/json'
  And header Content-Type = 'application/json'
  And request __arg
  When method POST
  Then status 201
