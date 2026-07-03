package com.PetConnect.pages;

import com.PetConnect.utils.DriverFactory;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PetPage {

    private final WebDriver driver = DriverFactory.getDriver();
    private final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

    private static final String BASE_URL = "http://127.0.0.1:5500/src/main/resources/static/html/";

    private final By newPetButton = By.id("cadastrarPetBtn");

    private final By nameField = By.id("name");
    private final By specieField = By.id("specie");
    private final By breedField = By.id("breed");
    private final By ageField = By.id("age");
    private final By observationsField = By.id("observations");

    private final By saveButton = By.cssSelector("button[type='submit']");

    public void open() {
        driver.get(BASE_URL + "petCreate.html");
    }

    public void clickNewPetButton() {
        driver.findElement(newPetButton).click();
    }

    public void fillPetForm(String name,
                            String specie,
                            String breed,
                            String age,
                            String observations) {

        driver.findElement(nameField).sendKeys(name);
        driver.findElement(specieField).sendKeys(specie);
        driver.findElement(breedField).sendKeys(breed);
        driver.findElement(ageField).sendKeys(age);
        driver.findElement(observationsField).sendKeys(observations);
    }

    public void submitPetRegistration() {
        driver.findElement(saveButton).click();
    }

    public boolean isPetPageLoaded() {

        wait.until(
                ExpectedConditions.urlContains("petCreate.html")
        );

        return driver.getCurrentUrl().contains("petCreate.html");
    }

    public boolean isHomePageLoaded() {

        wait.until(
                ExpectedConditions.urlContains("petOwnerHome.html")
        );

        return driver.getCurrentUrl().contains("petOwnerHome.html");
    }

    public void validateSuccessAlert() {
        Alert alert = wait.until(
                ExpectedConditions.alertIsPresent()
        );

        Assertions.assertEquals(
                "Pet cadastrado com sucesso!",
                alert.getText()
        );
        alert.accept();
    }

    public void fillPetFormWithLongName() {
        String longName = "K".repeat(300);
        fillPetForm(
                longName,
                "Gato",
                "Siamês",
                "2",
                "Teste"
        );
    }

    public void validateErrorAlert() {
        Alert alert = wait.until(
                ExpectedConditions.alertIsPresent()
        );

        Assertions.assertTrue(
                alert.getText().contains("Erro ao salvar o pet")
        );

        alert.accept();
    }
}