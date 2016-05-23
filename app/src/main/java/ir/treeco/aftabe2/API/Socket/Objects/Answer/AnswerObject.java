package ir.treeco.aftabe2.API.Socket.Objects.Answer;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 3/15/16.
 */
public class AnswerObject {

    @Expose
    String id;

    @Expose
    boolean correct = false;

    @Expose
    long delay;

    @Expose
    boolean skip = false;

    private long firstTime;

    public AnswerObject(String id) {
        this.id = id;
        setFirstTime();
    }

    public void setFirstTime() {
        firstTime = System.currentTimeMillis();
    }

    public void setCorrect() {
        correct = true;
        computeDelay();
    }

    private void computeDelay() {
        long diff = System.currentTimeMillis() - firstTime;
        delay = diff;
    }

    public void setSkip() {
        skip = true;
        computeDelay();
    }


}
