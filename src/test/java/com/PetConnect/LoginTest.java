package com.PetConnect;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://127.0.0.1:5500/src/main/resources/static/html/";

    @BeforeAll
    static void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @BeforeEach
    void limparSessao() {
        driver.manage().deleteAllCookies();
        driver.get("http://127.0.0.1:5500");

        ((ChromeDriver) driver)
                .executeScript("window.localStorage.clear();");

        ((ChromeDriver) driver)
                .executeScript("window.sessionStorage.clear();");
    }

    @Test
    @Order(1)
    void testLoginValido() {
        driver.get(BASE_URL + "login.html");

        WebElement email = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("email"))
        );

        email.sendKeys("usuario2@email.com");

        driver.findElement(By.id("senha"))
                .sendKeys("#Senha123");

        driver.findElement(By.id("btnLogin"))
                .click();

        wait.until(ExpectedConditions.urlContains("http://127.0.0.1:5500/src/main/resources/static/html/petSitterHome.html"));

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("http://127.0.0.1:5500/src/main/resources/static/html/petSitterHome.html")
        );
    }

    @Test
    @Order(2)
    void testLoginCredenciaisInvalidas() {
        driver.get(BASE_URL + "login.html");

        WebElement email = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("email"))
        );

        email.sendKeys("errado@email.com");

        driver.findElement(By.id("senha"))
                .sendKeys("senhaerrada");

        driver.findElement(By.id("btnLogin"))
                .click();

        WebElement erro = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("mensagemErro")
                )
        );

        Assertions.assertTrue(erro.isDisplayed());
        Assertions.assertEquals(
                "Email ou senha incorretos",
                erro.getText()
        );
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}