package com.PetConnect.hooks;

import com.PetConnect.utils.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {

    @Before
    public void setup() {

        DriverFactory.createDriver();
    }

    @After
    public void teardown() {

        DriverFactory.quitDriver();
    }
}