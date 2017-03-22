package home.animals;

import android.os.CountDownTimer;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Дима on 19.03.2017.
 *
 *
 */

class FetchNode implements Callable<String>
{
    private int id;
    private static final String NODE_GET = "https://dimazdy82.000webhostapp.com/animals/?next=%d"; //"http://10.0.2.2/animals/?next=%d";
    private Callback callback;
    private static FetchNode fetchNode;
    private Future<String> future;

    interface Callback{
        void onFetchNode(Node node);
    }

    @Override public String call()
    {
        try{
            return getJSONString(String.format(Locale.getDefault(), NODE_GET, id));
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private Node getFrom(JSONObject json) throws JSONException
    {
        Node node = new Node();

        node.id = json.isNull("node_id")? 0: json.getInt("node_id");
        node.question = json.isNull("question")? "": json.getString("question");
        node.answear = json.isNull("answear")? "": json.getString("answear");
        node.yesId = json.isNull("yes")? 0: json.getInt("yes");
        node.noId = json.isNull("no")? 0: json.getInt("no");
        return node;
    }

    private String getJSONString(String urlString) throws IOException
    {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try{
            connection.setConnectTimeout(3000);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IOException(connection.getResponseMessage() + ": with " + urlString);

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) out.write(buffer, 0, bytesRead);
            out.close();
            return out.toString();
        } finally {
            connection.disconnect();
        }
    }

    static void fetch(Game game, int nodeId)
    {
        fetchNode = new FetchNode();
        fetchNode.callback = game;
        fetchNode.id = nodeId;

        new CountDownTimer(Integer.MAX_VALUE, 100)
        {
            String response;
            @Override public void onFinish(){}
            @Override public void onTick(long millisUntilFinished)
            {
                if(fetchNode.future != null && fetchNode.future.isDone())
                {
                    try{
                        response = fetchNode.future.get();
                        if(response.contains("Error")) throw new IOException(response);

                        fetchNode.future = null;
                        JSONObject json = new JSONObject(response);
                        Node node = fetchNode.getFrom(json);
                        fetchNode.callback.onFetchNode(node);

                    } catch (InterruptedException | ExecutionException | JSONException | IOException e)
                    {
                        Log.d("FetchNode","Response:"+ response);
                        Log.d("NewNode", "Cause:" + e.getCause());
                    }
                    this.cancel();
                }
            }
        }.start();

       fetchNode.future = Game.EXECUTOR.submit(fetchNode);
    }
}