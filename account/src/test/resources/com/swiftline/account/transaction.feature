Feature: Transaction API - Simple Tests

Background:
  * url 'http://localhost:8080'
  * header Accept = 'application/json'
  * header Content-Type = 'application/json'

Scenario: Get non-existent transaction should return 404
  Given path 'transactions', 99999
  When method GET
  Then status 404
