package vn.edu.nlu.edushare.edu_share.api.article.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.nlu.edushare.edu_share.api.article.dto.request.CreateLocationRequestDto;
import vn.edu.nlu.edushare.edu_share.api.article.dto.response.LocationResponseDto;
import vn.edu.nlu.edushare.edu_share.api.article.model.LocationDemo;
import vn.edu.nlu.edushare.edu_share.api.article.repository.LocationRepository;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationRepository locationRepository;

    // Lấy danh sách tất cả locations
    @GetMapping
    public ResponseEntity<List<LocationResponseDto>> getAllLocations() {
        List<LocationResponseDto> result = locationRepository.findAll()
                .stream()
                .map(l -> LocationResponseDto.builder()
                        .id(l.getId())
                        .areaName(l.getAreaName())
                        .latitude(l.getLatitude())
                        .longitude(l.getLongitude())
                        .build())
                .toList();
        return ResponseEntity.ok(result);
    }

    // Tạo location mới
    @PostMapping
    public ResponseEntity<LocationResponseDto> createLocation(
            @RequestBody CreateLocationRequestDto request
    ) {
        LocationDemo location = new LocationDemo();
        location.setAreaName(request.getAreaName());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());

        LocationDemo saved = locationRepository.save(location);

        return ResponseEntity.status(201).body(
                LocationResponseDto.builder()
                        .id(saved.getId())
                        .areaName(saved.getAreaName())
                        .latitude(saved.getLatitude())
                        .longitude(saved.getLongitude())
                        .build()
        );
    }
}