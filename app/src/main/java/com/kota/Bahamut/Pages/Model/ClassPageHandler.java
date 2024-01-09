package com.kota.Bahamut.Pages.Model;

import com.kota.Telnet.Model.TelnetRow;
import com.kota.Telnet.TelnetClient;
import com.kota.Telnet.TelnetUtils;

public class ClassPageHandler {
    private static ClassPageHandler _instance = null;

    private ClassPageHandler() {
    }

    public static ClassPageHandler getInstance() {
        if (_instance == null) {
            _instance = new ClassPageHandler();
        }
        return _instance;
    }

    public ClassPageBlock load() {
        ClassPageBlock class_package = ClassPageBlock.create();
        if (TelnetClient.getModel().getRowString(2).trim().startsWith("編號")) {
            class_package.mode = 0;
        } else {
            class_package.mode = 1;
        }
        int end_index = 3 + 20;
        int i = 3;
        while (i < end_index && TelnetClient.getModel().getRowString(i).length() != 0) {
            TelnetRow row = TelnetClient.getModel().getRow(i);
            int board_index = TelnetUtils.getIntegerFromData(row, 1, 5);
            if (i == 3) {
                class_package.minimumItemNumber = board_index;
            }
            String board_selected = row.getSpaceString(0, 0).trim();
            if (board_selected.length() > 0 && board_selected.charAt(0) == '>') {
                class_package.selectedItemNumber = board_index;
            }
            int board_name_end = 9;
            int j = 0;
            while (j < 12 && row.data[j + 9] != 32) {
                board_name_end = j + 9;
                j++;
            }
            String board_name = row.getSpaceString(9, board_name_end).trim();
            String board_title = row.getSpaceString(board_name_end + 1, 64).trim();
            String board_manager = row.getSpaceString(65, 79).trim();
            ClassPageItem item = ClassPageItem.create();
            if (board_name.endsWith("/")) {
                item.isDirectory = true;
                board_name = board_name.substring(0, board_name.length() - 1);
            } else {
                item.isDirectory = false;
                if (board_title.length() > 2) {
                    board_title = board_title.substring(2);
                }
            }
            item.Name = board_name;
            item.Title = board_title;
            item.Manager = board_manager;
            class_package.maximumItemNumber = board_index;
            item.Number = board_index;
            class_package.setItem(i - 3, item);
            i++;
        }
        return class_package;
    }
}
