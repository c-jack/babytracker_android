package uk.cjack.babytracker.model;

import java.io.Serializable;

public class Baby implements Serializable {
    private long babyId;
    private String babyName;

    public Baby(long babyId, String babyName) {
        this.babyId = babyId;
        this.babyName = babyName;
    }


    public long getBabyId() {
        return babyId;
    }

    public void setBabyId(int babyId) {
        this.babyId = babyId;
    }

    public String getBabyName() {
        return babyName;
    }

    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }
}
