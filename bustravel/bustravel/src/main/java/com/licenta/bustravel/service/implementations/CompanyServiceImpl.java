package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.email.EmailSender;
import com.licenta.bustravel.model.CompanyEntity;
import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.repositories.CompanyRepository;
import com.licenta.bustravel.repositories.UserRepository;
import com.licenta.bustravel.service.CompanyService;
import com.licenta.bustravel.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public static String generatePassword() {
        String charLower = "abcdefghijklmnopqrstuvwxyz";
        String charUpper = charLower.toUpperCase();
        String digit = "0123456789";
        String specialChar = "!@#$%&'\"";

        String passwordChars = charLower + charUpper + digit + specialChar;

        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();

        // Adaugă câte un caracter din fiecare categorie pentru a asigura criteriile minime
        password.append(getRandomChar(charLower, random));
        password.append(getRandomChar(charUpper, random));
        password.append(getRandomChar(digit, random));
        password.append(getRandomChar(specialChar, random));

        // Adaugă caractere suplimentare până ajungem la cel puțin 10 caractere
        for (int i = 4; i < 10; i++) {
            password.append(getRandomChar(passwordChars, random));
        }

        // Shuffle the characters in the password to make it more random
        return shuffleString(password.toString(), random);
    }

    private static char getRandomChar(String source, SecureRandom random) {
        int randomIndex = random.nextInt(source.length());
        return source.charAt(randomIndex);
    }

    private static String shuffleString(String input, SecureRandom random) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int index = new SecureRandom().nextInt(i + 1);
            char temp = chars[index];
            chars[index] = chars[i];
            chars[i] = temp;
        }
        return new String(chars);
    }

    static String generateOwnerUsername(String ownerEmail) {
        String usernameBase = ownerEmail.substring(0, ownerEmail.indexOf('@'));
        int maxLength = 14;

        StringBuilder ownerUsername = new StringBuilder(usernameBase);

        // Ensure the username does not exceed maxLength
        if (ownerUsername.length() > maxLength) {
            ownerUsername.setLength(maxLength);
        } else {
            // If username is shorter than maxLength, append sequential characters
            int additionalChars = maxLength - ownerUsername.length();
            for (int i = 0; i < additionalChars; i++) {
                ownerUsername.append((char) ('a' + i % 26)); // Appends 'a' to 'z' sequentially
            }
        }

        return ownerUsername.toString();
    }



    public void validateUserType() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
        String username = authentication.getName();
        UserEntity userCurrent = userRepository.findByOauthId(username)
            .orElseThrow();
        if (!userCurrent.getUserType()
            .equals(UserType.ADMIN)) {
            throw new Exception("Not allowed.");
        }
    }
    @Override
    public void add(CompanyEntity companyEntity) throws Exception {
        validateUserType();
        if (companyEntity.isValid(companyEntity.getPhone(), companyEntity.getOwnerEmail())) {
            try {
                companyRepository.save(companyEntity);
                String ownerEmail = companyEntity.getOwnerEmail();
                String ownerUsername = generateOwnerUsername(ownerEmail);

                String password = generatePassword();
                UserEntity owner = UserEntity.builder()
                    .username(ownerUsername)
                    .password(password)
                    .name(companyEntity.getOwnerName())
                    .phone(companyEntity.getPhone())
                    .email(companyEntity.getOwnerEmail())
                    .userType(UserType.COMPANYEMPLOYEE)
                    .companyEntity(companyEntity)
                    .build();
//                owner.setPassword(passwordEncoder.encode(password));
                userService.add(owner);
                String to = companyEntity.getOwnerEmail();
                String subject = "New user registration";
                String body = "Hi, \n thanks for choosing to work with us! \n Here are your credentials: " + ownerEmail +"\n password: "+ password;
                EmailSender.sendEmail(to, subject, body);
            } catch (Exception e) {
                throw new Exception("Credentials already used");
            }
        } else {
            throw new Exception("Invalid credeantials.");
        }
    }

    @Override
    public Optional<CompanyEntity> getById(int id) throws Exception {
        validateUserType();
        return companyRepository.findById(id);
    }

    @Override
    public CompanyEntity getByName(String name) throws Exception {
        return companyRepository.getByName(name);
    }

    @Override
    public void modify(CompanyEntity companyEntity) throws Exception {
        validateUserType();
        companyRepository.save(companyEntity);
    }

    @Override
    public void delete(CompanyEntity companyEntity) throws Exception {
        validateUserType();
        companyRepository.delete(companyEntity);
    }

    @Override
    public List<CompanyEntity> getAll() throws Exception {
        validateUserType();
        return companyRepository.findAll();
    }
}
