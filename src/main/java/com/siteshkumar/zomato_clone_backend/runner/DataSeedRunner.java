package com.siteshkumar.zomato_clone_backend.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.siteshkumar.zomato_clone_backend.entity.UserEntity;
import com.siteshkumar.zomato_clone_backend.enums.AccountStatus;
import com.siteshkumar.zomato_clone_backend.enums.Role;
import com.siteshkumar.zomato_clone_backend.repository.mysql.UserRepository;

@Component
public class DataSeedRunner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("siteshk111@gmail.com").isEmpty()) {

            UserEntity admin = new UserEntity();
            admin.setName("Sitesh Kumar");
            admin.setEmail("siteshk111@gmail.com");
            admin.setPhone("6205268717");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setStatus(AccountStatus.APPROVED);

            userRepository.save(admin);

            System.out.println("Admin data seeded");
        }
    }
}
