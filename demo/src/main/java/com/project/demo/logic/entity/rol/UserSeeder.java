package com.project.demo.logic.entity.rol;

import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createUser();
    }

    private void createUser() {
        // Definir los detalles del usuario inicial
        User userr = new User();
        userr.setName("Robert");
        userr.setLastname("Araya");
        userr.setEmail("user@gmail.com");
        userr.setPassword("user123");

        // Buscar el rol y verificar si el usuario ya existe
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        Optional<User> optionalUser = userRepository.findByEmail(userr.getEmail());

        // Si el rol no existe o el usuario ya existe, retornar
        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        // Corregir la inicialización del nuevo usuario
        userr.setPassword(passwordEncoder.encode(userr.getPassword())); // Cifrar la contraseña
        userr.setRole(optionalRole.get()); // Asignar el rol

        userRepository.save(userr); // Guardar el usuario en el repositorio
    }
}
