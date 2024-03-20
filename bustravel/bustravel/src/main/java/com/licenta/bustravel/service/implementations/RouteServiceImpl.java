package com.licenta.bustravel.service.implementations;

import com.licenta.bustravel.entities.IntermediateRoutesEntity;
import com.licenta.bustravel.entities.StopEntity;
import com.licenta.bustravel.entities.RouteEntity;
import com.licenta.bustravel.entities.UserEntity;
import com.licenta.bustravel.entities.enums.UserType;
import com.licenta.bustravel.repositories.IntermediateRouteRepository;
import com.licenta.bustravel.repositories.StopsRepository;
import com.licenta.bustravel.repositories.RouteRepository;
import com.licenta.bustravel.repositories.UserRepository;
import com.licenta.bustravel.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    StopsRepository stopsRepository;
    @Autowired
    IntermediateRouteRepository intermediateRouteRepository;

    @Override
    public void add(List<RouteEntity> routeEntities, List<StopEntity> stops) throws Exception {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            UserEntity userCurrent = userRepository.findByUsername(username).get();
            if (userCurrent.getUserType().equals(UserType.CLIENT)) {
                throw new Exception("Not allowed.");
            }
        try {
            routeRepository.saveAll(routeEntities);
            for (StopEntity stop : stops) {
                stopsRepository.save(stop);
            }
            List<IntermediateRoutesEntity> intermediateRoutes = routeEntities.stream()
                    .flatMap(route -> stops.stream()
                            .map(stop -> {
                                IntermediateRoutesEntity intermediateRoute = new IntermediateRoutesEntity();
                                intermediateRoute.setId(0);
                                intermediateRoute.setRouteId(route.getId());
                                intermediateRoute.setStopId(stop.getId());
                                return intermediateRoute;
                            }))
                    .collect(Collectors.toList());
            intermediateRouteRepository.saveAll(intermediateRoutes);
        }catch(Exception ex){
            throw new Exception("Add failed!");
        }
    }

    @Override
    public Optional getById(int id) throws Exception {
        return routeRepository.findById(id);
    }

    @Override
    public void modify(RouteEntity routeEntity, List<StopEntity> stops) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity userCurrent = userRepository.findByUsername(username).get();
        if(userCurrent.getUserType().equals(UserType.CLIENT)) {
            throw new Exception("Not allowed.");
        }

        try{
            routeRepository.save(routeEntity);
            // addauga save la stops
//            for (StopEntity stop : stops) {
//                stop.setRouteEntity(routeEntity);
//                stopsRepository.save(stop);
//            }
        }catch(Exception e){
            throw new Exception("Modify failed!");
        }
    }

    @Override
    public void delete(RouteEntity routeEntity) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity userCurrent = userRepository.findByUsername(username).get();
        if(userCurrent.getUserType().equals(UserType.CLIENT)) {
            throw new Exception("Not allowed.");
        }

        try{
            // add delete stops si stergere recursiva
            routeRepository.delete(routeEntity);
        }catch(Exception e){
            throw new Exception("Delete failed!");
        }
    }

    @Override
    public List<RouteEntity> getAll() throws Exception {
        return routeRepository.findAll();
    }

    public List<StopEntity> getAllStops(){
        return stopsRepository.findAll();
    }

    public List<StopEntity> getStopsForRoute(RouteEntity routeEntity){
        List<StopEntity> stops = new ArrayList<>();
        for(StopEntity stop : stopsRepository.findAll()){
            stops.add(stop);
        }
        return stops;
    }
}
