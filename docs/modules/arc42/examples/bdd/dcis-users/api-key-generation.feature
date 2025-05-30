Feature: User API key generation

  Scenario: Generate API key successfully
    Given: A user with issuer 'ISSUER' and subject 'SUBJECT' exists
    And: The success counter is 0.
    And: The error counter is 0.
    When: A new api key is generated with issuer 'ISSUER' and subject 'SUBJECT'
    Then: The new api key exists
    And: The success counter is 1.
    And: The error counter is 0.
    And: The successful key generation is logged for user with issuer 'ISSUER' and subject 'SUBJECT'

  Scenario: Generation of API key fails
    Given: A user with issuer 'ISSUER' and subject 'SUBJECT' exists
    And: The success counter is 0.
    And: The error counter is 0.
    When: A new api key is generated with issuer 'ISSUER' and subject 'SUBJECT'
    Then: The success counter is 0.
    And: The error counter is 1.
    And: The unsuccessful key generation is logged for user with issuer 'ISSUER' and subject 'SUBJECT'
