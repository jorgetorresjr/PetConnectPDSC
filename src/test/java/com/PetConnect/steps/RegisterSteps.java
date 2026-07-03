package com.PetConnect.steps;

import com.PetConnect.pages.RegisterPage;
import io.cucumber.java.en.*;

public class RegisterSteps {

    RegisterPage registerPage = new RegisterPage();

    @Given("the user is on the registration page")
    public void openRegisterPage() {

        registerPage.open();
    }

    @When("the user submits valid registration data")
    public void submitValidData() {

        registerPage.fillValidForm("PO");
    }

    @And("clicks the register button")
    public void clickRegisterButton() {

        registerPage.clickRegister();
    }

    @Then("the account should be created successfully")
    public void validateRegisterSuccess() {

        registerPage.validateSuccessAlert();
    }

    @Then("the user should be redirected to the login page")
    public void validateRedirect() {

        registerPage.validateRedirectToLogin();
    }

    @Given("a user already exists with the email")
    public void createExistingUser() {
        registerPage.open();
        registerPage.registerExistingUser();
    }

    @When("the user submits registration data with an existing email")
    public void submitExistingEmail() {
        registerPage.fillFormWithExistingEmail();
    }

    @Then("the system should display an email already registered error")
    public void validateDuplicateEmailError() {
        registerPage.validateEmailAlreadyExistsError();
    }
}