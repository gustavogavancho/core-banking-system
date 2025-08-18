Feature: Create Transaction Helper

Scenario: Create a single transaction
  Given url 'http://localhost:8080'
  And path 'accounts', __arg.accountId, 'transactions'
  And header Accept = 'application/json'
  And header Content-Type = 'application/json'
  And request __arg.transaction
  When method POST
  Then status 201
