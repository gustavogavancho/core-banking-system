Feature: Client API - Basic Tests

Background:
  * url 'http://localhost:8081'
  * header Accept = 'application/json'
  * header Content-Type = 'application/json'

Scenario: Create a new client
  Given path 'clients'
  And request
    """
    {
      "name": "Juan Pérez",
      "gender": "M",
      "age": 30,
      "identification": "12345678",
      "address": "Calle 123 #45-67",
      "phoneNumber": "+57 300 123 4567",
      "password": "password123",
      "status": true
    }
    """
  When method POST
  Then status 201
  And match response.name == 'Juan Pérez'
  And match response.identification == '12345678'

Scenario: Get all clients
  Given path 'clients'
  When method GET
  Then status 200
  And match response == '#[]'
