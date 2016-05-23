package ir.treeco.aftabe2.API.Socket.Objects.UserAction;

/**
 * Created by al on 3/14/16.
 */
public class GameActionResult {


    private Result mResult;


    public GameActionResult(String action) {
        if (action.equals("skip")) {
            mResult = Result.Skip;
        } else if (action.equals("wrong")) {
            mResult = Result.Wrong;
        } else if (action.equals("correct")) {
            mResult = Result.Correct;
        }
    }

    public Result getResult() {
        return mResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameActionResult that = (GameActionResult) o;

        return mResult == that.mResult;

    }

    @Override
    public int hashCode() {
        return mResult != null ? mResult.hashCode() : 0;
    }

    public boolean isSkiped() {
        return mResult == Result.Skip;
    }

    public boolean isWrong() {
        return mResult == Result.Wrong;
    }

    public boolean isCorrect() {
        return mResult == Result.Correct;
    }

    public enum Result {
        Skip, Wrong, Correct
    }


}
