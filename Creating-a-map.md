# Requirements

The map is currently required to have the attackers on the Northern side of the map, and defence on the Southern side.\

# Example Config

This can be found in src/main/resources/maps/map_saloon.yml

# How to configure a map

## world_name:
This is the name of the world which the map is in, unless you use a multiword plugin this is likely the name of the map you downloaded or 'world'

## waiting_spawn:
The coordinates which you spawn in when the game countdown begins, this **must** have decimals after the number even if there is no decimal places. These can not be separated by a comma. <br/><br/> 
Eg. waiting_spawn: 0.5 128. 45.5

## waiting_platform_bounding_box:
The bounding box of the highest and lowest coordinate of the waiting spawn. This must be in a 3d shape. <br/><br/> 
Eg. waiting_platform_bounding_box:   
    - 3. 129. 42.   
    - -3. 126. 48.   

## void_enabled:
Wether you would like players to die when they go below a certain Y level. Defaults too true. Boolean value.

## void_level:
Level that if the above is enabled you should die. Requires a decimal after the number. <br/><br/> 
Eg. void_level: 70.

## max_z:
The furthest Z you can go in the arena. Requires decimal after the number. <br/><br/> 
Eg. max_z: 88.

## minute_z:
The least Z you can go in the arena. Requires decimal after the number <br/><br/> 
Eg. min_z: 2.

## max_y:
The highest Y level you can go in the arena. Requires decimal after the number.<br/><br/> 
Eg. max_y: 122.

## attacker_base_prot_max_z:
How far the attacker spawn protection is, gives protection for every Z below this number. Requires decimal after the number. <br/><br/> 
Eg. attacker_base_prot_max_z: 20.

# Bases:

Each colour requires an entry here, by default red & blue. You repeat the below for each side following syntax in example document.

## defender_spawn:
Coordinates for the defender to spawn at.

## defender_shop: 
Where the NPC spawns in coordinates.

## defender_bounding_box: 
Spawn protection, use same as waiting_platform_boundary_box:.

## objective: 
Where the star spawns in coordinates.

## attacker_spawn:
Coordinates for the attacker to spawn.

## attacker_shop:
Coordinates for the attacker's NPC to spawn at.

## attacker_buff_shop:
Coordinates for the NPC where you spend gamer points.

## emerald_spawns:
Can be a list of coordinates where emeralds will spawn, but can be a singular value in list form.

# Block related things

## placeable_blocks
List of values from https://helpch.at/docs/1.8/index.html?org/bukkit/Material.html that can be placed

## breakable_blocks
List of values from above link which can be broken.
