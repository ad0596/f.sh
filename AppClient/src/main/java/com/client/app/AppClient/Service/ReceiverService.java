package com.client.app.AppClient.Service;

import com.client.app.AppClient.DTO.ReqData;
import org.springframework.stereotype.Service;

@Service
public interface ReceiverService {
    public boolean reqSender(ReqData rcvrReqData);
    public void initializeFileWriter();
    public boolean getShard(byte [] shard);
}
