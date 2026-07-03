package com.PetConnect.steps;

import com.PetConnect.pages.LoginPage;
import com.PetConnect.pages.PetSitterProfilePage;
import com.PetConnect.pages.RegisterPage;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

public class PetSitterProfileSteps {

    private final RegisterPage registerPage = new RegisterPage();
    private final LoginPage loginPage = new LoginPage();
    private final PetSitterProfilePage petSitterProfilePage = new PetSitterProfilePage();

    @When("the user registers as a pet sitter with valid data")
    public void registerPetSitter() {

        registerPage.fillValidForm("PS");

        registerPage.clickRegister();
        registerPage.validateSuccessAlert();
        registerPage.validateRedirectToLogin();
    }

    @And("the pet sitter logs in with the registered credentials")
    public void loginPetSitter() {
        loginPage.fillCredentials(
                registerPage.getRegisteredEmail(),
                registerPage.getRegisteredPassword()
        );

        loginPage.clickLoginButton();

        Assertions.assertTrue(
                loginPage.isHomePageLoaded("petSitterHome.html")
        );
    }

    @And("the user opens the profile creation page")
    public void openProfileCreationPage() {
        petSitterProfilePage.clickCreateProfile();

    }

    @And("fills the pet sitter profile with valid data")
    public void fillProfile() {

        petSitterProfilePage.fillProfile();


    }

    @And("submits the profile")
    public void submitProfile() {

        petSitterProfilePage.submitProfile();
    }

    @Then("the system should display a profile creation success message")
    public void validateSuccessMessage() {
        petSitterProfilePage.validateSuccessAlert();
    }
}