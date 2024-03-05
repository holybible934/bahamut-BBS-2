package com.kota.Bahamut.Service;

/*
* 此處存放app執行階段使用的變數, 存放於記憶體, 關閉就消失
* */

public class TempSettings {
    static Boolean isUnderAutoToChat = false; // 正在自動登入ing
    static Boolean isFloatingInvisible = false; // 浮動工具列是否正處於隱藏狀態
    static String boardFollowTitle = ""; // 正在看的討論串標題

    public static void clearTempSettings() {
        setIsUnderAutoToChat(false);
        setIsFloatingInvisible(false);
        setBoardFollowTitle("");
    }
    public static void setIsUnderAutoToChat(boolean isEnable) {
        isUnderAutoToChat = isEnable;
    }
    public static boolean isUnderAutoToChat() {
        return isUnderAutoToChat;
    }

    public static void setIsFloatingInvisible(boolean isEnable) {
        isFloatingInvisible = isEnable;
    }
    public static boolean isFloatingInvisible() {
        return isFloatingInvisible;
    }

    public static void setBoardFollowTitle(String _title) { boardFollowTitle = _title; };
    public static boolean isBoardFollowTitle(String _readTitle) { return boardFollowTitle.equals(_readTitle); };
}
