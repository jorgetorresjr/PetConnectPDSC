package com.PetConnect.steps;

import com.PetConnect.pages.LoginPage;

import com.PetConnect.pages.RegisterPage;
import io.cucumber.java.en.*;

import org.junit.jupiter.api.Assertions;

public class LoginSteps {

    LoginPage loginPage = new LoginPage();
    RegisterPage registerPage = new RegisterPage();

    @Given("a registered user exists")
    public void createValidUser() {

        registerPage.open();

        registerPage.registerExistingUser();
    }

    @When("the user is on the login page")
    public void openLoginPage() {

        loginPage.open();
    }

    @When("the user submits valid login credentials")
    public void submitValidCredentials() {

        loginPage.fillCredentials(
                registerPage.getRegisteredEmail(),
                registerPage.getRegisteredPassword()
        );
    }

    @And("clicks the login button")
    public void clickLoginButton() {

        loginPage.clickLoginButton();
    }

    @Then("the user should be redirected to the home page")
    public void validateLoginSuccess() {

        Assertions.assertTrue(
                loginPage.isHomePageLoaded("petOwnerHome.html")
        );
    }

    @When("the user submits invalid login credentials")
    public void submitInvalidCredentials() {

        loginPage.fillCredentials(
                "errado@email.com",
                "senhaerrada"
        );
    }

    @Then("the system should display an email or password error message")
    public void validateErrorMessage() {

        Assertions.assertEquals(
                "Email ou senha incorretos",
                loginPage.getLoginErrorMessage()
        );
    }
}