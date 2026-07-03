package com.PetConnect.steps;

import com.PetConnect.pages.LoginPage;
import com.PetConnect.pages.PetPage;
import com.PetConnect.pages.RegisterPage;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

public class PetSteps {

    private final RegisterPage registerPage = new RegisterPage();
    private final LoginPage loginPage = new LoginPage();
    private final PetPage petPage = new PetPage();

    @When("the user registers as a pet owner with valid data")
    public void registerPetOwner() {

        registerPage.fillValidForm("PO");
        registerPage.clickRegister();
        registerPage.validateSuccessAlert();
        registerPage.validateRedirectToLogin();
    }

    @And("the user logs in with the registered credentials")
    public void loginWithRegisteredCredentials() {
        loginPage.fillCredentials(
                registerPage.getRegisteredEmail(),
                registerPage.getRegisteredPassword()
        );

        loginPage.clickLoginButton();

        Assertions.assertTrue(
                loginPage.isHomePageLoaded("petOwnerHome.html")
        );
    }

    @And("the user opens the pet registration page")
    public void openPetRegistrationPage() {

        petPage.clickNewPetButton();
        petPage.isPetPageLoaded();
    }

    @And("fills the pet form with valid data")
    public void fillPetForm() {

        petPage.fillPetForm(
                "Rex",
                "Cachorro",
                "Labrador",
                "5",
                "Nenhuma observação"
        );
    }

    @And("submits the pet registration")
    public void submitPetRegistration() {

        petPage.submitPetRegistration();
    }

    @Then("the system should display a pet registration success message")
    public void validateSuccessMessage() {

        petPage.validateSuccessAlert();
    }

    @Then("fills the pet form with a name exceeding the maximum length")
    public void fillFormWithLongName() {
        petPage.fillPetFormWithLongName();

    }

    @Then("the system should display a pet registration error message")
    public void validateFillFormWithLongName() {

        petPage.validateErrorAlert();
    }
}