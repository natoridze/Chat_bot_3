package Javabot.controller;

import Javabot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/registration")
public class RegistrationController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> registration(@RequestParam("username") String username, @RequestParam("password") String password) {
        userService.registration(username, password);
        return ResponseEntity.ok().build();
    }
}