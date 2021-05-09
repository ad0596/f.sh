package com.client.app.AppClient.Service.Impl;

import com.client.app.AppClient.DTO.User;
import com.client.app.AppClient.Service.CommonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpRequest;

@Service
public class CommonSeerviceImpl implements CommonService {

    @Value("${serverAddress}")
    private static final String serverAddress = null;

    @Override
    public void disconnect(User user) {
        //req server to get disconnected
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("content-typ", "application/json")
                    .uri(new URI("http://" + serverAddress + "/disconnect"))
                    .GET()
                    .build();

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

}
