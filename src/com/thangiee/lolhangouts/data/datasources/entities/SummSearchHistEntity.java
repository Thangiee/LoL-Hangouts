package com.thangiee.lolhangouts.data.datasources.entities;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

@Table(name = "SummSearchHistEntity")
public class SummSearchHistEntity extends Model {
    @Column(name = "inGameName", uniqueGroups = {"group1"}, onUniqueConflicts = Column.ConflictAction.REPLACE)
    public String inGameName;
    @Column(name = "regionId", uniqueGroups = {"group1"}, onUniqueConflicts = Column.ConflictAction.REPLACE)
    public String regionId;

    @Column public Date date;

    public SummSearchHistEntity() {
        super();
    }

    public SummSearchHistEntity(String inGameName, String regionId) {
        this.inGameName = inGameName;
        this.regionId = regionId;
        this.date = new Date();
    }
}
