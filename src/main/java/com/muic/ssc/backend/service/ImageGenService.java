package com.muic.ssc.backend.service;

import com.muic.ssc.backend.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Author: Pawin Pothasuthon
 */
@Service
public class ImageGenService {

    private static final Logger logger = LoggerFactory.getLogger(ImageGenService.class);

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private RestTemplate restTemplate;

    // Placeholder for API URL - to be configured in application properties
    private String imageGenerationApiUrl;
    private String apiKey;
}