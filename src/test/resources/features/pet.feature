Feature: Pet registration

  Scenario: Register a pet successfully
    Given the user is on the registration page
    When the user registers as a pet owner with valid data
    And the user logs in with the registered credentials
    And the user opens the pet registration page
    And fills the pet form with valid data
    And submits the pet registration
    Then the system should display a pet registration success message

  Scenario: Register a pet with a name that exceeds the maximum length
    Given the user is on the registration page
    When the user registers as a pet owner with valid data
    And the user logs in with the registered credentials
    And the user opens the pet registration page
    And fills the pet form with a name exceeding the maximum length
    And submits the pet registration
    Then the system should display a pet registration error message