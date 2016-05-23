package ir.treeco.aftabe2.View.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import ir.treeco.aftabe2.Adapter.ChatAdapter;
import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.Object.ChatObject;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.SizeConverter;
import ir.treeco.aftabe2.Util.SizeManager;

public class ChatFragment extends Fragment implements View.OnClickListener {


    EditText mEditText;
    View mMainLayout;
    ChatAdapter mChatAdapter;
    RecyclerView mChatView;
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ChatFragment() {
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ImageManager imageManager = ((MainApplication) getContext().getApplicationContext()).getImageManager();


        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        mChatView = (RecyclerView) view.findViewById(R.id.chat_recycler_view);
        TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.chat_text_input_layout);
        mEditText = (EditText) view.findViewById(R.id.chat_input_edit_text);
        mMainLayout = view.findViewById(R.id.chat_main_layout);
        ImageView sendButton = (ImageView) view.findViewById(R.id.chat_send);
        sendButton.setOnClickListener(this);
        int size = (int) (SizeManager.getScreenWidth() * 0.1);
        sendButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.chatbutton, size, size));


        LinearLayout.LayoutParams textlp = new LinearLayout.LayoutParams((int) (SizeManager.getScreenWidth() * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);

        textInputLayout.setLayoutParams(textlp);


        mChatView.setLayoutManager(new LinearLayoutManager(getActivity() ,LinearLayoutManager.VERTICAL , true));

        ArrayList<ChatObject> list = new ArrayList<>();

        ChatObject o1 = new ChatObject(ChatObject.TYPE_ME, "saalam");
        ChatObject o2 = new ChatObject(ChatObject.TYPE_OTHER, "salam");


        mChatAdapter = new ChatAdapter(list);
        mChatView.setAdapter(mChatAdapter);
        mChatAdapter.addChatItem(o1);
        mChatAdapter.addChatItem(o2);


        ImageView shaderImageView = (ImageView) view.findViewById(R.id.shade_image_chat_fragment);
        SizeConverter shadeConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth(), 1857, 23);
        shaderImageView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.shade, shadeConverter.mWidth, shadeConverter.mHeight, ImageManager.ScalingLogic.FIT));



        return view;
    }


    public void sendMessage(String msg) {
        ChatObject chatObject = new ChatObject(ChatObject.TYPE_ME, msg);

        mChatAdapter.addChatItem(chatObject);
        mChatView.scrollToPosition(0);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.chat_send:
                if(mEditText.getText().length() > 0) {
                    sendMessage(mEditText.getText().toString());
                    clear();
                }

        }
    }


    public void clear() {
        mEditText.setText("");
//        hideKeyboard();
//        mMainLayout.requestFocus();

    }
    public  void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity() .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mMainLayout.getWindowToken(), 0);
    }
}
