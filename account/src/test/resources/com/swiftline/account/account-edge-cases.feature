Feature: Account API Edge Cases and Error Handling

Background:
  * url 'http://localhost:8080'
  * header Accept = 'application/json'
  * header Content-Type = 'application/json'

Scenario: Create account with duplicate account number should fail
  # Crear primera cuenta
  Given path 'accounts'
  And request
    """
    {
      "accountNumber": "DUPLICATE-ACC-001",
      "accountType": "SAVINGS",
      "initialBalance": 1000.00,
      "status": true,
      "clientId": 1
    }
    """
  When method POST
  Then status 201

  # Intentar crear segunda cuenta con mismo número
  Given path 'accounts'
  And request
    """
    {
      "accountNumber": "DUPLICATE-ACC-001",
      "accountType": "CHECKING",
      "initialBalance": 2000.00,
      "status": true,
      "clientId": 2
    }
    """
  When method POST
  Then status 409

Scenario: Create account with invalid account type
  Given path 'accounts'
  And request
    """
    {
      "accountNumber": "INVALID-TYPE-001",
      "accountType": "INVALID_TYPE",
      "initialBalance": 1000.00,
      "status": true,
      "clientId": 1
    }
    """
  When method POST
  Then status 400

Scenario: Create account with zero initial balance
  Given path 'accounts'
  And request
    """
    {
      "accountNumber": "ZERO-BALANCE-001",
      "accountType": "SAVINGS",
      "initialBalance": 0.00,
      "status": true,
      "clientId": 1
    }
    """
  When method POST
  Then status 201
  And match response.initialBalance == 0.00

Scenario: Create account with very large initial balance
  Given path 'accounts'
  And request
    """
    {
      "accountNumber": "LARGE-BALANCE-001",
      "accountType": "SAVINGS",
      "initialBalance": 999999999.99,
      "status": true,
      "clientId": 1
    }
    """
  When method POST
  Then status 201
  And match response.initialBalance == 999999999.99

Scenario: Transaction with amount exceeding account balance should fail
  # Crear cuenta con balance inicial
  Given path 'accounts'
  And request
    """
    {
      "accountNumber": "INSUFFICIENT-001",
      "accountType": "CHECKING",
      "initialBalance": 100.00,
      "status": true,
      "clientId": 1
    }
    """
  When method POST
  Then status 201
  * def accountId = response.id

  # Intentar retirar más del saldo disponible
  Given path 'accounts', accountId, 'transactions'
  And request
    """
    {
      "date": "2024-01-21T10:00:00",
      "transactionType": "WITHDRAWAL",
      "amount": 200.00
    }
    """
  When method POST
  Then status 400

Scenario: Create transaction with invalid transaction type
  # Crear cuenta
  Given path 'accounts'
  And request
    """
    {
      "accountNumber": "INVALID-TXN-001",
      "accountType": "SAVINGS",
      "initialBalance": 1000.00,
      "status": true,
      "clientId": 1
    }
    """
  When method POST
  Then status 201
  * def accountId = response.id

  # Intentar crear transacción con tipo inválido
  Given path 'accounts', accountId, 'transactions'
  And request
    """
    {
      "date": "2024-01-21T10:00:00",
      "transactionType": "INVALID_TYPE",
      "amount": 100.00
    }
    """
  When method POST
  Then status 400

Scenario: Create transaction with future date should be allowed
  # Crear cuenta
  Given path 'accounts'
  And request
    """
    {
      "accountNumber": "FUTURE-DATE-001",
      "accountType": "SAVINGS",
      "initialBalance": 1000.00,
      "status": true,
      "clientId": 1
    }
    """
  When method POST
  Then status 201
  * def accountId = response.id

  # Crear transacción con fecha futura
  Given path 'accounts', accountId, 'transactions'
  And request
    """
    {
      "date": "2025-12-31T23:59:59",
      "transactionType": "DEPOSIT",
      "amount": 500.00
    }
    """
  When method POST
  Then status 201
  And match response.date == '2025-12-31T23:59:59'

Scenario: Bulk account creation and transaction processing
  * def accounts =
    """
    [
      {
        "accountNumber": "BULK-ACC-001",
        "accountType": "SAVINGS",
        "initialBalance": 1000.00,
        "status": true,
        "clientId": 1
      },
      {
        "accountNumber": "BULK-ACC-002",
        "accountType": "CHECKING",
        "initialBalance": 2000.00,
        "status": true,
        "clientId": 2
      },
      {
        "accountNumber": "BULK-ACC-003",
        "accountType": "SAVINGS",
        "initialBalance": 3000.00,
        "status": true,
        "clientId": 3
      }
    ]
    """

  # Crear cuentas en lote
  * def createdAccounts = []
  * def createAccount =
    """
    function(account) {
      karate.log('Creating account:', account.accountNumber);
      var response = karate.call('classpath:com/swiftline/account/create-account.feature', account);
      return response.response;
    }
    """

  * eval createdAccounts = karate.map(accounts, createAccount)

  # Verificar que se crearon todas las cuentas
  Given path 'accounts'
  When method GET
  Then status 200
  And match response == '#[]'

Scenario: Concurrent transaction processing simulation
  # Crear cuenta para transacciones concurrentes
  Given path 'accounts'
  And request
    """
    {
      "accountNumber": "CONCURRENT-001",
      "accountType": "CHECKING",
      "initialBalance": 5000.00,
      "status": true,
      "clientId": 1
    }
    """
  When method POST
  Then status 201
  * def accountId = response.id

  # Simular múltiples transacciones
  * def transactions =
    """
    [
      {
        "date": "2024-01-22T09:00:00",
        "transactionType": "DEPOSIT",
        "amount": 100.00
      },
      {
        "date": "2024-01-22T09:01:00",
        "transactionType": "WITHDRAWAL",
        "amount": 50.00
      },
      {
        "date": "2024-01-22T09:02:00",
        "transactionType": "DEPOSIT",
        "amount": 200.00
      },
      {
        "date": "2024-01-22T09:03:00",
        "transactionType": "WITHDRAWAL",
        "amount": 75.00
      }
    ]
    """

  * def processTransaction =
    """
    function(txn) {
      var payload = txn;
      var response = karate.call('classpath:com/swiftline/account/create-transaction.feature', {accountId: accountId, transaction: payload});
      return response.response;
    }
    """

  * def processedTransactions = karate.map(transactions, processTransaction)

  # Verificar todas las transacciones
  Given path 'accounts', accountId, 'transactions'
  When method GET
  Then status 200
  And match response == '#[4]'
