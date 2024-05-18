package com.licenta.bustravel.config;


import com.licenta.bustravel.model.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserInfo implements UserDetails {
    private String oAuthId;
    private String password;
    private List<GrantedAuthority> authorityList;
    public UserInfo(UserEntity user){
        this.oAuthId = user.getOauthId();
        this.password = user.getPassword();
        this.authorityList = List.of(new SimpleGrantedAuthority(user.getUserType().toString()));
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList;
    }
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return oAuthId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
