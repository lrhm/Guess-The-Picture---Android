package ir.treeco.aftabe2.API.Socket.Objects.Result;

/**
 * Created by al on 3/15/16.
 */
public class ScoreStatus {

    public enum State {
        TimeOut, Finish
    }

    public String status;

    State state;

    public void update() {
        if (status.equals("timeout")) {
            state = State.TimeOut;
        }
        else if (state.equals("finish")) {
            state = State.Finish;
        }
    }

    public State getState() {
        return state;
    }

    public boolean isFinished() {
        return state == State.Finish;
    }

    public boolean isTimedOut() {
        return state == State.TimeOut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScoreStatus that = (ScoreStatus) o;

        return state == that.state;

    }

}
