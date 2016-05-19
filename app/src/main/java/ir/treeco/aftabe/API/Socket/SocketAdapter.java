package ir.treeco.aftabe.API.Socket;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import ir.treeco.aftabe.API.Socket.Interfaces.FriendRequestListener;
import ir.treeco.aftabe.API.Socket.Interfaces.NotifListener;
import ir.treeco.aftabe.API.Socket.Interfaces.SocketFriendMatchListener;
import ir.treeco.aftabe.API.Socket.Interfaces.SocketListener;
import ir.treeco.aftabe.API.Socket.Interfaces.TimeLefTListener;
import ir.treeco.aftabe.API.Socket.Objects.Answer.AnswerObject;
import ir.treeco.aftabe.API.Socket.Objects.Friends.FriendRequestHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.FriendRequestResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchRequestHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchRequestSFHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchResponseHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.Friends.OnlineFriendStatusHolder;
import ir.treeco.aftabe.API.Socket.Objects.GameRequest.RequestHolder;
import ir.treeco.aftabe.API.Socket.Objects.GameResult.GameResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.GameStart.GameStartObject;
import ir.treeco.aftabe.API.Socket.Objects.Notifs.NotifCountHolder;
import ir.treeco.aftabe.API.Socket.Objects.Result.ResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.TimeLeftHolder;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.UserActionHolder;
import ir.treeco.aftabe.Util.Tools;

/**
 * Created by al on 3/14/16.
 */
public class SocketAdapter {

    private static ArrayList<SocketListener> listeners = new ArrayList<>();
    private static ArrayList<SocketFriendMatchListener> friendsListeners = new ArrayList<>();
    private static ArrayList<FriendRequestListener> requestLiseners = new ArrayList<>();
    private static ArrayList<NotifListener> notifListeners = new ArrayList<>();
    private static TimeLefTListener timeLefTListener;

    private static final Object lock = new Object();
    private static final Object friendsLock = new Object();
    private static final Object requestLock = new Object();
    private static final Object notifLock = new Object();

    private static Socket mSocket;
    private static Context mContext;

    private static final String TAG = "SocketAdapter";

    public static void setContext(Context context) {
        if (mContext == null)
            mContext = context;
    }

    public static void addNotifListener(NotifListener notifListener) {
        synchronized (notifLock) {
            notifListeners.add(notifListener);
        }
    }

    public static void removeNotifListener(NotifListener notifListener) {
        synchronized (notifLock) {
            notifListeners.remove(notifListener);
        }
    }

    private static void callNotifListners(NotifCountHolder countHolder) {
        synchronized (notifLock) {
            for (NotifListener listener : notifListeners)
                listener.onNewNotification(countHolder);

        }
    }

    public static void addFriendSocketListener(SocketFriendMatchListener socketListener) {
        synchronized (friendsLock) {
            friendsListeners.add(socketListener);
        }
    }

    public static void removeFriendSocketListener(SocketFriendMatchListener socketListener) {
        synchronized (friendsLock) {
            friendsListeners.remove(socketListener);
        }
    }

    public static void addFriendRequestListener(FriendRequestListener listener) {
        synchronized (requestLock) {
            requestLiseners.add(listener);
        }
    }


    public static void removeFriendRequestListener(FriendRequestListener listener) {
        synchronized (requestLock) {
            requestLiseners.remove(listener);
        }
    }

    public static void addSocketListener(SocketListener socketListener) {
        synchronized (lock) {
            listeners.add(socketListener);
        }
    }

    public static void removeSocketListener(SocketListener socketListener) {
        synchronized (lock) {
            listeners.remove(socketListener);
        }
    }

    private static void initSocket() {

        if (mSocket != null)
            return;

        if (Tools.getCachedUser(mContext) == null)
            return;

        Log.d(TAG, "initilizing socketa");

        String url = "https://aftabe2.com:2020";


        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = true;
//        opts.timeout = 30000;
        opts.query = "auth_token=" + Tools.getCachedUser(mContext).getLoginInfo().getAccessToken();


        try {


            mSocket = IO.socket(url, opts);


            final Gson gson = new Gson();
            mSocket.on("gameResult", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {

                    Log.d(TAG, "got");
                    Log.d(TAG, "gameResult is " + args[0].toString());
                    GameResultHolder gameResultHolder = gson.fromJson(args[0].toString(), GameResultHolder.class);
                    callGameRequestResult(gameResultHolder);


                }
            }).on("userActions", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String msg = args[0].toString();
                    Log.d(TAG, "user action is " + msg);
                    UserActionHolder userActionHolder = gson.fromJson(msg, UserActionHolder.class);
                    userActionHolder.update();
                    callGameActions(userActionHolder);

                }
            }).on("result", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String msg = args[0].toString();
                    Log.d(TAG, "result is " + msg);
                    ResultHolder resultHolder = gson.fromJson(msg, ResultHolder.class);
                    callGameResult(resultHolder);

                }
            }).on("gameStart", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String msg = args[0].toString();
                    Log.d(TAG, "gameStart is " + msg);
                    GameStartObject gameStartObject = gson.fromJson(msg, GameStartObject.class);
                    callGameStart(gameStartObject);
                }
            }).on("online", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String msg = args[0].toString();
//                    Log.d(TAG, "online is : " + msg);
                    OnlineFriendStatusHolder statusHolder = gson.fromJson(msg, OnlineFriendStatusHolder.class);
                    if (statusHolder.getStatus() != null)
                        callFriendStatusChanged(statusHolder);

                }
            }).on("matchSF", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    String msg = args[0].toString();
                    Log.d(TAG, "matchReqSF is : " + msg);
                    MatchRequestSFHolder requestHolder = gson.fromJson(msg, MatchRequestSFHolder.class);
                    callMatchRequest(requestHolder);

                }
            }).on("matchResult", new Emitter.Listener() {
                @Override
                public void call(Object... args) {


                    String msg = args[0].toString();
                    Log.d(TAG, "matchResult is : " + msg);
                    MatchResultHolder matchResultHolder = gson.fromJson(msg, MatchResultHolder.class);
                    callMatchResult(matchResultHolder);

                }
            }).on("friendSF", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    String msg = args[0].toString();
                    Log.d(TAG, "friendSF is : " + msg);
                    FriendRequestHolder friendRequestHolder = gson.fromJson(msg, FriendRequestHolder.class);
                    callFriendRequestListeners(friendRequestHolder);

                }
            }).on("notifWarn", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String msg = args[0].toString();
                    Log.d(TAG, "notifWarn is : " + msg);
                    NotifCountHolder countHolder = gson.fromJson(msg, NotifCountHolder.class);
                    callNotifListners(countHolder);
                }
            }).on("timerUpdate", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String msg = args[0].toString();
                    Log.d(TAG, "timerUpdate is : " + msg);
                    TimeLeftHolder timeLeftHolder = gson.fromJson(msg, TimeLeftHolder.class);
                    if (timeLefTListener != null)
                        timeLefTListener.onTime(timeLeftHolder.left);


                }
            }).on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    Log.d(TAG, "connected " + ((args.length != 0) ? args[0].toString() : ""));

                }
            }).on(Socket.EVENT_PING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
//                    Log.d(TAG, "ping " + ((args.length != 0) ? args[0].toString() : ""));

                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "disconnected " + ((args.length != 0) ? args[0].toString() : ""));
                }
            }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "error " + ((args.length != 0) ? args[0].toString() : ""));

                }
            }).on(Socket.EVENT_PONG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
//                    Log.d(TAG, "pong " + ((args.length != 0) ? args[0].toString() : ""));

                }
            }).on("cancelResult", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d(TAG, "cancel result " + args[0].toString());
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
        final String msg = gson.toJson(requestHolder);
        Log.d(TAG, "emit:gameRequest " + msg);

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket != null)
                    mSocket.emit("gameRequest", msg, new Ack() {
                        @Override
                        public void call(Object... args) {
                            Log.d(TAG, " got ack in game requeset ");

                        }
                    });
            }
        }).run();


    }

    public static void ping() {
        initSocket();
        if (mSocket == null)
            return;
        final Gson gson = new Gson();
        RequestHolder requestHolder = new RequestHolder();
        final String msg = gson.toJson(requestHolder);
        Log.d(TAG, "emit:ping " + msg);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket != null)

                    mSocket.emit(Socket.EVENT_PING, msg, new Ack() {
                        @Override
                        public void call(Object... args) {
                            Log.d(TAG, " got ack in pong " + ((args.length != 0) ? args[0].toString() : ""));

                        }
                    });
            }
        }).run();


    }

    private static void callFriendRequestListeners(FriendRequestHolder holder) {
        synchronized (requestLock) {
            for (FriendRequestListener listener : requestLiseners) {
                if (holder.isRequest())
                    listener.onFriendRequest(holder.getUser());
                else if (holder.isDecline())
                    listener.onFriendRequestReject(holder.getUser());
                else listener.onFriendRequestAccept(holder.getUser());
            }
        }

    }

    private static void callMatchRequest(MatchRequestSFHolder responseHolder) {

        synchronized (friendsLock) {
            for (SocketFriendMatchListener socket : friendsListeners)
                socket.onMatchRequest(responseHolder);

        }
    }


    private static void callMatchResult(MatchResultHolder responseHolder) {
        synchronized (friendsLock) {

            for (SocketFriendMatchListener socket : friendsListeners)
                socket.onMatchResultToSender(responseHolder);
        }
    }

    private static void callFriendStatusChanged(OnlineFriendStatusHolder responseHolder) {
        synchronized (friendsLock) {

            for (SocketFriendMatchListener socket : friendsListeners)
                socket.onOnlineFriendStatus(responseHolder);
        }
    }


    private static void callGameStart(GameStartObject gameStartObject) {

        synchronized (lock) {
            for (SocketListener socketListener : listeners)
                socketListener.onGameStart(gameStartObject);
        }

    }

    private static void callGameRequestResult(GameResultHolder gameResultHolder) {
        synchronized (lock) {
            for (SocketListener socketListener : listeners)
                socketListener.onGotGame(gameResultHolder);
        }
    }

    private static void callGameResult(ResultHolder resultHolder) {
        synchronized (lock) {
            for (SocketListener socketListener : listeners)
                socketListener.onFinishGame(resultHolder);
        }
    }

    private static void callGameActions(UserActionHolder userActionHolder) {
        synchronized (lock) {
            for (SocketListener socketListener : listeners)
                socketListener.onGotUserAction(userActionHolder);
        }
    }

    public static void setReadyStatus() {
        if (mSocket == null)
            return;


        RequestHolder requestHolder = new RequestHolder();
        Gson gson = new Gson();
        final String msg = gson.toJson(requestHolder);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "emit:ready " + msg);
                if (mSocket != null)

                    mSocket.emit("ready", msg);
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
                Log.d(TAG, "answerLevel : " + msg);
                if (mSocket != null)

                    mSocket.emit("answerLevel", msg, new Ack() {
                        @Override
                        public void call(Object... args) {
                            Log.d(TAG, "got asnwer level ack");
                        }
                    });
            }
        }).run();


    }

    public static void cancelRequest() {
        if (mSocket == null)
            return;
        RequestHolder requestHolder = new RequestHolder();
        Gson gson = new Gson();
        final String msg = gson.toJson(requestHolder);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket != null)

                    mSocket.emit("cancelRequest", msg);
            }
        }).run();

    }

    public static void requestToAFriend(final String friendId) {
        initSocket();

        new Thread(new Runnable() {
            @Override
            public void run() {

                MatchRequestHolder requestHolder = new MatchRequestHolder(friendId);
                Gson gson = new Gson();

                Log.d(TAG, "emit matchUS : " + gson.toJson(requestHolder));

                if (mSocket != null)

                    mSocket.emit("matchUS", gson.toJson(requestHolder));

            }
        }).start();
    }

    public static void responseToMatchRequest(final String friendId, final boolean accepted) {

        initSocket();

        new Thread(new Runnable() {
            @Override
            public void run() {

                MatchResponseHolder responseHolder = new MatchResponseHolder(friendId, accepted);
                Gson gson = new Gson();
                Log.d(TAG, "emit matchResponse : " + gson.toJson(responseHolder));
                if (mSocket != null)

                    mSocket.emit("matchResponse", gson.toJson(responseHolder));

            }
        }).start();
    }

    public static void requestOnlineFriendsStatus() {

        initSocket();
        new Thread(new Runnable() {
            @Override
            public void run() {

                RequestHolder requestHolder = new RequestHolder();
                Gson gson = new Gson();
                Log.d(TAG, "emit onlineRequest : " + gson.toJson(requestHolder));
                if (mSocket != null)
                    mSocket.emit("onlineRequest", gson.toJson(requestHolder));

            }
        }).start();

    }

    public static void answerFriendRequest(final String userId, final boolean accept) {
        initSocket();

        new Thread(new Runnable() {
            @Override
            public void run() {

                FriendRequestResultHolder holder = new FriendRequestResultHolder(userId, accept);
                Gson gson = new Gson();
                Log.d(TAG, "emit friendUS : " + gson.toJson(holder));
                if (mSocket != null)

                    mSocket.emit("friendUS", gson.toJson(holder));

            }
        }).start();
    }

    public static void disconnect() {
        if (mSocket == null)
            return;
        mSocket.disconnect();
    }

    public static void reconnect() {
        if (mSocket == null) {
            initSocket();
            return;
        }

        mSocket.connect();
    }

    public static TimeLefTListener getTimeLefTListener() {
        return timeLefTListener;
    }

    public static void setTimeLefTListener(TimeLefTListener timeLefTListener) {
        SocketAdapter.timeLefTListener = timeLefTListener;
    }
}
