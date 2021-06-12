package com.client.app.FshClient.Service.AppService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CommonService {
    ResponseEntity<?> networkInfo();
}
