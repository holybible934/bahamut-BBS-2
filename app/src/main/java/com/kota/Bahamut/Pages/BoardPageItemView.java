package com.kota.Bahamut.Pages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kota.Bahamut.Pages.Model.BoardPageItem;
import com.kota.Bahamut.R;

import java.util.Objects;

public class BoardPageItemView extends LinearLayout {
    private static final int _count = 0;
    private TextView _author_label = null;
    private ViewGroup _content_view = null;
    private TextView _date_label = null;
    private View _divider_bottom = null;
    private TextView _gy_label = null;
    private TextView _gy_title_label = null;
    private TextView _mark_label = null;
    private TextView _number_label = null;
    private TextView _status_label = null;
    private TextView _title_label = null;

    public BoardPageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoardPageItemView(Context context) {
        super(context);
        init();
    }

    /* access modifiers changed from: protected */
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void setItem(BoardPageItem aItem) {
        if (aItem != null) {
            setTitle(aItem.Title);
            setNumber(aItem.Number);
            setDate(aItem.Date);
            setAuthor(aItem.Author);
            setMark(aItem.isMarked);
            setGYNumber(aItem.GY);
            setReply(aItem.isReply);
            setRead(aItem.isDeleted || aItem.isRead);
            return;
        }
        clear();
    }

    private void init() {
        ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.board_page_item_view, this);
        this._status_label = findViewById(R.id.BoardPage_ItemView_Status);
        this._title_label = findViewById(R.id.BoardPage_ItemView_Title);
        this._number_label = findViewById(R.id.BoardPage_ItemView_Number);
        this._date_label = findViewById(R.id.BoardPage_ItemView_Date);
        this._gy_title_label = findViewById(R.id.BoardPage_ItemView_GY_Title);
        this._gy_label = findViewById(R.id.BoardPage_ItemView_GY);
        this._mark_label = findViewById(R.id.BoardPage_ItemView_mark);
        this._author_label = findViewById(R.id.BoardPage_ItemView_Author);
        this._content_view = findViewById(R.id.BoardPage_ItemView_contentView);
        this._divider_bottom = findViewById(R.id.BoardPage_ItemView_DividerBottom);
    }

    public void setDividerBottomVisible(boolean visible) {
        if (this._divider_bottom == null) {
            return;
        }
        if (visible) {
            if (this._divider_bottom.getVisibility() != View.VISIBLE) {
                this._divider_bottom.setVisibility(View.VISIBLE);
            }
        } else if (this._divider_bottom.getVisibility() != View.GONE) {
            this._divider_bottom.setVisibility(View.GONE);
        }
    }

    public void setTitle(String title) {
        if (this._title_label != null) {
            this._title_label.setText(Objects.requireNonNullElse(title, "讀取中..."));
        }
    }

    public void setAuthor(String author) {
        if (this._author_label != null) {
            this._author_label.setText(Objects.requireNonNullElse(author, "讀取中"));
        }
    }

    public void setDate(String date) {
        if (this._date_label != null) {
            this._date_label.setText(Objects.requireNonNullElse(date, "讀取中"));
        }
    }

    @SuppressLint({"DefaultLocale"})
    public void setNumber(int number) {
        if (this._number_label != null) {
            if (number > 0) {
                this._number_label.setText(String.format("%1$05d", number));
                return;
            }
            this._number_label.setText("讀取中");
        }
    }

    public void setGYNumber(int number) {
        if (this._gy_label == null) {
            return;
        }
        if (number == 0) {
            this._gy_title_label.setVisibility(View.GONE);
            this._gy_label.setVisibility(View.GONE);
            return;
        }
        this._gy_title_label.setVisibility(View.VISIBLE);
        this._gy_label.setVisibility(View.VISIBLE);
        this._gy_label.setText(String.valueOf(number));
    }

    @SuppressLint("SetTextI18n")
    public void setReply(boolean isReply) {
        if (this._status_label == null) {
            return;
        }
        if (isReply) {
            this._status_label.setText("Re");
        } else {
            this._status_label.setText("◆");
        }
    }

    public void setMark(boolean isMarked) {
        if (isMarked) {
            this._mark_label.setVisibility(View.VISIBLE);
        } else {
            this._mark_label.setVisibility(View.INVISIBLE);
        }
    }

    public void setRead(boolean isRead) {
        if (isRead) {
            this._title_label.setTextColor(-8355712);
        } else {
            this._title_label.setTextColor(-1);
        }
    }

    public void clear() {
        setTitle(null);
        setDate(null);
        setAuthor(null);
        setNumber(0);
        setGYNumber(0);
        setRead(true);
        setReply(false);
        setMark(false);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            if (this._content_view.getVisibility() != View.VISIBLE) {
                this._content_view.setVisibility(View.VISIBLE);
            }
        } else if (this._content_view.getVisibility() != View.GONE) {
            this._content_view.setVisibility(View.GONE);
        }
    }
}
