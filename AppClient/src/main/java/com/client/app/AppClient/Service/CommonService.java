package com.client.app.AppClient.Service;

import com.client.app.AppClient.DTO.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CommonService {
    ResponseEntity<?> disconnect(User user);
}
