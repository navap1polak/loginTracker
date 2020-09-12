package com.imubit.loginTracker.service;

import com.imubit.loginTracker.model.User;
import com.imubit.loginTracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrackerService {

    @Autowired
    private SimpMessagingTemplate webSocketSender;

    @Autowired
    private UserRepository userRepository;

    public Page<User> getUsers(int page, int size){
        Pageable pagingSort = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pagingSort);

        return userPage;

    }

    public List<String> getAllUsers(){
        return userRepository.findAll().stream().map(u->u.getName()).collect(Collectors.toList());
    }

    public void userLoggedIn(String user){
        userRepository.save(new User(user));
    }

    public void newLoginUsers(List<String> loginUsers){
        if(loginUsers != null && !loginUsers.isEmpty()) {
            loginUsers.forEach(u->webSocketSender.convertAndSend("/topic/logins", u));
            userRepository.saveAll(loginUsers.stream().map(s -> new User(s)).collect(Collectors.toList()));
        }

    }

}
