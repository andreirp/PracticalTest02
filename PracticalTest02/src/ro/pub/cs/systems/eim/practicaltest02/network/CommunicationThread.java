package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.AlarmInformation;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket != null) {
            try {
                BufferedReader bufferedReader = Utilities.getReader(socket);
                PrintWriter printWriter = Utilities.getWriter(socket);
                if (bufferedReader != null && printWriter != null) {
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (command set,hour,minute/reset/poll)!");
                    String command = bufferedReader.readLine();
                    String[] parts = command.split(";");
                    String cmd = parts[0];
                    
                    HashMap<String, AlarmInformation> data = serverThread.getData();
                    AlarmInformation alarmInformation = null;
                    if (command != null && !command.isEmpty()) {
                    	if (cmd.equals("set")) {
                    		String H = parts[1];
                    		String M = parts[2];
                    		Log.i(Constants.TAG, "[COMMUNICATION THREAD] Setting alarm to " + H + ":" + M);
                    		
                    		alarmInformation = new AlarmInformation(H, M);
                            serverThread.setData(Integer.toString(socket.getPort()), alarmInformation);
                    	} else if (cmd.equals("reset")) {
                    		Log.i(Constants.TAG, "[COMMUNICATION THREAD] Reseting alarm for port " + Integer.toString(socket.getPort()));
                    		serverThread.setData(Integer.toString(socket.getPort()), null);
                    	} else if (cmd.equals("poll")) {
                    		String client = Integer.toString(socket.getPort()); 
                    		if (!data.containsKey(client)) {
                                printWriter.println("none");
                                printWriter.flush();
                    		} else {
                                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                                HttpClient httpClient = new DefaultHttpClient();
                                HttpPost httpPost = new HttpPost(Constants.WEB_SERVICE_ADDRESS);
                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair(Constants.QUERY_ATTRIBUTE, "?"));
                                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                                httpPost.setEntity(urlEncodedFormEntity);
                                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                                String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                                if (pageSourceCode != null) {
                                	// check for active/inactive
                                } else {
                                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                                }
                    		}
                    	}

                    } else {
                        Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client!");
                    }
                } else {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] BufferedReader / PrintWriter are null!");
                }
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            } 
        } else {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
        }
    }

}
