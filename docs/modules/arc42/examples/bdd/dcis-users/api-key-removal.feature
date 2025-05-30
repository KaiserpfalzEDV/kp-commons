Feature: User API key removal

  Scenario: Remove API key successfully
    Given: A user with issuer 'ISSUER' and subject 'SUBJECT' exists
    And: An api key 'ÀPI-KEY' for issuer 'ISSUER' and subject 'SUBJECT' exists
    And: The success counter is 0.
    And: The error counter is 0.
    When: The api key 'API-KEY' is removed by user identified by issuer 'ISSUER' and subject 'SUBJECT'
    Then: The new api 'API-KEY' does not exist
    And: The success counter is 1.
    And: The error counter is 0.
    And: The successful key removal for api key 'API-KEY' is logged for user with issuer 'ISSUER' and subject 'SUBJECT'

  Scenario: Removal of API key fails
    Given: A user with issuer 'ISSUER' and subject 'SUBJECT' exists
    And: No api key 'ÀPI-KEY' for issuer 'ISSUER' and subject 'SUBJECT' exists
    And: The success counter is 0.
    And: The error counter is 0.
    When: The api key 'API-KEY' is removed by user identified by issuer 'ISSUER' and subject 'SUBJECT'
    Then: The new api 'API-KEY' does not exist
    And: The success counter is 0.
    And: The error counter is 1.
    And: The unsuccessful key removal for api key 'API-KEY' is logged for user with issuer 'ISSUER' and subject 'SUBJECT'
