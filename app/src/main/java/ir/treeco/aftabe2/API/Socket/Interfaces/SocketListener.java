package ir.treeco.aftabe2.API.Socket.Interfaces;

import ir.treeco.aftabe2.API.Socket.Objects.GameResult.GameResultHolder;
import ir.treeco.aftabe2.API.Socket.Objects.GameStart.GameStartObject;
import ir.treeco.aftabe2.API.Socket.Objects.Result.ResultHolder;
import ir.treeco.aftabe2.API.Socket.Objects.UserAction.UserActionHolder;

/**
 * Created by al on 3/14/16.
 */
public interface SocketListener {


    void onGotGame(GameResultHolder gameHolder);

    void onGameStart(GameStartObject gameStartObject);

    void onGotUserAction(UserActionHolder actionHolder);

    void onFinishGame(ResultHolder resultHolder);



}
