package com.PetConnect;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    static void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    @Order(1)
    void testLoginValido() {

        driver.get("http://127.0.0.1:5500/src/main/resources/static/login.html");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")))
                .sendKeys("usuario@email.com");

        driver.findElement(By.id("senha")).sendKeys("#Senha123");
        driver.findElement(By.id("btnLogin")).click();

        wait.until(ExpectedConditions.urlContains("home"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("home"));
    }

    @Test
    @Order(2)
    void testLoginCredenciaisInvalidas() {
        driver.get("http://127.0.0.1:5500/src/main/resources/static/login.html");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")))
                .sendKeys("errado@email.com");

        driver.findElement(By.id("senha")).sendKeys("senhaerrada");
        driver.findElement(By.id("btnLogin")).click();

        WebElement erro = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("mensagem-erro"))
        );
        Assertions.assertTrue(erro.isDisplayed());
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) driver.quit();
    }
}