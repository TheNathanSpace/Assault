## Roadmap
 
 - Game cycle
 - Spawning on game start and death
 - Resource generation/gathering
    - Coins automatically tick up
    - Emeralds spawn in physical locations near each base
    - GP on kill
 - Shops
    - UBOM (physical items)
        - Weapons
        - Blocks
        - Utility items
        - Special map-specific items
    - Gamer Shop (team buffs)
        - Potion effects
        - Movement buffs
 - Lobby/game instance stuff
    - Moving players in/out of game
    - Map voting
    - Joining/leaving game while it's running
 - Game modifiers (admin set or voting)

### Completed

 - Teams
 - Me and my cohort built Saloon 😎
 - Restructure current world setup (worlds vs. map)
 - World copying/loading/deleting from maps
 - Initial spawning (About at game cycle #6)
 - Basic game restrictions
     - Attacking players
     - Placing blocks
     - Destroying blocks
 - Block breaking/placing restrictions (based on block type, nothing too complicated)
 
---

## Game Cycle

1. Everybody starts in a lobby world
2. Each player that wants to join the game signifies it by running a command or something, then they're added to a list
3. Once the game has enough players, the game instance is created, the map/players passed to it, and the game world created
4. All the players are moved to the game world (on the waiting platform)
5. The countdown starts
6. Teams are split, and sent to their defending base in build mode
7. Once building time is up, the attackers start

---

#### Planning

Misc., inconsequential questions to answer in the future:

 - Should health be visible to enemies?
 
---

#### Resources

 - [Scoreboard Management](https://bukkit.org/threads/tutorial-scoreboards-teams-with-the-bukkit-api.139655/)