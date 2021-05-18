package com.server.app.AppServer.Service.Impl;

import com.server.app.AppServer.DTO.ReqData;
import com.server.app.AppServer.Service.MainService;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class MainServiceImpl implements MainService {

    private static final Map<String, String> sm = new HashMap<>();
    private static final Map<String, String> rm = new HashMap<>();

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public boolean reqReceiver(ReqData reqData) {
        if(reqData != null && reqData.getSender() != null && reqData.getReceiver() != null) {
            // Put data in data-maps
            sm.put(reqData.getSender().getId(), reqData.getSender().getAddress());
            rm.put(reqData.getReceiver().getId(), reqData.getSender().getId());

            // Schedule delete entries task after 2 minutes
            TimerTask removeMapEntries = new TimerTask() {
                @Override
                public void run() {
                    sm.remove(reqData.getSender().getId());
                    rm.remove(reqData.getReceiver().getId());
                }
            };
            Timer t = new Timer();
            t.schedule(removeMapEntries, 1000*60);
            return true;
        }
        return false;
    }

    @Override
    public boolean reqSender(ReqData reqData) {
        if(reqData != null && reqData.getSender() != null && reqData.getReceiver() != null) {
            if(rm.containsKey(reqData.getReceiver().getId()) && rm.get(reqData.getReceiver().getId()).equals(reqData.getSender().getId()))
                //TODO: Notify sender that receiver is not available & share rcvr address to sender

                return true;
        }
        return false;
    }

}
