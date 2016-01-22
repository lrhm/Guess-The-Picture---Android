package ir.treeco.aftabe.Object;

/**
 * Created by al on 12/26/15.
 */
public class User {
    private String userName;
    private int mark;
    private int rank;
    public User(String userName, int mark , int rank){
        this.userName = userName;
        this.mark = mark;
        this.rank = rank;
    }

    public User(String userName, int mark ){
        this.userName = userName;
        this.mark = mark;
    }

    public int getRank(){
        return rank;
    }

    public String getUserName(){
        return userName;
    }

    public int getMark() {
        return mark;
    }
}
