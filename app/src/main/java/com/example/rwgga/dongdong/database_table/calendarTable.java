package com.example.rwgga.dongdong.database_table;

import java.io.Serializable;

public class calendarTable  implements Serializable {
    private int calID;
    private int boardKind;
    private String calDate;
    private String calContent;

    public int getCalID() {
        return calID;
    }

    public void setCalID(int calID) {
        this.calID = calID;
    }

    public int getBoardKind() {
        return boardKind;
    }

    public void setBoardKind(int boardKind) {
        this.boardKind = boardKind;
    }

    public String getCalDate() {
        return calDate;
    }

    public void setCalDate(String calDate) {
        this.calDate = calDate;
    }

    public String getCalContent() {
        return calContent;
    }

    public void setCalContent(String calContent) {
        this.calContent = calContent;
    }
}
