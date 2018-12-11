package com.example.rwgga.dongdong.database_table;

public class waitTable {

    private int infoId;
    private int boardKind;
    private String waitContent;
    private String studentNumber;

    public int getInfoId() {
        return infoId;
    }

    public void setInfoId(int infoId) {
        this.infoId = infoId;
    }

    public int getBoardKind() {
        return boardKind;
    }

    public void setBoardKind(int boardKind) {
        this.boardKind = boardKind;
    }

    public String getWaitContent() {
        return waitContent;
    }

    public void setWaitContent(String waitContent) {
        this.waitContent = waitContent;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
}
