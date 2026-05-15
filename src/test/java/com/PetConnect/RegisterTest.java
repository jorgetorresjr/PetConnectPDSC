package com.PetConnect;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegisterTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String BASE_URL =
            "http://127.0.0.1:5500/src/main/resources/static/html/";

    @BeforeAll
    static void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        driver = new ChromeDriver(options);

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    @Order(1)
    void testCadastroValido() {
        driver.get(BASE_URL + "register.html");
        wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("name"))
        );
        driver.findElement(By.id("name"))
                .sendKeys("Flavio Augusto");
        driver.findElement(By.id("emailRegister"))
                .sendKeys("usuario01@email.com");

        driver.findElement(By.id("passwordRegister"))
                .sendKeys("#Senha123");

        driver.findElement(By.id("cpfRegister"))
                .sendKeys("529.982.247-25");

        driver.findElement(By.id("birthDateRegister"))
                .sendKeys("2000-01-01");

        driver.findElement(By.id("phoneRegister"))
                .sendKeys("81999999999");

        driver.findElement(By.id("addressStreet"))
                .sendKeys("Rua Um");

        driver.findElement(By.id("addressNumber"))
                .sendKeys("123");

        driver.findElement(By.id("addressComplement"))
                .sendKeys("Apto 1");

        driver.findElement(By.id("addressNeighborhood"))
                .sendKeys("Centro");

        driver.findElement(By.id("addressCity"))
                .sendKeys("Recife");

        driver.findElement(By.id("addressState"))
                .sendKeys("PE");

        driver.findElement(By.id("addressCep"))
                .sendKeys("50000-000");

        Select role = new Select(
                driver.findElement(By.id("roleRegister"))
        );

        role.selectByValue("PO");

        driver.findElement(By.id("btnRegister"))
                .click();

        Alert alert = wait.until(
                ExpectedConditions.alertIsPresent()
        );

        Assertions.assertEquals(
                "Cadastro realizado com sucesso! Faça login.",
                alert.getText()
        );

        alert.accept();

        wait.until(
                ExpectedConditions.urlContains("login.html")
        );

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("login.html")
        );
    }

    @Test
    @Order(2)
    void testCadastroInvalidoEmailJaCadastrado() {
        driver.get(BASE_URL + "register.html");
        wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("name"))
        );
        driver.findElement(By.id("name"))
                .sendKeys("Flavio Augusto");
        driver.findElement(By.id("emailRegister"))
                .sendKeys("usuario2@email.com");

        driver.findElement(By.id("passwordRegister"))
                .sendKeys("#Senha123");

        driver.findElement(By.id("cpfRegister"))
                .sendKeys("529.982.247-25");

        driver.findElement(By.id("birthDateRegister"))
                .sendKeys("2000-01-01");

        driver.findElement(By.id("phoneRegister"))
                .sendKeys("81999999999");

        driver.findElement(By.id("addressStreet"))
                .sendKeys("Rua Um");

        driver.findElement(By.id("addressNumber"))
                .sendKeys("123");

        driver.findElement(By.id("addressComplement"))
                .sendKeys("Apto 1");

        driver.findElement(By.id("addressNeighborhood"))
                .sendKeys("Centro");

        driver.findElement(By.id("addressCity"))
                .sendKeys("Recife");

        driver.findElement(By.id("addressState"))
                .sendKeys("PE");

        driver.findElement(By.id("addressCep"))
                .sendKeys("50000-000");

        Select role = new Select(
                driver.findElement(By.id("roleRegister"))
        );

        role.selectByValue("PO");

        driver.findElement(By.id("btnRegister"))
                .click();

        Alert alert = wait.until(
                ExpectedConditions.alertIsPresent()
        );

        Assertions.assertEquals(
                "Erro ao cadastrar: Email já cadastrado",
                alert.getText()
        );

        alert.accept();

        Assertions.assertTrue(
                driver.getCurrentUrl().contains("http://127.0.0.1:5500/src/main/resources/static/html/register.html")
        );
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
