package com.client.app.AppClient.Service;

import com.client.app.AppClient.DTO.User;
import org.springframework.stereotype.Service;

@Service
public interface ReceiverService {
    public boolean reqSender(User user);
    public boolean getShard(byte [] shard);
}
