Feature: User Registration in a client

  Scenario: Register a new user
    Given: A user with issuer 'ISSUER' and subject 'SUBJECT' does not exists
    And: The success counter is 0.
    And: The error counter is 0.
    When: A user with issuer 'ISSUER' and subject 'SUBJECT' is registered
    Then: A user with issuer 'ISSUER' and subject 'SUBJECT' exists
    And: The success counter is 1.
    And: The error counter is 0.

  Scenario: Trying to register a banned user
    Given: A user with issuer 'ISSUER' and subject 'SUBJECT' does exists
    And: The user with issuer 'ISSUER' and subject 'SUBJECT' is banned
    And: The success counter is 0.
    And: The error counter is 0.
    When: A user with issuer 'ISSUER' and subject 'SUBJECT' is registered
    Then: A user with issuer 'ISSUER' and subject 'SUBJECT' is banned
    And: The success counter is 0.
    And: The error counter is 1.

  Scenario: Trying to register a blocked user
    Given: A user with issuer 'ISSUER' and subject 'SUBJECT' does exists
    And: The user with issuer 'ISSUER' and subject 'SUBJECT' is blocked
    And: The success counter is 0.
    And: The error counter is 0.
    When: A user with issuer 'ISSUER' and subject 'SUBJECT' is registered
    Then: A user with issuer 'ISSUER' and subject 'SUBJECT' is blocked
    And: The success counter is 0.
    And: The error counter is 1.

  Scenario: Trying to register a deleted user
    Given: A user with issuer 'ISSUER' and subject 'SUBJECT' does exists
    And: The user with issuer 'ISSUER' and subject 'SUBJECT' is deleted
    And: The success counter is 0.
    And: The error counter is 0.
    When: A user with issuer 'ISSUER' and subject 'SUBJECT' is registered
    Then: A user with issuer 'ISSUER' and subject 'SUBJECT' is blocked
    And: The success counter is 0.
    And: The error counter is 1.
