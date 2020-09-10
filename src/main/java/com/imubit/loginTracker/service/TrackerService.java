package com.imubit.loginTracker.service;

import com.imubit.loginTracker.model.User;
import com.imubit.loginTracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TrackerService {

    @Autowired
    private UserRepository userRepository;

    public Page<User> getUsers(int page, int size){
        Pageable pagingSort = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pagingSort);

        return userPage;

    }


    private List<String> newLogins;

    public void userLoggedIn(String user){
        userRepository.save(new User(user));
    }

    public List<String> getRecentLogins(){
        if(newLogins != null &!newLogins.isEmpty()){
            List<String> ret  = new ArrayList<String>(newLogins);
            newLogins.clear();
            return ret;
        }
        return new ArrayList<>();
    }
}
