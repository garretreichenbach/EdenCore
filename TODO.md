### Navigation
- ~~increase max amount of saved Coordinates~~ (OBSOLETE)
- ~~draw gate icons on map~~
    - ~~sprite creation (GIMP)~~
    - ~~sprite loading~~
    - ~~subsprite loading~~
    - ~~sprite drawing~~
    - stitch multiple sprites/recolor partyl (multicolored sprites)
- ~~text on hover~~
- ~~sprite scaling when further away~~
- ~~sprite scaling on hover~~

#### Marker UI
- ~~leave out coords, use current sector~~
- allow color manipulation from command
- ~~allow overwriting of existing coords~~
- ~~list command~~

#### Public markers
- event based update system
    - ~~add through command~~
    - ~~remove through command~~
    - ~~player spawns (server + client)~~
    - ~~player saves coord~~ (good enough, could use improvement)
    
#### GATES
- always draw public warpgate routes
    - ~~childclass exclusivley for gates that includes connections~~ <<<<<<<<<<<<<
    - creation of gatemarker with line(s)
        - ~~selected entity + automated lines~~
            - ~~get relevant info from gameserver (all connections that are type "warpgate" from this sector)~~
            - automated update for gate
        - automated marker creation for admin-faction stations (?) (delayed to a later point)
            - how to notice station is wanted gatestation ? (special gate faction?)
            
#### PERSISTENCE
- ~~allow saving of childs inerhiting from MapMarker~~
- ~~get rid of sectorCode in savefile (unnecessary)~~

#### Personal saved coords
- ~~icon~~
- ~~update~~
- ~~drawing~~