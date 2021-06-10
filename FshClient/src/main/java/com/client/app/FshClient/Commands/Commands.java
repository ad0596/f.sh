package com.client.app.FshClient.Commands;

import com.client.app.FshClient.Service.ShellService.ConsoleService;
import com.client.app.FshClient.Service.ShellService.ShellUserService;
import com.client.app.FshClient.Util.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.shell.standard.EnumValueProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class Commands {

    @Autowired
    Environment environment;
    @Autowired
    private ConsoleService console;
    @Autowired
    ShellUserService shellUserService;

    @ShellMethod(value = "Know your Address")
    public void myAddr() {
        console.writeACK("localhost:" + environment.getProperty("local.server.port"));
    }


    @ShellMethod(value = "Set User-Profile [SENDER / RECEIVER]")
    public void setProfile(@ShellOption(valueProvider = EnumValueProvider.class) UserType profile) {
        shellUserService.setProfile(profile);
    }
}
