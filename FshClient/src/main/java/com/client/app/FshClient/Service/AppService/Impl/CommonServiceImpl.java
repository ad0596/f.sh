package com.client.app.FshClient.Service.AppService.Impl;

import com.client.app.FshClient.Service.AppService.CommonService;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

@Component
public class CommonServiceImpl implements CommonService {

    private static final Logger LOGGER = Logger.getLogger(CommonServiceImpl.class.getName());

    @Autowired
    private ServletWebServerApplicationContext webServerAppCtxt;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public ResponseEntity<?> networkInfo() {
        String localHost = "", publicHost = "";
        int applicationPort = 0;
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            applicationPort = webServerAppCtxt.getWebServer().getPort();
            localHost = inetAddress.getHostAddress();
            URL url = new URL("http://checkip.amazonaws.com");
            URLConnection urlConnection = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            publicHost = br.readLine();
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
            LOGGER.info(ex.getStackTrace().toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("applicationPort: " + applicationPort + "\nlocalHost: " + localHost + "\n publicHost: " + publicHost);
        }
        return ResponseEntity.status(HttpStatus.OK).body("applicationPort: " + applicationPort + "\nlocalHost: " + localHost + "\npublicHost: " + publicHost);
    }
}
