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
public class RequestServiceImpl implements RequestService {
    private final CompanyService companyService;
    private final RequestRepository requestRepository;
    @Override
    public void makeCompanyRequest(RequestEntity request, CompanyEntity companyEntity) throws Exception {
        String requestDetails = createRequestDetailsForCompany(companyEntity);
        request.setCreatedAt(LocalDateTime.now());
        request.setRequestDetails(requestDetails);
        request.setStatus(RequestStatus.PENDING);
        requestRepository.save(request);
    }

    @Override
    public void solveCompanyRequest(RequestEntity request) throws Exception {
        RequestEntity requestEntity = requestRepository.findById(request.getId())
            .orElseThrow(() -> new Exception("Request not found"));
        if(request.getStatus().equals(RequestStatus.APPROVED)) {
            Map<String, String> requestDetails = stringToMap(requestEntity.getRequestDetails());
            CompanyEntity companyEntity = CompanyEntity.builder()
                .name(requestDetails.get("name"))
                .description(requestDetails.get("description"))
                .ownerName(requestDetails.get("ownerName"))
                .ownerEmail(requestDetails.get("ownerEmail"))
                .phone(requestDetails.get("phone"))
                .build();
            try {
                companyService.add(companyEntity);
                requestEntity.setStatus(RequestStatus.APPROVED);
                requestRepository.save(requestEntity);
            } catch (Exception e) {
                requestEntity.setRequestDetails(e.getMessage());
                requestRepository.save(requestEntity);
                throw new Exception("Company details are not valid.");
            }
        } else if (request.getStatus().equals(RequestStatus.REJECTED)) {
            requestEntity.setStatus(RequestStatus.REJECTED);
            requestRepository.save(requestEntity);
        }
    }

    @Override
    public void rejectCompanyRequest(RequestEntity request) throws Exception {
        RequestEntity requestEntity = requestRepository.findById(request.getId())
            .orElseThrow(() -> new Exception("Request not found"));
        requestEntity.setStatus(RequestStatus.REJECTED);
        requestRepository.save(requestEntity);
    }

    private String createRequestDetailsForCompany(CompanyEntity companyEntity) {
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
        String trimmedString = mapAsString.trim()
            .substring(1, mapAsString.length() - 1)
            .trim();

        if (!trimmedString.isEmpty()) {
            String[] keyValuePairs = trimmedString.split(",");
            for (String pair : keyValuePairs) {
                String[] entry = pair.split("=");
                String key = entry[0].trim();
                String value = (entry.length > 1) ? entry[1].trim() : "";
                map.put(key, value);
            }
        }
        return map;
    }


    @Override
    public void modifyRequest(RequestEntity request) throws Exception {
        requestRepository.save(request);
    }

    @Override
    public void deleteRequest(RequestEntity request) throws Exception {
        requestRepository.delete(request);
    }

    @Override
    public RequestEntity getRequestById(int id) throws Exception {
        return requestRepository.findById(id)
            .orElseThrow(() -> new Exception("Request not found"));
    }

    @Override
    public List<RequestEntity> getAllRequests(String status) {
        if(status.equals("ALL"))
            return requestRepository.findAll();
        else{
            return requestRepository.findAllByStatus(RequestStatus.valueOf(status));
        }
    }
}
