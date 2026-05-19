package com.eduardo.geolocation_h3uber.services;

import com.uber.h3core.H3Core;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeolocationService {

    private final H3Core h3Core;

    /**
     * Finds nearby H3 indexes within a given radius (k-ring).
     *
     * @param h3Index The central H3 index.
     * @param radius  The radius in hexagons.
     * @return A list of neighboring H3 indexes.
     */
    public List<String> findNeighbors(String h3Index, int radius) {
        return h3Core.gridDisk(h3Index, radius);
    }
}
