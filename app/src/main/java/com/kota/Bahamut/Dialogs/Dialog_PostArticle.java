package com.kota.Bahamut.Dialogs;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import com.kota.ASFramework.Dialog.ASDialog;
import com.kota.bahamut_bbs_2.R;;

public class Dialog_PostArticle extends ASDialog implements View.OnClickListener {
    public static final int NEW = 0;
    public static final int REPLY = 1;
    Button _cancel_button = null;
    Dialog_PostArticle_Listener _listener = null;
    RadioGroup _post_target_group;
    Button _send_button = null;
    Spinner _sign_spinner = null;
    int _target = 0;

    public String getName() {
        return "BahamutBoardsPostArticle";
    }

    public Dialog_PostArticle(int aTarget) {
        requestWindowFeature(1);
        setContentView(R.layout.dialog_post_article);
        getWindow().setBackgroundDrawable((Drawable) null);
        this._target = aTarget;
        this._post_target_group = (RadioGroup) findViewById(R.id.post_target);
        this._send_button = (Button) findViewById(R.id.send);
        this._cancel_button = (Button) findViewById(R.id.cancel);
        View reply_target_view = findViewById(R.id.reply_target_view);
        View findViewById = findViewById(R.id.sign_view);
        if (this._target == 0) {
            reply_target_view.setVisibility(8);
        } else {
            reply_target_view.setVisibility(0);
        }
        this._sign_spinner = (Spinner) findViewById(R.id.sign_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.reply_target_list, 17367048);
        adapter.setDropDownViewResource(17367049);
        this._sign_spinner.setAdapter(adapter);
        this._send_button.setOnClickListener(this);
        this._cancel_button.setOnClickListener(this);
    }

    public void onClick(View view) {
        String target;
        if (view == this._send_button && this._listener != null) {
            int checked_id = this._post_target_group.getCheckedRadioButtonId();
            int selected_sign = this._sign_spinner.getSelectedItemPosition();
            String sign = "";
            if (selected_sign > 0) {
                sign = "" + (selected_sign - 1);
            }
            if (checked_id == R.id.post_to_mail) {
                target = "M";
            } else if (checked_id == R.id.post_to_both) {
                target = "B";
            } else {
                target = "F";
            }
            this._listener.onPostArticleDoneWithTarger(target, sign);
        }
        dismiss();
    }

    public void setListener(Dialog_PostArticle_Listener listener) {
        this._listener = listener;
    }
}
