package com.thekingelessar.assault.database;

public enum Statistic
{
    PLAYER_UUID("TEXT"),
    GAMES_FINISHED("INTEGER"),
    KILLS("INTEGER"),
    DEATHS("INTEGER"),
    STARS("INTEGER"),
    FASTEST_TIME("REAL"),
    MOST_KILLS_IN_SINGLE_GAME("INTEGER"),
    MOST_DEATHS_IN_SINGLE_GAME("INTEGER"),
    MOST_STARS_IN_SINGLE_GAME("INTEGER"),
    LAST_PLAYED("INTEGER");
    
    private String type;
    
    Statistic(String type)
    {
        this.type = type;
    }
    
    public String getType()
    {
        return this.type;
    }
}