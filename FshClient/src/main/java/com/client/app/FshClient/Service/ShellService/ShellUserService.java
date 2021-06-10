package com.client.app.FshClient.Service.ShellService;

import com.client.app.FshClient.Util.UserType;
import org.springframework.stereotype.Service;

@Service
public interface ShellUserService {
    Boolean isConnected();
    UserType myProfile();
    void setProfile(UserType profile);
}
