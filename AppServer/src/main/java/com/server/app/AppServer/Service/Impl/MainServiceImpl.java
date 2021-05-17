package com.server.app.AppServer.Service.Impl;

import com.server.app.AppServer.DTO.ReqData;
import com.server.app.AppServer.Service.MainService;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MainServiceImpl implements MainService {

    private static final Map<String, String> sm = new HashMap<>();
    private static final Map<String, String> rm = new HashMap<>();

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public boolean reqReceiver(ReqData reqData) {
        if(reqData != null && reqData.getSender() != null && reqData.getReceiver() != null) {
            if (sm.get(reqData.getSender().getId()) == null) {
                sm.put(reqData.getSender().getId(), reqData.getSender().getAddress());
                rm.put(reqData.getReceiver().getId(), reqData.getSender().getId());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean reqSender(ReqData reqData) {
        if(reqData != null && reqData.getSender() != null && reqData.getReceiver() != null) {
            if(rm.get(reqData.getReceiver().getId()).equals(reqData.getSender().getId()))

                //TODO: Notify sender that receiver is not available & share rcvr address to sender

                return true;
        }
        return false;
    }

}
