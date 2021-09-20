package com.thekingelessar.assault.database;

public class AssaultTable
{
    //    public Column playerUUID = new Column("Player UUID", DataType.STRING, 0);
    //    public Column gamesFinished = new Column("Games Finished", DataType.INTEGER, 0);
    //    public Column kills = new Column("Kills", DataType.INTEGER, 0);
    //    public Column deaths = new Column("Deaths", DataType.INTEGER, 0);
    //    public Column stars = new Column("Stars", DataType.INTEGER, 0);
    //    public Column fastestTime = new Column("Fastest Time", DataType.FLOAT, 0);
    //    public Column mostKillsInSingleGame = new Column("Most Kills in Single Game", DataType.INTEGER, 0);
    //    public Column mostDeathsInSingleGame = new Column("Most Deaths in Single Game", DataType.INTEGER, 0);
    //    public Column mostStarsInSingleGame = new Column("Most Stars in Single Game", DataType.INTEGER, 0);
    //
    public DatabaseClient databaseClient;
    
    public AssaultTable(DatabaseClient databaseClient)
    {
        this.databaseClient = databaseClient;
    }
    //
    //    public void setGamesFinished(UUID uuid, int gamesFinished)
    //    {
    //        this.playerUUID.setValue(uuid);
    //        this.gamesFinished.setValue(gamesFinished);
    //    }
    //
    //    public void setKills(UUID uuid, int kills)
    //    {
    //        this.playerUUID.setValue(uuid);
    //        this.kills.setValue(kills);
    //    }
    //
    //    public void setDeaths(UUID uuid, int deaths)
    //    {
    //        this.playerUUID.setValue(uuid);
    //        this.deaths.setValue(deaths);
    //    }
    //
    //    public void setStars(UUID uuid, int stars)
    //    {
    //        this.playerUUID.setValue(uuid);
    //        this.stars.setValue(stars);
    //    }
    //
    //    public void setFastestTime(UUID uuid, float fastestTime)
    //    {
    //        this.playerUUID.setValue(uuid);
    //        this.fastestTime.setValue(fastestTime);
    //    }
    //    // todo: finish this and make method for getting all updated values
}
