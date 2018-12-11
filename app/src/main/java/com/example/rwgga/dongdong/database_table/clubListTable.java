package com.example.rwgga.dongdong.database_table;

import java.io.Serializable;

public class clubListTable implements Serializable {
    private int boardKind;
    private String clubCaptain;
    private String clubName;
    private String clubContent;
    private String clubLocation;
    private String clubPhoneNumber;
    private int clubUserCnt;
    private int clubKind;

    public int getClubUserCnt() {
        return clubUserCnt;
    }

    public void setClubUserCnt(int clubUserCnt) {
        this.clubUserCnt = clubUserCnt;
    }

    public int getBoardKind() {
        return boardKind;
    }

    public void setBoardKind(int boardKind) {
        this.boardKind = boardKind;
    }

    public String getClubCaptain() {
        return clubCaptain;
    }

    public void setClubCaptain(String clubCaptain) {
        this.clubCaptain = clubCaptain;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubContent() {
        return clubContent;
    }

    public String getClubLocation() { return clubLocation; }

    public void setClubLocation(String clubLocation) { this.clubLocation = clubLocation; }

    public String getClubPhoneNumber() { return clubPhoneNumber; }

    public void setClubPhoneNumber(String clubPhoneNumber) { this.clubPhoneNumber = clubPhoneNumber; }

    public void setClubContent(String content) {
        clubContent = content;
    }

    public int getClubKind() {
        return clubKind;
    }

    public void setClubKind(int clubKind) {
        this.clubKind = clubKind;
    }
}
