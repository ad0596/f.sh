package com.client.app.AppClient.Service;

import com.client.app.AppClient.DTO.SenderReqData;
import com.client.app.AppClient.DTO.User;

public interface SenderService {

    public boolean reqReceiver(SenderReqData senderReqData);
    public void initFS(User rcvr);

}
