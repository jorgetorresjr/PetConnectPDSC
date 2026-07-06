Feature: Pet registration

  Background:
    Given the user is on the registration page

  Scenario: Register a pet successfully
    When the user registers as a pet owner with valid data
    And the user logs in with the registered credentials
    And the user opens the pet registration page
    And the user fills the pet form with valid data
    And submits the pet registration
    Then the system should display a pet registration success message

  Scenario: Register a pet with a name that exceeds the maximum length
    When the user registers as a pet owner with valid data
    And the user logs in with the registered credentials
    And the user opens the pet registration page
    And the user enters a pet name longer than 100 characters
    Then the name field should contain only 100 characters