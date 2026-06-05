package com.PetConnect.pages;

import com.PetConnect.utils.DriverFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private final WebDriver driver = DriverFactory.getDriver();
    private final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

    private static final String BASE_URL = "http://127.0.0.1:5500/src/main/resources/static/html/";

    private final By emailField = By.id("email");
    private final By senhaField = By.id("senha");
    private final By loginButton = By.id("btnLogin");
    private final By errorMessage = By.id("mensagemErro");

    public void open() {

        driver.get(BASE_URL + "login.html");
    }

    public void fillCredentials(String email, String senha) {

        driver.findElement(emailField)
                .sendKeys(email);

        driver.findElement(senhaField)
                .sendKeys(senha);
    }

    public void clickLoginButton() {

        driver.findElement(loginButton).click();
    }

    public boolean isHomePageLoaded() {

        wait.until(
                ExpectedConditions.urlContains("http://127.0.0.1:5500/src/main/resources/static/html/petOwnerHome.html")
        );

        return driver.getCurrentUrl().contains("http://127.0.0.1:5500/src/main/resources/static/html/petOwnerHome.html");
    }

    public String getLoginErrorMessage() {

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(errorMessage)
        );

        return driver.findElement(errorMessage)
                .getText();
    }
}