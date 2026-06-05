Feature: User registration
  Scenario: Successful registration
    Given the user is on the registration page
    When the user submits valid registration data
    And clicks the register button
    Then the account should be created successfully
    And the user should be redirected to the login page

  Scenario: Registration with an already registered email
    Given a user already exists with the email
    And the user is on the registration page
    When the user submits registration data with an existing email
    And clicks the register button
    Then the system should display an email already registered error