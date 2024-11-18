package com.example.relayservice.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class CommandServiceImpl implements CommandService{

    @Override
    public String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            // On Windows, use cmd.exe to run the command
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Capture the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n"); // Accumulate the output
            }

            // Wait for the process to finish
            int exitCode = process.waitFor();
            output.append("Command executed with exit code: ").append(exitCode);

        } catch (Exception e) {
            e.printStackTrace();
            output.append("Error executing command: ").append(e.getMessage());
        }
        return output.toString(); // Return the accumulated output
    }
}
