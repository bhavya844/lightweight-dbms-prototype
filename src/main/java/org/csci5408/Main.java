package org.csci5408;

;
import org.csci5408.auth.Authenticator;
import org.csci5408.cli.CLIHandler;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        Authenticator.authenticate();
        CLIHandler.run();

    }
}