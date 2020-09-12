package com.imubit.loginTracker.controller;

import com.imubit.loginTracker.model.User;
import com.imubit.loginTracker.service.TrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("api")
@RestController
public class TrackerController {

    @Autowired
    private TrackerService trackerService;

    @GetMapping("/users-login")
    public
    ResponseEntity<Map<String, Object>> getUsersLogin(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "3") int size) {
        Page<User> userPage = trackerService.getUsers(page,size);
        if (userPage.getContent().isEmpty()) {
            return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else {
            Map<String, Object> response = new HashMap<>();
            response.put("users", userPage.getContent().stream().map(user->user.getName()).collect(Collectors.toList()));
            response.put("currentPage", userPage.getNumber());
            response.put("totalItems", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping("allUsers")
    public List<String> getAllUsers(){
        return trackerService.getAllUsers();
    }


    @ExceptionHandler
    void handleIllegalArgumentException(
            IllegalArgumentException e,
            HttpServletResponse response) throws IOException {

        response.sendError(HttpStatus.BAD_REQUEST.value());

    }





}
