package com.licenta.bustravel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.licenta.bustravel.model.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique = true, nullable = false)
    private int id;

    @Column(name="username", unique=true)
    private String username;
    @Column(name="password")
    private String password;
    @Column(name="name")
    private String name;
    @Column(name="phone",unique=true)
    private String phone;
    @Column(name="email", unique=true)
    private String email;
    @Column(name="usertype")
    @Enumerated(EnumType.STRING)
    private UserType userType;
    @Column(name="token")
    private String token;

    @Column(name="oauth_id", unique=true)
    private String oauthId;

    @ManyToOne
    @JoinColumn(name="company_name", referencedColumnName = "name")
    private CompanyEntity companyEntity;
    @OneToMany(mappedBy = "userEntity",fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<BookingEntity> bookingList = new ArrayList<>();




    public boolean isValid(String phone, String email){
        if(phone.length() != 10)
            return false;

        boolean isPhoneValid = phone.matches("\\d+");
        boolean isEmailValid = email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        return isPhoneValid && isEmailValid;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
