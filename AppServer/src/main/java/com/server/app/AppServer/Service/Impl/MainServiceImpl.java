package com.server.app.AppServer.Service.Impl;

import com.server.app.AppServer.DTO.ReqData;
import com.server.app.AppServer.Service.MainService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

@Component
public class MainServiceImpl implements MainService {

    private static final Logger LOGGER = Logger.getLogger(MainServiceImpl.class.getName());

    @Value("${local}")
    private String conn;
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
                        ex.printStackTrace();
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
                // Notify sender that receiver is available
                try {
                    String url = conn + sm.get(reqData.getSender().getId()) + "/fshClient/s/connectionAck";
                    Request req = new Request.Builder()
                            .url(url)
                            .build();
                    Response resp = client.newCall(req).execute();
                    return ResponseEntity.status(resp.code()).body(resp.body().string());
                } catch (Exception ex) {
                    LOGGER.info(ex.toString());
                    ex.printStackTrace();
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("NACK: Sender not available.");
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NACK: Sender not available.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NACK: Incorrect Request-Body.");
    }

}
