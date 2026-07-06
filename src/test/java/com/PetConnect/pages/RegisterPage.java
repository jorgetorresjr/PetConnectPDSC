package com.PetConnect.pages;

import com.PetConnect.utils.DriverFactory;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.UUID;

public class RegisterPage {
    private final WebDriver driver;

    private final WebDriverWait wait;

    private static final String BASE_URL = "http://127.0.0.1:5500/src/main/resources/static/html/";

    public RegisterPage() {

        this.driver = DriverFactory.getDriver();

        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    private final By name = By.id("name");
    private final By emailInput = By.id("emailRegister");
    private final By password = By.id("passwordRegister");
    private final By cpf = By.id("cpfRegister");
    private final By birthDate = By.id("birthDateRegister");
    private final By phone = By.id("phoneRegister");

    private final By street = By.id("addressStreet");
    private final By number = By.id("addressNumber");
    private final By complement = By.id("addressComplement");
    private final By neighborhood = By.id("addressNeighborhood");
    private final By city = By.id("addressCity");
    private final By state = By.id("addressState");
    private final By cep = By.id("addressCep");

    private final By roleSelect = By.id("roleRegister");
    private final By registerButton = By.id("btnRegister");

    private static String registeredEmail;
    private static final String registeredPassword = "#Senha123";
    private final By errorMessage = By.id("register-errors");


    public void open() {
        driver.get(BASE_URL + "register.html");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(name)
        );
    }

    public void fillValidForm(String role) {
        registeredEmail = "user" + UUID.randomUUID() + "@email.com";

        fillFormWithEmail(registeredEmail);

        Select select = new Select(driver.findElement(roleSelect));
        select.selectByValue(role);
    }

    public void clickRegister() {

        driver.findElement(registerButton)
                .click();
    }

    public void validateSuccessAlert() {

        Alert alert = wait.until(
                ExpectedConditions.alertIsPresent()
        );

        Assertions.assertEquals(
                "Cadastro realizado com sucesso! Faça login.",
                alert.getText()
        );

        alert.accept();
    }

    public void validateRedirectToLogin() {

        wait.until(
                ExpectedConditions
                        .urlContains("login.html")
        );

        Assertions.assertTrue(
                driver.getCurrentUrl()
                        .contains("login.html")
        );
    }

    public void fillFormWithEmail(String email) {
        driver.findElement(name).sendKeys("Flavio Augusto");
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(password).sendKeys(registeredPassword);
        driver.findElement(cpf).sendKeys("529.982.247-25");
        driver.findElement(birthDate).sendKeys("01-01-2000");
        driver.findElement(phone).sendKeys("81999999999");
        driver.findElement(street).sendKeys("Rua Um");
        driver.findElement(number).sendKeys("123");
        driver.findElement(complement).sendKeys("Apto 1");
        driver.findElement(neighborhood).sendKeys("Centro");
        driver.findElement(city).sendKeys("Recife");
        driver.findElement(state).sendKeys("PE");
        driver.findElement(cep).sendKeys("50000-000");
    }

    public String getRegisteredEmail() {

        return registeredEmail;
    }

    public String getRegisteredPassword() {

        return registeredPassword;
    }

    public void registerExistingUser() {
        registeredEmail = "user" + UUID.randomUUID() + "@email.com";

        fillFormWithEmail(registeredEmail);

        Select select = new Select(driver.findElement(roleSelect));
        select.selectByValue("PO");

        clickRegister();
        validateSuccessAlert();
    }

    public void fillFormWithExistingEmail() {
        fillFormWithEmail(registeredEmail);
        Select select = new Select(driver.findElement(roleSelect));
        select.selectByValue("PO");
    }

    public String getRegisterErrorMessage() {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(errorMessage)
        );

        return driver.findElement(errorMessage)
                .getText();
    }

    public void fillFormWithInvalidBirthDate(String email) {
            driver.findElement(name).sendKeys("Flavio Augusto");
            driver.findElement(emailInput).sendKeys(email);
            driver.findElement(password).sendKeys(registeredPassword);
            driver.findElement(cpf).sendKeys("529.982.247-25");
            driver.findElement(birthDate).sendKeys("01-01-1843");
            driver.findElement(phone).sendKeys("81999999999");
            driver.findElement(street).sendKeys("Rua Um");
            driver.findElement(number).sendKeys("123");
            driver.findElement(complement).sendKeys("Apto 1");
            driver.findElement(neighborhood).sendKeys("Centro");
            driver.findElement(city).sendKeys("Recife");
            driver.findElement(state).sendKeys("PE");
            driver.findElement(cep).sendKeys("50000-000");
    }

    public void fillInvalidForm(String role) {
        registeredEmail = "user" + UUID.randomUUID() + "@email.com";

        fillFormWithInvalidBirthDate(registeredEmail);

        Select select = new Select(driver.findElement(roleSelect));
        select.selectByValue(role);
    }
}