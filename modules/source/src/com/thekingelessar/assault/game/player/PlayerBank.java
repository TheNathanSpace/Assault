package com.thekingelessar.assault.game.player;

import com.thekingelessar.assault.Assault;
import com.thekingelessar.assault.util.version.XSound;

public class PlayerBank
{
    public int coins;
    public GamePlayer gamePlayer;
    
    public boolean maxCoinAlerted = false;
    
    public PlayerBank(int coins, GamePlayer gamePlayer)
    {
        this.coins = coins;
        this.gamePlayer = gamePlayer;
    }
    
    public void addCoins(int coinsToAdd)
    {
        boolean alert = false;
        if (this.gamePlayer.gameInstance.gameMap.coinCap != 0)
        {
            if (this.coins + coinsToAdd > this.gamePlayer.gameInstance.gameMap.coinCap)
            {
                this.coins = this.gamePlayer.gameInstance.gameMap.coinCap;
                alert = true;
            }
            else
            {
                this.coins += coinsToAdd;
            }
        }
        else
        {
            this.coins += coinsToAdd;
        }
        
        if (alert && !this.maxCoinAlerted)
        {
            this.gamePlayer.player.sendMessage(Assault.ASSAULT_PREFIX + "You've can't hold onto anymore coins! Go spend some!");
            this.gamePlayer.player.playSound(this.gamePlayer.player.getLocation(), XSound.ITEM_FLINTANDSTEEL_USE.parseSound(), 1.0f, 1.0f);
            this.maxCoinAlerted = true;
        }
    }
}
