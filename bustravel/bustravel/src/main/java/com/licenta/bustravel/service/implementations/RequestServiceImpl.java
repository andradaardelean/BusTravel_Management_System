package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.model.CompanyEntity;
import com.licenta.bustravel.model.RequestEntity;
import com.licenta.bustravel.model.enums.RequestStatus;
import com.licenta.bustravel.repositories.RequestRepository;
import com.licenta.bustravel.service.CompanyService;
import com.licenta.bustravel.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService
{
    private final CompanyService companyService;
    private final RequestRepository requestRepository;
    @Override
    public void makeCompanyRequest(RequestEntity request, CompanyEntity companyEntity) throws Exception {
            String requestDetails = createRequestDetailsForCompany(companyEntity);
            request.setCreatedAt(LocalDateTime.now());
            request.setRequestDetails(requestDetails);
            request.setStatus(RequestStatus.CREATED);
            requestRepository.save(request);
    }

    @Override
    public void solveCompanyRequest(RequestEntity request) throws Exception {
        Map<String, String> requestDetails = stringToMap(request.getRequestDetails());
        CompanyEntity companyEntity = CompanyEntity.builder()
                .name(requestDetails.get("name"))
                .description(requestDetails.get("description"))
                .ownerName(requestDetails.get("ownerName"))
                .ownerEmail(requestDetails.get("ownerEmail"))
                .phone(requestDetails.get("phone"))
                .build();
        companyService.add(companyEntity);
        request.setStatus(RequestStatus.APPROVED);
        requestRepository.save(request);
    }

    private String createRequestDetailsForCompany(CompanyEntity companyEntity){
        Map<String, String> requestDetails = new HashMap<>();
        requestDetails.put("name", companyEntity.getName());
        requestDetails.put("description", companyEntity.getDescription());
        requestDetails.put("ownerName", companyEntity.getOwnerName());
        requestDetails.put("ownerEmail", companyEntity.getOwnerEmail());
        requestDetails.put("phone", companyEntity.getPhone());
        return requestDetails.toString();
    }

    public static Map<String, String> stringToMap(String mapAsString) {
        Map<String, String> map = new HashMap<>();
        // Remove the curly braces and any potential whitespace from the string
        String trimmedString = mapAsString.trim().substring(1, mapAsString.length() - 1).trim();

        if (!trimmedString.isEmpty()) {
            // Split the string by commas to separate out the key-value pairs
            String[] keyValuePairs = trimmedString.split(",");
            for (String pair : keyValuePairs) {
                // Split the pairs by equals sign to separate keys from values
                String[] entry = pair.split("=");
                // Trim key and value to remove any excess whitespace
                String key = entry[0].trim();
                String value = (entry.length > 1) ? entry[1].trim() : ""; // Handle missing values
                map.put(key, value);
            }
        }
        return map;
    }


    @Override
    public void modifyRequest(RequestEntity request) throws Exception {

    }

    @Override
    public void deleteRequest(RequestEntity request) throws Exception {

    }

    @Override
    public RequestEntity getRequestById(int id) throws Exception {
        return null;
    }

    @Override
    public List<RequestEntity> getAllRequests() {
        return null;
    }
}
