package com.licenta.bustravel.config;


import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public class UserInfoService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByOauthId(username);
        return user.map(UserInfo::new)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: "+ username+ " not found!"));
    }

    public UserDetails loadUserByOAuthId(String oAuthId) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByOauthId(oAuthId);
        return user.map(UserInfo::new)
                .orElseThrow(() -> new UsernameNotFoundException("User with oAuthId: "+ oAuthId+ " not found!"));
    }
}
