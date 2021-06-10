package com.client.app.FshClient.Service.ShellService.Impl;

import com.client.app.FshClient.Service.ShellService.ConsoleService;
import org.jline.utils.AttributedString;
import org.springframework.stereotype.Component;

import java.io.PrintStream;

@Component
public class ConsoleServiceImpl implements ConsoleService {
    private final static String ANSI_RESET = "\u001b[0m";
    private final static String ANSI_BLACK = "\u001b[30m";
    private final static String ANSI_RED = "\u001b[31m";
    private final static String ANSI_GREEN = "\u001b[32m";
    private final static String ANSI_YELLOW = "\u001b[33m";
    private final static String ANSI_BLUE = "\u001b[34m";
    private final static String ANSI_MAGENTA = "\u001b[35m";
    private final static String ANSI_WHITE = "\u001b[37m";

    private final PrintStream out = System.out;

    // Force-write FshPrompt to console
    @Override
    public void write(AttributedString msg) {
        this.out.print(msg.toAnsi());
    }

    @Override
    public void write(String msg, boolean isAck) {
        this.out.print("\n> ");
        this.out.print(isAck ? ANSI_GREEN : ANSI_RED);
        this.out.print(msg);
        this.out.print(ANSI_RESET);
        this.out.println();
    }

    @Override
    public void writeACK(String msg) {
        this.out.print("> ");
        this.out.print(ANSI_GREEN);
        this.out.print(msg);
        this.out.print(ANSI_RESET);
        this.out.println();
    }

    @Override
    public void writeNACK(String msg) {
        this.out.print("> ");
        this.out.print(ANSI_RED);
        this.out.print(msg);
        this.out.print(ANSI_RESET);
        this.out.println();
    }

    @Override
    public void writeInfo(String msg) {
        String [] lines = msg.split("[\\r\\n]+");
        for(String line : lines) {
            this.out.print("\n> ");
            this.out.print(ANSI_YELLOW);
            this.out.print(line);
            this.out.print(ANSI_RESET);
        }
        this.out.println();
    }
}
