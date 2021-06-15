package com.server.app.FshServer.Service.Impl;

import com.server.app.FshServer.DTO.ReqData;
import com.server.app.FshServer.DTO.User;
import com.server.app.FshServer.Service.MainService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class MainServiceImpl implements MainService {

    private static final Logger LOGGER = Logger.getLogger(MainServiceImpl.class.getName());

    @Value("${conn}")
    private String conn;
    private static final Map<String, String> sm = new ConcurrentHashMap<>();
    private static final Map<String, String> rm = new ConcurrentHashMap<>();

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
                    LOGGER.info("Disconnecting Sender. [Reason: Receiver not found for 2min]");
                    // Give disconnect alert to sender
                    try {
                        String url = conn + reqData.getSender().getAddress() + "/fshClient/s/disconnectAck";
                        Request req = new Request.Builder()
                                .url(url)
                                .build();
                        client.newCall(req).execute();
                    } catch (Exception ex) {
                        LOGGER.info(ex.toString());
                        LOGGER.info(ex.getStackTrace().toString());
                    }
                }
            };
            t = new Timer();
            t.schedule(removeMapEntries, 1000*120); // 2min timer
            return ResponseEntity.status(HttpStatus.OK).body("ACK");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NACK: Incorrect with Request body.");
    }

    @Override
    public ResponseEntity<?> reqSender(ReqData reqData) {
        if(reqData != null && reqData.getSender() != null && reqData.getReceiver() != null) {
            if(rm.containsKey(reqData.getReceiver().getId()) && rm.get(reqData.getReceiver().getId()).equals(reqData.getSender().getId())) {
                t.cancel();
                String reqDataJson = reqData.getReceiver().toString();
                RequestBody reqBody = RequestBody.create(reqDataJson, MediaType.parse("application/json"));
                // Notify sender that receiver is available
                try {
                    String url = conn + sm.get(reqData.getSender().getId()) + "/fshClient/s/connectionAck";
                    Request req = new Request.Builder()
                            .url(url)
                            .post(reqBody).build();
                    Response resp = client.newCall(req).execute();
                    int respCode = resp.code();
                    String respBody = resp.body().string();
                    if(respCode == 200) {
                        User sndr = new User();
                        sndr.setId(reqData.getSender().getId());
                        sndr.setAddress(sm.get(reqData.getSender().getId()));
                        sm.remove(reqData.getSender().getId());
                        rm.remove(reqData.getReceiver().getId());
                        return ResponseEntity.status(respCode).body(sndr.toString());
                    } else
                        return ResponseEntity.status(respCode).body(respBody);
                } catch (Exception ex) {
                    LOGGER.info(ex.toString());
                    LOGGER.info(ex.getStackTrace().toString());
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: Sender not available.");
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NACK: Sender not available.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NACK: Incorrect Request-Body.");
    }

}
