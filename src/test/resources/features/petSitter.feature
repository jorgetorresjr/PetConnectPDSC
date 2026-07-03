Feature: Pet sitter profile

  Scenario: Complete the pet sitter profile successfully
    Given the user is on the registration page
    When the user registers as a pet sitter with valid data
    And the pet sitter logs in with the registered credentials
    And the user opens the profile creation page
    And fills the pet sitter profile with valid data
    And submits the profile
    Then the system should display a profile creation success message