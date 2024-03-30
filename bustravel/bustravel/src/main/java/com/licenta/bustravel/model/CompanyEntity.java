package com.licenta.bustravel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "companies")
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "name", unique = true)
    private String name;
    @Column(name = "description")
    private String description;

    @Column(name =  "owner_name")
    private String ownerName;
    @Column(name = "owner_email")
    private String ownerEmail;
    @Column(name = "phone", unique = true)
    private String phone;

    @OneToMany(mappedBy = "companyEntity", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<UserEntity> employees = new ArrayList<>();

    @OneToMany(mappedBy = "companyEntity", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<RouteEntity> routes = new ArrayList<>();

    public boolean isValid(String phone, String ownerEmail){
        if(phone.length() != 10)
            return false;

        boolean isPhoneValid = phone.matches("\\d+");
        boolean isEmailValid = ownerEmail.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        return isPhoneValid && isEmailValid;
    }
}
