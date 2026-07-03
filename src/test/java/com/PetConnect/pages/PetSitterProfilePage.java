package com.PetConnect.pages;

import com.PetConnect.utils.DriverFactory;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PetSitterProfilePage {

    private final WebDriver driver = DriverFactory.getDriver();
    private final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

    private final By createProfileButton = By.id("criarPerfilBtn");

    private final By specialty = By.id("specialty");
    private final By certificates = By.id("certificates");

    private final By walkService = By.cssSelector("input[name='services'][value='1']");
    private final By monday = By.cssSelector("input[name='dias'][value='Segunda']");

    private final By startTime = By.id("horarioInicio");
    private final By endTime = By.id("horarioFim");

    private final By saveButton = By.cssSelector("button[type='submit']");

    public void clickCreateProfile() {

        driver.findElement(createProfileButton).click();
    }

    public void fillProfile() {

        driver.findElement(By.id("specialty"))
                .sendKeys("Especialista em cães");

        driver.findElement(By.id("certificates"))
                .sendKeys("Curso de Primeiros Socorros");

        driver.findElement(
                By.cssSelector("input[name='services'][value='1']")
        ).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("preco_Passeio")
                )
        );

        driver.findElement(By.id("preco_Passeio"))
                .sendKeys("40");

        driver.findElement(
                By.cssSelector("input[name='dias'][value='Segunda']")
        ).click();

        driver.findElement(By.id("horarioInicio"))
                .sendKeys("08:00");

        driver.findElement(By.id("horarioFim"))
                .sendKeys("18:00");
    }

    public void submitProfile() {
        driver.findElement(
                By.cssSelector("button[type='submit']")
        ).click();
    }

    public void validateSuccessAlert() {
        Alert alert = wait.until(
                ExpectedConditions.alertIsPresent()
        );

        Assertions.assertEquals(
                "Perfil criado com sucesso!",
                alert.getText()
        );

        alert.accept();
    }
}
