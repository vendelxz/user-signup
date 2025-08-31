package com.usersignup.controller;

import com.usersignup.dtos.UserRequestDTO;
import com.usersignup.dtos.UserResponseDTO;
import com.usersignup.service.UserService;
import com.usersignup.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO response = userService.registerUser(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequestDTO dto) {
        Optional<UserResponseDTO> userOpt = userService.getAllUsers()
                .stream()
                .filter(u -> u.getEmail().equals(dto.getEmail()))
                .findFirst();

        if (userOpt.isPresent() && userService.checkPassword(dto.getSenha(), userOpt.get().getEmail())) {
            String token = jwtUtil.generateToken(dto.getEmail());
            return ResponseEntity.ok(token);
        }

        return ResponseEntity.status(401).body("Credenciais inválidas");
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
