package home.animals;

import android.os.CountDownTimer;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Дима on 20.03.2017.
 *
 */

class NewNode implements Callable<String>
{
    private static final String NODE_POST = "https://dimazdy82.000webhostapp.com/animals/"; // "http://10.0.2.2/animals/";
    private static NewNode newNode;
    private Future<String> future;
    private JSONObject json;
    private Callback callback;

    interface Callback{
        void onErrorCreating(String msg);
    }

    @Override public String call()
    {
        try {
            return postJSON();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private String postJSON() throws IOException
    {
        String params = "new=" + json.toString();
        URL url = new URL(NODE_POST);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try{
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Length", Integer.toString(params.getBytes().length));
            OutputStream out = connection.getOutputStream();
            out.write(params.getBytes("UTF-8"));
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IOException(connection.getResponseMessage() + ": with " + NODE_POST);

            ByteArrayOutputStream response = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) response.write(buffer, 0, bytesRead);
            response.close();
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    static void newNode(Game game, int currentId, boolean yesPointer, Node node)
    {
        newNode = new NewNode();
        newNode.callback = game;
        newNode.json = new JSONObject();
        try {
            // Insert new node
            newNode.json.put("question", node.question);
            newNode.json.put("answear", node.answear);

            // Update yes or no pointer via YesPointer at current node
            newNode.json.put("current", currentId);
            newNode.json.put("yes", yesPointer?1:0);

            new CountDownTimer(Integer.MAX_VALUE, 100)
            {
                String response;
                @Override public void onFinish() {}

                @Override public void onTick(long millisUntilFinished)
                {
                    try {
                        response = newNode.future.get();
                        if(!response.equals("OK")) throw new IOException(response);
                    } catch (InterruptedException | ExecutionException | IOException e) {
                        String error;
                        error = "Response:" + response;
                        Log.d("NewNode", error);
                        newNode.callback.onErrorCreating(error);
                    }
                    this.cancel();
                }
            }.start();

            newNode.future = Game.EXECUTOR.submit(newNode);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
