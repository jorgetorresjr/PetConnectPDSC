package com.PetConnect.steps;

import com.PetConnect.pages.EditPetProfilePage;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

public class EditPetProfileSteps {

    private final EditPetProfilePage editPetProfilePage = new EditPetProfilePage();

    @When("the user opens the pet edit page")
    public void theUserOpensThePetEditPage() {
        editPetProfilePage.clickBackButton();
        editPetProfilePage.openFirstPetProfile();
        editPetProfilePage.clickEditButton();
    }

    @And("updates the pet information with valid data")
    public void updatesThePetInformationWithValidData() {
        editPetProfilePage.fillPetName("Rex");
        editPetProfilePage.fillBreed("Golden Retriever");
        editPetProfilePage.fillAge("5");
    }

    @And("submits the changes")
    public void submitsTheChanges() {
        editPetProfilePage.clickSaveButton();
    }

    @Then("the system should display a pet update success message")
    public void theSystemShouldDisplayAPetUpdateSuccessMessage() {
        String alert = editPetProfilePage.getAlertText();
        Assertions.assertTrue(alert.contains("Pet atualizado com sucesso"));
        editPetProfilePage.acceptAlert();
    }

    @And("changes only the pet name")
    public void changesOnlyThePetName() {
        editPetProfilePage.fillPetName("Bolt");
    }
}