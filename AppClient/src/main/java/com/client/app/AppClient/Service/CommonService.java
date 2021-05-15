package com.client.app.AppClient.Service;

import com.client.app.AppClient.DTO.User;
import org.springframework.stereotype.Service;

@Service
public interface CommonService {

    public void disconnect(User user);
}
