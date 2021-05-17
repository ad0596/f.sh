package com.client.app.AppClient;

import com.client.app.AppClient.DTO.ReqData;
import com.client.app.AppClient.Service.Impl.ReceiverServiceImpl;
import com.client.app.AppClient.Service.ReceiverService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppClientApplication.class, args);
	}

}
