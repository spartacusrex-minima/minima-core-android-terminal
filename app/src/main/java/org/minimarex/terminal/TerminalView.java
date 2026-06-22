package org.minimarex.terminal;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.minimarex.minimaapi.MinimaAPI;
import org.minimarex.minimaapi.MinimaAPIListener;

public class TerminalView extends BaseView  {

    public int MAX_TERMINAL_LENGTH = 20000;

    ScrollView mScroller;
    TextView mMainText;
    EditText mInput;

    MinimaAPI mMinimaAPI;

    public TerminalView(Activity zActivity){
        super(zActivity, R.layout.view_terminal);

        mScroller = getMainView().findViewById(R.id.terminal_scroller);

        mMainText   = mMainView.findViewById(R.id.terminal_maintext);
        mMainText.setTypeface(Typeface.MONOSPACE);
        mMainText.setTextIsSelectable(true);

        appendText("**********************************************");
        appendText("*  __  __  ____  _  _  ____  __  __    __    *");
        appendText("* (  \\/  )(_  _)( \\( )(_  _)(  \\/  )  /__\\   *");
        appendText("*  )    (  _)(_  )  (  _)(_  )    (  /(__)\\  *");
        appendText("* (_/\\/\\_)(____)(_)\\_)(____)(_/\\/\\_)(__)(__) *");
        appendText("*                                            *");
        appendText("**********************************************");
        appendText("You can run commands in this Terminal..");

        mInput      = mMainView.findViewById(R.id.terminal_input);
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String cmd = mInput.getText().toString().trim();
                mInput.post(new Runnable() {
                    @Override
                    public void run() {
                        mInput.setText("");
                    }
                });

                if(actionId == EditorInfo.IME_ACTION_DONE){
                    runCMD(cmd);
                    return true;
                }

                if(event != null) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (cmd.equals("")) {
                            appendText("\n");
                            return false;
                        }

                        //Run this command
                        runCMD(cmd);

                        return true;
                    }
                }
                return false;
            }
        });

        Button but  = mMainView.findViewById(R.id.terminal_send);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmd = mInput.getText().toString().trim();
                mInput.post(new Runnable() {
                    @Override
                    public void run() {
                        mInput.setText("");
                    }
                });

                if(cmd.equals("")){
                    return;
                }
                //Run this command
                runCMD(cmd);
            }
        });
    }

    public void setMinimaAPI(MinimaAPI zAPI){
        mMinimaAPI = zAPI;
    }

    public void runCMD(String zCommand){

        //First append the command
        appendText(zCommand);

        //Run and append the result..
        mMinimaAPI.Command(zCommand, new MinimaAPIListener() {
            @Override
            public void response(JSONObject jsonObject) {

                try {
                    String json = jsonObject.toString(2);
                    appendText(json);

                } catch (JSONException e) {
                    appendText("Error json format : "+jsonObject.toString());
                }
            }
        });
    }

    public void appendText(String zText) {
        mMainText.post(new Runnable() {
            @Override
            public void run() {
                String text = mMainText.getText().toString();
                int len     = text.length();
                if(len > MAX_TERMINAL_LENGTH){
                    String newtext = text.substring(len-MAX_TERMINAL_LENGTH,len)+"\n" + zText;
                    mMainText.setText(newtext);
                }else{
                    mMainText.append("\n" + zText);
                }

                mScroller.post(new Runnable() {
                    @Override
                    public void run() {
                        mScroller.scrollTo(0,1000000);
                    }
                });
            }
        });
    }
}
