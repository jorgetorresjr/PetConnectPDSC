Feature: User login

  Scenario: Successful login
    Given a registered user exists
    And the user is on the login page
    When the user submits valid login credentials
    And clicks the login button
    Then the user should be redirected to the home page

  Scenario: Unsuccessful login
    Given the user is on the login page
    When the user submits invalid login credentials
    And clicks the login button
    Then the system should display an email or password error message