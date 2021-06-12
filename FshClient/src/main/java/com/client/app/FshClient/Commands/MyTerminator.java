package com.client.app.FshClient.Commands;

import com.client.app.FshClient.Service.ShellService.ConsoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;

@ShellComponent
public class MyTerminator implements Quit.Command {

    @Autowired
    ConsoleService console;
    @ShellMethod(value = "Exit the shell.", key = {"quit", "exit", "terminate"}, group = "Built-In Commands")
    public void quit() {
        console.writeInfo("Exiting the Application\nGood Bye!!!!");
        System.out.println();
        System.exit(0);
    }
}
