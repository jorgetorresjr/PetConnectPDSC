package com.PetConnect.services;

import com.PetConnect.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetails user = (UserDetails) userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            System.out.println("[DEBUG] Usuário não encontrado para email: " + email);
            throw new UsernameNotFoundException("Usuário não encontrado: " + email);
        }
        System.out.println("[DEBUG] Usuário carregado: " + email + ", senha hash: " + user.getPassword());
        return user;
    }
}
