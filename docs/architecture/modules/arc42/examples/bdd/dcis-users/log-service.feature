Feature: User Log Service

  Scenario: Write User Log Entry
    Given: A user with namespace 'NAMESPACE' and name 'NAME' exists
    And: The success counter is 0.
    And: The error counter is 0.
    When: A log entry for user with namespace 'NAMESPACE' and name 'NAME' with system 'SYSTEM' and a date 'NOW' with text 'TEXT FOR LOG' is sent
    Then: A log entry with the data given has to be in the database
    And: The success counter is 1.
    And: The error counter is 0.

  Scenario: Write Log Entry for Unknown User
    Given: A user with namespace 'NAMESPACE' and name 'NAME' does not exist
    And: The success counter is 0.
    And: The error counter is 0.
    When: A log entry for user with namespace 'NAMESPACE' and name 'NAME' with system 'SYSTEM' and a date 'NOW' with text 'TEXT FOR LOG' is sent
    Then: A log entry with the data given has to be in the database
    And: The success counter is 0.
    And: The error counter is 1.