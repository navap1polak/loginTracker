package com.imubit.loginTracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("track")
@RestController
public class TrackerController {

    @Autowired
    private TrackerService trackerService;

    @GetMapping(value="/users-login", produces = "application/json")
    public
    List<String> getUsersLogin() {
        return StreamSupport.stream(trackerService.getUsers().spliterator(), false).map(user->user.getName()).collect(Collectors.toList());
    }

    @RequestMapping(method= RequestMethod.POST,
            value="/login",
            produces = APPLICATION_JSON_VALUE)
    public void usersLogin(@RequestBody User user) {
        trackerService.userLoggedIn(user);
    }

    @ExceptionHandler
    void handleIllegalArgumentException(
            IllegalArgumentException e,
            HttpServletResponse response) throws IOException {

        response.sendError(HttpStatus.BAD_REQUEST.value());

    }





}
