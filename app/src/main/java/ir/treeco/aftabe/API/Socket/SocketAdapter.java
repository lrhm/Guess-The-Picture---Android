package ir.treeco.aftabe.API.Socket;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import ir.treeco.aftabe.API.Socket.Objects.Answer.AnswerObject;
import ir.treeco.aftabe.API.Socket.Objects.GameRequest.RequestHolder;
import ir.treeco.aftabe.API.Socket.Objects.GameResult.GameResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.Result.ResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.UserActionHolder;
import ir.treeco.aftabe.Util.Tools;

/**
 * Created by al on 3/14/16.
 */
public class SocketAdapter {

    private static ArrayList<SocketListener> listeners = new ArrayList<>();

    private static Socket mSocket;
    private static Context mContext;

    private static final String TAG = "SocketAdapter";

    public static void setContext(Context context) {
        if (mContext == null)
            mContext = context;
    }

    public static void addSocketListener(SocketListener socketListener) {
        listeners.add(socketListener);
    }

    public void removeSocketListener(SocketListener socketListener) {
        listeners.remove(socketListener);
    }

    private static void initSocket() {

        if (mSocket != null)
            return;
        Log.d(TAG, "initilizing socketa");

        String url = "https://aftabe2.com:2020";

        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = true;
        opts.query = "auth_token=" + Tools.getCachedUser().getLoginInfo().getAccessToken();

        try {
            mSocket = IO.socket(url, opts);

            final Gson gson = new Gson();
            mSocket.on("gameResult", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String msg = (String) args[0];
                    Log.d(TAG, "gameResult is " + msg);
                    GameResultHolder gameResultHolder = gson.fromJson(msg, GameResultHolder.class);
                    callGameRequestResult(gameResultHolder);

                }
            }).on("userActions", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String msg = (String) args[0];
                    Log.d(TAG, "user action is " + msg);
                    UserActionHolder userActionHolder = gson.fromJson(msg, UserActionHolder.class);
                    userActionHolder.update();
                    callGameActions(userActionHolder);

                }
            }).on("result", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String msg = (String) args[0];
                    Log.d(TAG, "result is " + msg);
                    ResultHolder resultHolder = gson.fromJson(msg, ResultHolder.class);
                    resultHolder.update();
                    callGameResult(resultHolder);

                }
            }).on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    Log.d(TAG, "connected " + ((args.length != 0 ) ? args[0].toString() : "" ));

                }
            }).on(Socket.EVENT_PING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "ping " + ((args.length != 0 ) ? args[0].toString() : "" ));

                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "disconnected");
                }
            });
            mSocket.connect();


            Log.d(TAG, "try to connect");


        } catch (URISyntaxException e) {
            e.printStackTrace();
            mSocket = null;
            Toast.makeText(mContext, "could not connect to server ", Toast.LENGTH_SHORT).show();
        }

    }

    public static void requestGame() {
        initSocket();
        if (mSocket == null)
            return;
        final Gson gson = new Gson();
        RequestHolder requestHolder = new RequestHolder();
        String msg = gson.toJson(requestHolder);
        Log.d(TAG, "emit:gameRequest " + msg);

        mSocket.emit("gameRequest", msg, new Ack() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, " got ack in game requeset");

            }
        });


    }

    private static void callGameRequestResult(GameResultHolder gameResultHolder) {
        for (SocketListener socketListener : listeners)
            socketListener.onGotGame(gameResultHolder);
    }

    private static void callGameResult(ResultHolder resultHolder) {
        for (SocketListener socketListener : listeners)
            socketListener.onFinishGame(resultHolder);
    }

    private static void callGameActions(UserActionHolder userActionHolder) {
        for (SocketListener socketListener : listeners)
            socketListener.onGotUserAction(userActionHolder);
    }

    private static void setReadyStatus() {
        if (mSocket == null)
            return;

        RequestHolder requestHolder = new RequestHolder();
        Gson gson = new Gson();
        final String msg = gson.toJson(requestHolder);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mSocket.emit("ready", msg, new Ack() {
                    @Override
                    public void call(Object... args) {
                        Log.d(TAG, "got ready ack");
                    }
                });
            }
        }).run();


    }

    public static void setAnswerLevel(AnswerObject answerObject) {
        if (mSocket == null)
            return;

        Gson gson = new Gson();
        final String msg = gson.toJson(answerObject);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mSocket.emit("answerLevel", msg, new Ack() {
                    @Override
                    public void call(Object... args) {
                        Log.d(TAG, "got asnwer level ack");
                    }
                });
            }
        }).run();


    }


}
