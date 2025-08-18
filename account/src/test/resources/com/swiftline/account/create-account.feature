Feature: Create Account Helper

Scenario: Create a single account
  Given url 'http://localhost:8080'
  And path 'accounts'
  And header Accept = 'application/json'
  And header Content-Type = 'application/json'
  And request __arg
  When method POST
  Then status 201
