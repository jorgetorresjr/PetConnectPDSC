package com.PetConnect.config;

import com.PetConnect.entities.Address;
import com.PetConnect.entities.Admin;
import com.PetConnect.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail("admin@petconnect.com").isEmpty()) {
            Admin admin = new Admin();
            admin.setName("Administrador");
            admin.setEmail("admin@petconnect.com");
            admin.setLogin("admin@petconnect.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setCpf("529.982.247-25");
            admin.setPhone("11000000000");

            Calendar cal = Calendar.getInstance();
            cal.set(1990, Calendar.JANUARY, 1);
            admin.setBirthDate(cal.getTime());

            Address address = new Address();
            address.setStreet("Rua Administracao");
            address.setNeighborhood("Centro");
            address.setNumber(1);
            address.setCep("01001-000");
            address.setCity("São Paulo");
            address.setState("SP");
            admin.setAddress(address);

            userRepository.save(admin);
            System.out.println("[DataInitializer] Admin criado: admin@petconnect.com / Admin@123");
        }
    }
}
