package com.kota.Bahamut.Pages;

import static com.kota.Bahamut.Service.CommonFunctions.getContextString;
import static com.kota.Bahamut.Service.MyBillingClient.checkPurchaseHistoryQuery;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.kota.ASFramework.Dialog.ASAlertDialog;
import com.kota.ASFramework.Dialog.ASDialog;
import com.kota.ASFramework.Dialog.ASProcessingDialog;
import com.kota.ASFramework.Thread.ASRunner;
import com.kota.Bahamut.BahamutPage;
import com.kota.Bahamut.DataModels.UrlDatabase;
import com.kota.Bahamut.R;
import com.kota.Bahamut.Service.CloudBackup;
import com.kota.Bahamut.Service.NotificationSettings;
import com.kota.Bahamut.Service.TempSettings;
import com.kota.Bahamut.Service.UserSettings;
import com.kota.Telnet.Model.TelnetFrame;
import com.kota.Telnet.TelnetClient;
import com.kota.Telnet.TelnetCursor;
import com.kota.TelnetUI.TelnetPage;
import com.kota.TelnetUI.TelnetView;

import java.util.concurrent.atomic.AtomicReference;

public class LoginPage extends TelnetPage {
    boolean _cache_telnet_view = false;
    int _error_count = 0;
    View.OnClickListener _login_listener = v -> {
        String err_message;
        LoginPage.this._username = ((EditText) LoginPage.this.findViewById(R.id.Login_UsernameEdit)).getText().toString().trim();
        LoginPage.this._password = ((EditText) LoginPage.this.findViewById(R.id.Login_passwordEdit)).getText().toString().trim();
        LoginPage.this._save_logon_user = ((CheckBox) LoginPage.this.findViewById(R.id.Login_loginRememberCheckBox)).isChecked();
        if (LoginPage.this._username.length() == 0 && LoginPage.this._password.length() == 0) {
            err_message = "帳號、密碼不可為空，請重新輸入。";
        } else if (LoginPage.this._username.length() == 0) {
            err_message = "帳號不可為空，請重新輸入。";
        } else if (LoginPage.this._password.length() == 0) {
            err_message = "密碼不可為空，請重新輸入。";
        } else {
            err_message = null;
        }
        if (err_message != null) {
            ASAlertDialog.showErrorDialog(err_message, LoginPage.this);
        } else {
            LoginPage.this.login();
        }
    };
    String _password = "";
    ASAlertDialog _remove_logon_user_dialog = null;
    boolean _save_logon_user = false;
    ASDialog _save_unfinished_article_dialog = null;
    TelnetView _telnet_view = null;
    String _username = "";

    public int getPageType() {
        return BahamutPage.BAHAMUT_LOGIN;
    }

    public int getPageLayout() {
        return R.layout.login_page;
    }

    public void onPageDidLoad() {
        // 登入
        getNavigationController().setNavigationTitle("勇者登入");
        findViewById(R.id.Login_loginButton).setOnClickListener(_login_listener);
        // checkbox區塊點擊
        CheckBox checkBox = (CheckBox) findViewById(R.id.Login_loginRememberCheckBox);
        findViewById(R.id.blockRememberLabel).setOnClickListener(view -> checkBox.setChecked(!checkBox.isChecked()));
        _telnet_view = (TelnetView) findViewById(R.id.Login_TelnetView);
        // 讀取預設勇者設定
        loadLogonUser();
        System.out.println("current  version:" + Build.VERSION.SDK_INT);

        // 清空暫存和執行中變數
        TempSettings.clearTempSettings(); // 清除暫存資料
        try (UrlDatabase urlDatabase = new UrlDatabase(getContext())) { // 清除URL資料庫
            urlDatabase.clearDb();
        } catch (Exception e) {
            Log.e("Bookmark", "initial fail");
        }

        // check VIP
        if (!UserSettings.getPropertiesVIP()) {
            checkPurchaseHistoryQuery();
        }
    }

    public synchronized boolean onPagePreload() {
        return handleNormalState();
    }

    // 按下返回
    protected boolean onBackPressed() {
        TelnetClient.getClient().close();
        return true;
    }

    boolean handleNormalState() {
        String row_23 = TelnetClient.getModel().getRowString(23);
        TelnetCursor cursor = TelnetClient.getModel().getCursor();
        if (row_23.endsWith("再見 ...")) {
            onLoginAccountOverLimit();
            return false;
        } else if (row_23.startsWith("您想刪除其他重複的 login")) {
            onCheckRemoveLogonUser();
            return false;
        } else if (row_23.startsWith("★ 密碼輸入錯誤") && cursor.row == 23) {
            _error_count++;
            onPasswordError();
            TelnetClient.getClient().sendStringToServer("");
            return false;
        } else if (row_23.startsWith("★ 錯誤的使用者代號") && cursor.row == 23) {
            _error_count++;
            onUsernameError();
            TelnetClient.getClient().sendStringToServer("");
            return false;
        } else if (cursor.equals(23, 16)) {
            // 開啟"自動登入中"
            if (UserSettings.getPropertiesAutoToChat()) {
                TempSettings.isUnderAutoToChat = true;
            }
            sendPassword();
            return false;
        } else {
            return true;
        }
    }

    void loadLogonUser() {
        EditText login_username_field = (EditText) findViewById(R.id.Login_UsernameEdit);
        EditText login_password_field = (EditText) findViewById(R.id.Login_passwordEdit);
        CheckBox login_remember = (CheckBox) findViewById(R.id.Login_loginRememberCheckBox);
        String username = UserSettings.getPropertiesUsername();
        String password = UserSettings.getPropertiesPassword();
        String username2 = username.trim();
        String password2 = password.trim();
        login_username_field.setText(username2);
        login_password_field.setText(password2);
        login_remember.setChecked(UserSettings.getPropertiesSaveLogonUser());
    }

    void saveLogonUserToProperties() {
        CheckBox login_remember = (CheckBox) findViewById(R.id.Login_loginRememberCheckBox);
        if (login_remember.isChecked()) {
            String username = ((EditText) findViewById(R.id.Login_UsernameEdit)).getText().toString().trim();
            String password = ((EditText) findViewById(R.id.Login_passwordEdit)).getText().toString().trim();
            UserSettings.setPropertiesUsername(username);
            UserSettings.setPropertiesPassword(password);
        } else {
            UserSettings.setPropertiesUsername("");
            UserSettings.setPropertiesPassword("");
        }
        UserSettings.setPropertiesSaveLogonUser(login_remember.isChecked());
        UserSettings.notifyDataUpdated();
    }

    public void onPageDidDisappear() {
        _telnet_view = null;
        _remove_logon_user_dialog = null;
        _save_unfinished_article_dialog = null;
        clear();
    }

    public void onPageRefresh() {
        if (_telnet_view != null) {
            setFrameToTelnetView();
        }
    }

    void setFrameToTelnetView() {
        TelnetFrame frame = TelnetClient.getModel().getFrame().clone();
        frame.removeRow(23);
        frame.removeRow(22);
        _telnet_view.setFrame(frame);
    }

    public void onPageWillDisappear() {
        ASProcessingDialog.dismissProcessingDialog();
        if (TempSettings.isUnderAutoToChat) {
            ASProcessingDialog.showProcessingDialog(getContextString(R.string.is_under_auto_logging_chat));
        }
    }

    public void clear() {
        _error_count = 0;
        _cache_telnet_view = false;
    }

    void login() {
        ASProcessingDialog.showProcessingDialog("登入中");
        ASRunner.runInNewThread(() ->
            TelnetClient.getClient()
                    .sendStringToServerInBackground(LoginPage.this._username)
        );
    }

    public void onCheckRemoveLogonUser() {
        new ASRunner() {
            public void run() {
                ASProcessingDialog.dismissProcessingDialog();
                if (LoginPage.this._remove_logon_user_dialog == null) {
                    LoginPage.this._remove_logon_user_dialog = (ASAlertDialog) ASAlertDialog.createDialog().setTitle("提示").setMessage("您想刪除其他重複的登入嗎？").addButton("否").addButton("是").setListener((aDialog, index) -> {
                        if (index == 0) {
                            TelnetClient.getClient().sendStringToServerInBackground("n");
                        } else {
                            TelnetClient.getClient().sendStringToServerInBackground("y");
                        }
                        LoginPage.this._remove_logon_user_dialog = null;
                        ASProcessingDialog.showProcessingDialog("登入中");
                    }).setOnBackDelegate(aDialog -> {
                        TelnetClient.getClient().sendStringToServerInBackground("n");
                        LoginPage.this._remove_logon_user_dialog.dismiss();
                        LoginPage.this._remove_logon_user_dialog = null;
                        ASProcessingDialog.showProcessingDialog("登入中");
                        return true;
                    });
                }
                LoginPage.this._remove_logon_user_dialog.show();
            }
        }.runInMainThread();
    }

    public void onPasswordError() {
        if (_error_count < 3) {
            new ASRunner() {
                public void run() {
                    ASProcessingDialog.dismissProcessingDialog();
                    ASAlertDialog.createDialog().setTitle("勇者密碼錯誤").setMessage("勇者密碼錯誤，請重新輸入勇者密碼").addButton("確定").scheduleDismissOnPageDisappear(LoginPage.this).show();
                }
            }.runInMainThread();
        } else {
            onLoginErrorAndDisconnected();
        }
    }

    public void onUsernameError() {
        if (_error_count < 3) {
            new ASRunner() {
                public void run() {
                    ASProcessingDialog.dismissProcessingDialog();
                    ASAlertDialog.createDialog().setTitle("勇者代號錯誤").setMessage("勇者代號錯誤，請重新輸入勇者代號").addButton("確定").scheduleDismissOnPageDisappear(LoginPage.this).show();
                }
            }.runInMainThread();
        } else {
            onLoginErrorAndDisconnected();
        }
    }

    public void onLoginErrorAndDisconnected() {
        new ASRunner() {
            public void run() {
                ASProcessingDialog.dismissProcessingDialog();
                ASAlertDialog.createDialog().setTitle("斷線").setMessage("帳號密碼輸入錯誤次數過多，請重新連線。").addButton("確定").show();
            }
        }.runInMainThread();
    }

    public void sendPassword() {
        TelnetClient.getClient().sendStringToServer(_password);
    }

    public void onLoginSuccess() {
        // 存檔客戶資料
        TelnetClient.getClient().setUsername(_username);
        saveLogonUserToProperties();
        UserSettings.notifyDataUpdated();

        // 詢問雲端
//        askCloudSave();
    }

    public void onSaveArticle() {
        if (_save_unfinished_article_dialog == null) {
            _save_unfinished_article_dialog = ASAlertDialog.createDialog().setTitle("提示").setMessage("您有一篇文章尚未完成").addButton("放棄").addButton("寫入暫存檔").setListener((aDialog, index) -> {
                switch (index) {
                    case 0 -> TelnetClient.getClient().sendStringToServer("Q");
                    case 1 -> TelnetClient.getClient().sendStringToServer("S");
                }
                LoginPage.this._save_unfinished_article_dialog = null;
            }).scheduleDismissOnPageDisappear(this);
            _save_unfinished_article_dialog.show();
        }
    }

    public void onLoginAccountOverLimit() {
        new ASRunner() {
            public void run() {
                ASProcessingDialog.dismissProcessingDialog();
                ASAlertDialog.createDialog().setTitle("警告").setMessage("您的帳號重覆登入超過上限，請選擇刪除其他重複的登入或將其它帳號登出。").addButton("確定").show();
            }
        }.runInMainThread();
    }

    private void askCloudSave() {
        final int[] cloudSaveStatus = new int[]{NotificationSettings.getShowCloudSave()};
        switch (cloudSaveStatus[0]){
            case 0:
                AtomicReference<Boolean> isCloudExist = new AtomicReference<>(false);
                // 詢問是否啟用雲端備份
                ASAlertDialog.createDialog()
                        .setTitle(getContextString(R.string.cloud_save))
                        .setMessage(getContextString(R.string.cloud_save_question1))
                        .addButton(getContextString(R.string.cancel))
                        .addButton(getContextString(R.string.on))
                        .setListener((aDialog, index) -> {
                            // 決定同步, 檢查雲端存檔是否存在
                            if (index == 1) {
                                isCloudExist.set(CloudBackup.INSTANCE.checkCloud());
                            } else {// 取消同步, 以本地端為主
                                cloudSaveStatus[0] = 1;
                                NotificationSettings.setShowCloudSave(cloudSaveStatus[0]);
                            }
                        }).show();

                // 雲端存在, 選擇本地或雲端
                if (isCloudExist.get()) {
                    ASAlertDialog.createDialog()
                            .setTitle(getContextString(R.string.cloud_save))
                            .setMessage(getContextString(R.string.cloud_save_question2))
                            .addButton(getContextString(R.string.cloud_save_local))
                            .addButton(getContextString(R.string.cloud_save_cloud))
                            .setListener((aDialog, index) -> {
                                if (index==0) {
                                    // 選擇本地=>覆蓋雲端
                                    CloudBackup.INSTANCE.backup();
                                } else {
                                    // 選擇雲端=>覆蓋本地
                                    CloudBackup.INSTANCE.restore();
                                }
                            });

                }
                break;
            case 1, 2:
                break;
        }
    }
}
