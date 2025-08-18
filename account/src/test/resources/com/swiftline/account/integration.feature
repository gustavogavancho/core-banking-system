Feature: Integration Tests - Client and Account Services

Background:
  * def clientBaseUrl = 'http://localhost:8081'
  * def accountBaseUrl = 'http://localhost:8080'
  * header Accept = 'application/json'
  * header Content-Type = 'application/json'

Scenario: Complete workflow - Create client and accounts with transactions
  # Paso 1: Crear un cliente
  Given url clientBaseUrl
  And path 'clients'
  And request
    """
    {
      "name": "Integration Test User",
      "gender": "M",
      "age": 35,
      "identification": "INT123456",
      "address": "Test Address 123",
      "phoneNumber": "+57 300 123 4567",
      "password": "testpass123",
      "status": true
    }
    """
  When method POST
  Then status 201
  And match response.name == 'Integration Test User'
  * def clientId = response.id

  # Paso 2: Crear una cuenta de ahorros para el cliente
  Given url accountBaseUrl
  And path 'accounts'
  And request
    """
    {
      "accountNumber": "SAV-INT-001",
      "accountType": "SAVINGS",
      "initialBalance": 5000.00,
      "status": true,
      "clientId": #(clientId)
    }
    """
  When method POST
  Then status 201
  And match response.accountType == 'SAVINGS'
  And match response.clientId == clientId
  * def savingsAccountId = response.id

  # Paso 3: Crear una cuenta corriente para el mismo cliente
  Given url accountBaseUrl
  And path 'accounts'
  And request
    """
    {
      "accountNumber": "CHK-INT-001",
      "accountType": "CHECKING",
      "initialBalance": 2000.00,
      "status": true,
      "clientId": #(clientId)
    }
    """
  When method POST
  Then status 201
  And match response.accountType == 'CHECKING'
  And match response.clientId == clientId
  * def checkingAccountId = response.id

  # Paso 4: Realizar transacciones en la cuenta de ahorros
  Given url accountBaseUrl
  And path 'accounts', savingsAccountId, 'transactions'
  And request
    """
    {
      "date": "2024-01-20T10:00:00",
      "transactionType": "DEPOSIT",
      "amount": 1000.00
    }
    """
  When method POST
  Then status 201
  And match response.transactionType == 'DEPOSIT'
  And match response.amount == 1000.00

  # Paso 5: Realizar retiro en cuenta corriente
  Given url accountBaseUrl
  And path 'accounts', checkingAccountId, 'transactions'
  And request
    """
    {
      "date": "2024-01-20T11:30:00",
      "transactionType": "WITHDRAWAL",
      "amount": 500.00
    }
    """
  When method POST
  Then status 201
  And match response.transactionType == 'WITHDRAWAL'
  And match response.amount == 500.00

  # Paso 6: Verificar las transacciones de la cuenta de ahorros
  Given url accountBaseUrl
  And path 'accounts', savingsAccountId, 'transactions'
  When method GET
  Then status 200
  And match response == '#[1]'
  And match response[0].transactionType == 'DEPOSIT'

  # Paso 7: Verificar las transacciones de la cuenta corriente
  Given url accountBaseUrl
  And path 'accounts', checkingAccountId, 'transactions'
  When method GET
  Then status 200
  And match response == '#[1]'
  And match response[0].transactionType == 'WITHDRAWAL'

Scenario: Validate client existence before account creation
  # Intentar crear una cuenta para un cliente que no existe
  Given url accountBaseUrl
  And path 'accounts'
  And request
    """
    {
      "accountNumber": "INVALID-CLIENT-001",
      "accountType": "SAVINGS",
      "initialBalance": 1000.00,
      "status": true,
      "clientId": 99999
    }
    """
  When method POST
  Then status 404

Scenario: Multiple accounts for same client validation
  # Crear un cliente
  Given url clientBaseUrl
  And path 'clients'
  And request
    """
    {
      "name": "Multi Account User",
      "gender": "F",
      "age": 28,
      "identification": "MULTI123",
      "address": "Multi Address 456",
      "phoneNumber": "+57 300 999 8888",
      "password": "multipass",
      "status": true
    }
    """
  When method POST
  Then status 201
  * def clientId = response.id

  # Crear múltiples cuentas para el mismo cliente
  Given url accountBaseUrl
  And path 'accounts'
  And request
    """
    {
      "accountNumber": "MULTI-SAV-001",
      "accountType": "SAVINGS",
      "initialBalance": 3000.00,
      "status": true,
      "clientId": #(clientId)
    }
    """
  When method POST
  Then status 201

  Given url accountBaseUrl
  And path 'accounts'
  And request
    """
    {
      "accountNumber": "MULTI-CHK-001",
      "accountType": "CHECKING",
      "initialBalance": 1500.00,
      "status": true,
      "clientId": #(clientId)
    }
    """
  When method POST
  Then status 201

  Given url accountBaseUrl
  And path 'accounts'
  And request
    """
    {
      "accountNumber": "MULTI-SAV-002",
      "accountType": "SAVINGS",
      "initialBalance": 5000.00,
      "status": true,
      "clientId": #(clientId)
    }
    """
  When method POST
  Then status 201

  # Verificar que todas las cuentas se crearon correctamente
  Given url accountBaseUrl
  And path 'accounts'
  When method GET
  Then status 200
  And match response == '#[]'
