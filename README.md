## Todo

- ~~Respawn command~~
- ~~Team/all chats~~
- ~~Stop defenders from spawn-camping the attackers~~
- ~~Fix arrow team-killing~~
- ~~Move magic location values to map config file~~
- ~~Make it more user-friendly when you're moving around wood swords and better swords~~
- End the game if the second time goes past first time? (Forfeit option?)
- Clean up random barriers around the map
- Attacker momentum would really stagnate for a while when they couldn't find an easy way into the base, maybe this can
  be fixed by adding/changing team buffs and by increasing the price of blocks.
- Better /assault commands

## Bugs

- ~~Gravel can't be broken when it falls into its location.~~
- ~~Building phase coin duping~~
- ~~Shop NPCs have capes~~
- Sometimes players aren't assigned to a team (maybe if there's an odd number of players?)
- Name colors aren't updating when new games start.
- Team buff effects are sometimes persisting in-between rounds(/games?)
- Cleaner time when it goes to 8 minutes
- Building mode keep inventory
- Shops not looking in the right direction (attacker normal shop)

## Roadmap

- Shops
    - Armor
    - Double jump
    - Strength?
- Multiple maps
- Game modifiers (admin set or voting)

### Completed

- Teams
- Me and my cohort built Saloon 😎
- Restructure current world setup (worlds vs. map)
- World copying/loading/deleting from maps
- Game cycle to about #6
- Basic game restrictions
    - Attacking players
    - Placing blocks
    - Destroying blocks
- Block breaking/placing restrictions (based on block type, nothing too complicated)
- Spawning, death, respawning
- Resource generation/gathering
    - Coins automatically tick up
    - GP on kill
- Shops
    - UBOM (physical items)
        - Weapons
        - Blocks
        - Utility items
        - Special map-specific items
        - Some items take coins, more powerful ones take emeralds
- Ending build stage
- Tools (stay with you on death, only buy once)
- Spawn items
- Swapping attacking teams
- Team buff shop
- Indication if team has the buff
- Kill sound effects
- Yaw/pitch values in spawns
- Forfeit when last player leaves
- Add player to instance while balancing it
- Book in hotbar takes you to website
- Emerald spawning
- Transportation out of base for defenders
- Stop block placing in spawn area
- Shop NPCs

---

## Game Cycle

1. Everybody starts in a lobby world
2. Each player that wants to join the game signifies it by running a command or something, then they're added to a list
3. Once the game has enough players, the game instance is created, the map/players passed to it, and the game world
   created
4. All the players are moved to the game world (on the waiting platform)
5. The countdown starts
6. Teams are split, and sent to their defending base in build mode.
7. Once building time is up, the attackers start.
8. Once time is up, or the objective is reached, the teams switch sides.
9. Once time is up, or the objective is reached, the winner is decided, and everything goes bog wild.

---

#### Planning

Misc., inconsequential questions to answer in the future:

- Should health be visible to enemies?
- Are gamer points shared throughout entire team or kept by individual players?

---

#### Resources

[FastBoard](https://github.com/MrMicky-FR/FastBoard)