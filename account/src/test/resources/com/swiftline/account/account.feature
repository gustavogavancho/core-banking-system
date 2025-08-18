Feature: Account API - Simple Tests

Background:
  * url 'http://localhost:8080'
  * header Accept = 'application/json'
  * header Content-Type = 'application/json'

Scenario: Get all accounts
  Given path 'accounts'
  When method GET
  Then status 200
  And match response == '#[]'
