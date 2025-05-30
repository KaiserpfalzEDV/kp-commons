Feature: User Registration

  Scenario: Register a new user
    Given: A user with issuer 'ISSUER' and subject 'subject' does not exists
    And: The success counter is 0.
    And: The error counter is 0.
    When: A user with issuer 'ISSUER' and subject 'subject' is registered
    Then: A user with issuer 'ISSUER' and subject 'subject' exists
    And: The success counter is 1.
    And: The error counter is 0.

  Scenario: Trying to register a banned user
    Given: A user with issuer 'ISSUER' and subject 'subject' does exists
    And: The user with issuer 'ISSUER' and subject 'subject' is banned
    And: The success counter is 0.
    And: The error counter is 0.
    When: A user with issuer 'ISSUER' and subject 'subject' is registered
    Then: A user with issuer 'ISSUER' and subject 'subject' is banned
    And: The success counter is 0.
    And: The error counter is 1.

  Scenario: Trying to register a blocked user
    Given: A user with issuer 'ISSUER' and subject 'subject' does exists
    And: The user with issuer 'ISSUER' and subject 'subject' is blocked
    And: The success counter is 0.
    And: The error counter is 0.
    When: A user with issuer 'ISSUER' and subject 'subject' is registered
    Then: A user with issuer 'ISSUER' and subject 'subject' is blocked
    And: The success counter is 0.
    And: The error counter is 1.

  Scenario: Re-register a deleted user
    Given: A user with issuer 'ISSUER' and subject 'subject' does exists
    And: The user with issuer 'ISSUER' and subject 'subject' is deleted
    And: The success counter is 0.
    And: The error counter is 0.
    When: A user with issuer 'ISSUER' and subject 'subject' is registered
    Then: A user with issuer 'ISSUER' and subject 'subject' exists
    And: The success counter is 1.
    And: The error counter is 0.
