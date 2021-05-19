package com.server.app.AppServer.Service.Impl;

import com.server.app.AppServer.DTO.ReqData;
import com.server.app.AppServer.DTO.User;
import com.server.app.AppServer.Service.MainService;
import okhttp3.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private Timer t;

    @Override
    public ResponseEntity<?> reqReceiver(ReqData reqData) {
        if(reqData != null && reqData.getSender() != null && reqData.getReceiver() != null) {
            // Put data in data-maps
            sm.put(reqData.getSender().getId(), reqData.getSender().getAddress());
            rm.put(reqData.getReceiver().getId(), reqData.getSender().getId());

            // Schedule task to delete entries after 2 minutes
            TimerTask removeMapEntries = new TimerTask() {
                @Override
                public void run() {
                    sm.remove(reqData.getSender().getId());
                    rm.remove(reqData.getReceiver().getId());
                }
            };
            t = new Timer();
            t.schedule(removeMapEntries, 1000*60);
            return ResponseEntity.status(HttpStatus.OK).body("ACK");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NACK: Incorrect with Request body.");
    }

    @Override
    public ResponseEntity<?> reqSender(ReqData reqData) {
        if(reqData != null && reqData.getSender() != null && reqData.getReceiver() != null) {
            if(rm.containsKey(reqData.getReceiver().getId()) && rm.get(reqData.getReceiver().getId()).equals(reqData.getSender().getId()))
                //TODO: Notify sender that receiver is not available & share rcvr address to sender

                return ResponseEntity.status(HttpStatus.OK).body("ACK");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NACK: Incorrect with Request body.");
    }

    @Override
    public ResponseEntity<?> disconnect(User user) {
        if(user != null && user.getId() != null) {
            // TODO: Implement disconnect logic
            return ResponseEntity.status(HttpStatus.OK).body("ACK");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: Error while disconnecting.");
    }

}
