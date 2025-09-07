package dev.marianof.findmyvehicleapi.controller;

import dev.marianof.findmyvehicleapi.dto.UpdateCoordinatesDTO;
import dev.marianof.findmyvehicleapi.model.User;
import dev.marianof.findmyvehicleapi.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/updateCoordinates")
    public ResponseEntity<?> updateCoordinates(@RequestBody UpdateCoordinatesDTO updateCoordinatesDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        user.setLongitude(updateCoordinatesDTO.longitude());
        user.setLatitude(updateCoordinatesDTO.latitude());
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(users);
    }

    @GetMapping
    public ResponseEntity<?> getMyUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(user);
    }
}
