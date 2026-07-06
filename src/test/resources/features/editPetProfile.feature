Feature: Edit pet profile

  Background:
    Given the user is registered as a pet owner
    And the user is logged in
    And the user has a registered pet
    And the user is on the pet profile page

  Scenario: Edit a pet successfully
    When the user opens the pet edit page
    And updates the pet information with valid data
    And submits the changes
    Then the system should display a pet update success message

  Scenario: Edit only the pet name
    When the user opens the pet edit page
    And changes only the pet name
    And submits the changes
    Then the system should display a pet update success message

  Scenario: Edit a pet with a name that exceeds the maximum length
    When the user opens the pet edit page
    And the user enters a pet name longer than 100 characters
    Then the name field should contain only 100 characters
