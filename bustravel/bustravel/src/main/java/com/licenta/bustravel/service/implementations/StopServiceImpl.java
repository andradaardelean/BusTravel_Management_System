package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.model.StopEntity;
import com.licenta.bustravel.repositories.StopsRepository;
import com.licenta.bustravel.service.StopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StopServiceImpl implements StopService {
    private final StopsRepository stopsRepository;
    @Override
    public List<StopEntity> getAllStops() {
        return stopsRepository.findAll();
    }
}
