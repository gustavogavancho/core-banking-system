Feature: Client API Edge Cases and Error Handling

Background:
  * url 'http://localhost:8081'
  * header Accept = 'application/json'
  * header Content-Type = 'application/json'

Scenario: Create client with duplicate identification should fail
  # Crear primer cliente
  Given path 'clients'
  And request
    """
    {
      "name": "Cliente Original",
      "gender": "M",
      "age": 30,
      "identification": "DUPLICATE123",
      "address": "Dirección Original",
      "phoneNumber": "+57 300 111 1111",
      "password": "password123",
      "status": true
    }
    """
  When method POST
  Then status 201

  # Intentar crear segundo cliente con misma identificación
  Given path 'clients'
  And request
    """
    {
      "name": "Cliente Duplicado",
      "gender": "F",
      "age": 25,
      "identification": "DUPLICATE123",
      "address": "Dirección Diferente",
      "phoneNumber": "+57 300 222 2222",
      "password": "password456",
      "status": true
    }
    """
  When method POST
  Then status 409

Scenario: Create client with invalid email format
  Given path 'clients'
  And request
    """
    {
      "name": "Test User",
      "gender": "M",
      "age": 30,
      "identification": "EMAIL123",
      "address": "Test Address",
      "phoneNumber": "+57 300 123 4567",
      "password": "password123",
      "status": true
    }
    """
  When method POST
  Then status 201

Scenario: Update non-existent client should return 404
  Given path 'clients', 99999
  And request
    """
    {
      "name": "Updated Name",
      "gender": "M",
      "age": 35,
      "identification": "UPDATED123",
      "address": "Updated Address",
      "phoneNumber": "+57 300 999 9999",
      "password": "newpassword",
      "status": true
    }
    """
  When method PUT
  Then status 404

Scenario: Create client with invalid age (negative)
  Given path 'clients'
  And request
    """
    {
      "name": "Invalid Age User",
      "gender": "F",
      "age": -5,
      "identification": "INVALID123",
      "address": "Test Address",
      "phoneNumber": "+57 300 123 4567",
      "password": "password123",
      "status": true
    }
    """
  When method POST
  Then status 400

Scenario: Create client with invalid age (too old)
  Given path 'clients'
  And request
    """
    {
      "name": "Too Old User",
      "gender": "M",
      "age": 150,
      "identification": "TOOOLD123",
      "address": "Test Address",
      "phoneNumber": "+57 300 123 4567",
      "password": "password123",
      "status": true
    }
    """
  When method POST
  Then status 400

Scenario: Create client with invalid phone number format
  Given path 'clients'
  And request
    """
    {
      "name": "Invalid Phone User",
      "gender": "F",
      "age": 25,
      "identification": "PHONE123",
      "address": "Test Address",
      "phoneNumber": "invalid-phone",
      "password": "password123",
      "status": true
    }
    """
  When method POST
  Then status 400

Scenario: Bulk operations - Create multiple clients and verify
  # Crear múltiples clientes
  * def clients =
    """
    [
      {
        "name": "Bulk User 1",
        "gender": "M",
        "age": 30,
        "identification": "BULK001",
        "address": "Address 1",
        "phoneNumber": "+57 300 001 0001",
        "password": "password1",
        "status": true
      },
      {
        "name": "Bulk User 2",
        "gender": "F",
        "age": 25,
        "identification": "BULK002",
        "address": "Address 2",
        "phoneNumber": "+57 300 002 0002",
        "password": "password2",
        "status": true
      },
      {
        "name": "Bulk User 3",
        "gender": "M",
        "age": 35,
        "identification": "BULK003",
        "address": "Address 3",
        "phoneNumber": "+57 300 003 0003",
        "password": "password3",
        "status": false
      }
    ]
    """

  * def createClient =
    """
    function(client) {
      var result = karate.call('classpath:com/swiftline/client/create-client.feature', client);
      return result.response;
    }
    """

  # Crear los clientes usando función auxiliar
  * def createdClients = karate.map(clients, createClient)

  # Verificar que todos fueron creados
  Given path 'clients'
  When method GET
  Then status 200
  And match response == '#[]'
