package com.PetConnect.pages;

import com.PetConnect.utils.DriverFactory;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EditPetProfilePage {

    private final WebDriver driver = DriverFactory.getDriver();
    private final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

    private final By petProfile = By.id("perfilPet");

    private final By nameField = By.id("nome");
    private final By breedField = By.id("raca");
    private final By ageField = By.id("idade");

    private final By profileName = By.id("petName");
    private final By cancelButton = By.id("cancelBtn");
    private final By editButton = By.id("editarPetBtn");

    private final By saveButton = By.cssSelector("button[type='submit']");
    private final By backButton = By.xpath("//button[contains(text(),'Voltar')]");
    private final By viewProfileButton = By.xpath("//button[contains(text(),'Ver Perfil')]");

    public void openFirstPetProfile() {
        wait.until(ExpectedConditions.elementToBeClickable(viewProfileButton))
                .click();
    }

    public void clickEditButton() {
        wait.until(ExpectedConditions.elementToBeClickable(editButton)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
    }


    public void fillPetName(String name) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
        driver.findElement(nameField).clear();
        driver.findElement(nameField).sendKeys(name);
    }

    public void fillBreed(String breed) {
        driver.findElement(breedField).clear();
        driver.findElement(breedField).sendKeys(breed);
    }

    public void fillAge(String age) {
        driver.findElement(ageField).clear();
        driver.findElement(ageField).sendKeys(age);
    }


    public void clickSaveButton() {
        driver.findElement(saveButton).click();
    }

    public void clickCancelButton() {
        driver.findElement(cancelButton).click();
    }

    public String getPetName() {
        return driver.findElement(profileName).getText();
    }

    public Alert waitForAlert() {
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    public String getAlertText() {
        return waitForAlert().getText();
    }

    public void acceptAlert() {
        waitForAlert().accept();
    }

    public boolean isOnPetOwnerHome() {
        wait.until(ExpectedConditions.urlContains("petOwnerHome"));
        return driver.getCurrentUrl().contains("petOwnerHome");
    }

    public void clickBackButton() {
        wait.until(ExpectedConditions.elementToBeClickable(backButton)).click();
    }
}