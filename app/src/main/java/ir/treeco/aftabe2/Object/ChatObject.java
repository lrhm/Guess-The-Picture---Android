package ir.treeco.aftabe2.Object;

/**
 * Created by al on 12/28/15.
 */
public class ChatObject {

    public static final int TYPE_ME = 0;
    public static final int TYPE_OTHER = 1;

    private int mType;



    private String mChatContent;

    public ChatObject(int type, String chatContent) {
        this.mType = type;
        this.mChatContent = chatContent;
    }

    public int getType(){
        return mType;
    }

    public String getChatContent() {
        return mChatContent;
    }
}
