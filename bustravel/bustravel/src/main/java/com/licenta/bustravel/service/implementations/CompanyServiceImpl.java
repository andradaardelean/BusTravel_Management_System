package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.email.EmailSender;
import com.licenta.bustravel.model.CompanyEntity;
import com.licenta.bustravel.model.UserEntity;
import com.licenta.bustravel.model.enums.UserType;
import com.licenta.bustravel.repositories.CompanyRepository;
import com.licenta.bustravel.repositories.UserRepository;
import com.licenta.bustravel.service.CompanyService;
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
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public static String generatePassword() {
        String charLower = "abcdefghijklmnopqrstuvwxyz";
        String charUpper = charLower.toUpperCase();
        String digit = "0123456789";
        String specialChar = "!@#$%&'\"";

        String passwordChars = charLower + charUpper + digit + specialChar;

        StringBuilder password = new StringBuilder();
        password.append(getRandomChar(charLower));
        password.append(getRandomChar(charUpper));
        password.append(getRandomChar(digit));
        password.append(getRandomChar(specialChar));

        for (int i = 4; i < 8; i++) {
            password.append(getRandomChar(passwordChars));
        }

        // Shuffle the characters in the password to make it more random
        return shuffleString(password.toString());
    }

    private static char getRandomChar(String source) {
        int randomIndex = new SecureRandom().nextInt(source.length());
        return source.charAt(randomIndex);
    }

    private static String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int index = new SecureRandom().nextInt(i + 1);
            char temp = chars[index];
            chars[index] = chars[i];
            chars[i] = temp;
        }
        return new String(chars);
    }
    @Override
    public void add(CompanyEntity companyEntity) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity userCurrent = userRepository.findByUsername(username).orElseThrow();
        if(!userCurrent.getUserType().equals(UserType.ADMIN)) {
            throw new Exception("Not allowed.");
        }
        if (companyEntity.isValid(companyEntity.getPhone(), companyEntity.getOwnerEmail())) {
            try {
                companyRepository.save(companyEntity);
                String ownerEmail = companyEntity.getOwnerEmail();
                String ownerUsername = ownerEmail.substring(0, ownerEmail.indexOf('@'));
                String password = generatePassword();
                UserEntity owner = new UserEntity(0,ownerUsername, password, companyEntity.getOwnerName(), companyEntity.getPhone(), companyEntity.getOwnerEmail(), UserType.COMPANYEMPLOYEE, companyEntity, null);
                owner.setPassword(passwordEncoder.encode(password));
                userRepository.save(owner);
                String to = companyEntity.getOwnerEmail();
                String subject = "New user registration";
                String body = "Hi, \n thanks for choosing to work with us! \n Here are your credentials: " + ownerUsername +"\n password: "+ password;
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
        return companyRepository.findById(id);
    }

    @Override
    public CompanyEntity getByName(String name) throws Exception {
        return companyRepository.getByName(name);
    }

    @Override
    public void modify(CompanyEntity companyEntity) throws Exception {
        companyRepository.save(companyEntity);
    }

    @Override
    public void delete(CompanyEntity companyEntity) throws Exception {
        companyRepository.delete(companyEntity);
    }

    @Override
    public List<CompanyEntity> getAll() throws Exception {
        return companyRepository.findAll();
    }
}
