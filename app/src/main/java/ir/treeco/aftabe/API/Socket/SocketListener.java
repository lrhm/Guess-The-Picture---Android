package ir.treeco.aftabe.API.Socket;

import ir.treeco.aftabe.API.Socket.Objects.GameResult.GameResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.Result.ResultHolder;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.GameActionResult;
import ir.treeco.aftabe.API.Socket.Objects.Result.ScoreResult;
import ir.treeco.aftabe.API.Socket.Objects.UserAction.UserActionHolder;
import ir.treeco.aftabe.Object.Level;
import ir.treeco.aftabe.Object.User;

/**
 * Created by al on 3/14/16.
 */
public interface SocketListener {


    void onGotGame(GameResultHolder gameHolder);

    void onGotUserAction(UserActionHolder actionHolder);

    void onFinishGame(ResultHolder resultHolder);

}
