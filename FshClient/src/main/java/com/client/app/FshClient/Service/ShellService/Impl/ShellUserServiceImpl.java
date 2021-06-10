package com.client.app.FshClient.Service.ShellService.Impl;

import com.client.app.FshClient.Service.AppService.ReceiverService;
import com.client.app.FshClient.Service.AppService.SenderService;
import com.client.app.FshClient.Service.ShellService.ShellUserService;
import com.client.app.FshClient.Util.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShellUserServiceImpl implements ShellUserService {

    @Autowired
    SenderService senderService;
    @Autowired
    ReceiverService receiverService;

    private UserType profile;

    @Override
    public Boolean isConnected() {
        return senderService.isConnected() && receiverService.isConnected();
    }

    @Override
    public UserType myProfile() {
        return profile;
    }

    @Override
    public void setProfile(UserType profile) {
        this.profile = profile;
    }
}
