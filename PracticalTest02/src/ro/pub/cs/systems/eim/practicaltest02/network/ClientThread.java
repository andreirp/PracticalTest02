package ro.pub.cs.systems.eim.practicaltest02.network;


import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String command;
    private TextView alarmTextView;

    private Socket socket;

    public ClientThread(
            String address,
            int port,
            String command,
            TextView alarmTextView) {
        this.address = address;
        this.port = port;
        this.command = command;
        this.alarmTextView = alarmTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader != null && printWriter != null) {
                printWriter.println(command);
                printWriter.flush();
                String returnInformation;
                while ((returnInformation = bufferedReader.readLine()) != null) {
                    final String finalizedWeatherInformation = returnInformation;
                    alarmTextView.post(new Runnable() {
                        @Override
                        public void run() {
                        	alarmTextView.append(finalizedWeatherInformation + "\n");
                        }
                    });
                }
            } else {
                Log.e(Constants.TAG, "[CLIENT THREAD] BufferedReader / PrintWriter are null!");
            }
            socket.close();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

}
