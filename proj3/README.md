# Build Your Own World Design Document

**Partner 1: Luvsandash (Steve) Davaasuren**

**Partner 2: John Gardner**

## Classes and Data Structures

### `Main`
#### Description
The `Main` class serves as the entry point for the world generation and interaction program.

### `World`
#### Description
The `World` class is central to the game's functionality. It handles the creation and rendering of the game world, including rooms and hallways. The class manages user interactions, game state updates, visibility mechanics, and enemy movements.
#### Instance Variables
- `maxRoomWid`: Maximum width of the rooms in the game world.
- `maxRoomHeight`: Maximum height of the rooms.
- `WIDTH`: The width of the world (game grid size).
- `HEIGHT`: The height of the world (game grid size).
- `world`: 2D array of `TETile`, representing the tiles in the game world.
- `visibilityMap`: Boolean array indicating visible areas for the player.
- `isLineOfSight`: Flag to toggle the line of sight feature.
- `pathFinder`: Instance of `AStarPathFinder` for calculating enemy paths.
- `showEnemyPath`: Flag to show/hide the enemy's planned path.
- `enemyPath`: List of points representing the enemy's path.
- `lastEnemyMoveTime`: Timestamp for the enemy's last move.
- `avatar`: Player's avatar in the game.
- `enemy`: Enemy character in the game.
- `movementHistory`: String recording the player's movements.
- `seed`: Seed used for random generation.
- `rooms`: List of `Room` objects representing the rooms in the world.
- `random`: Random number generator for various game aspects.
- `flowerPositions`: List of points representing flower positions.
- `flowers`: Number of flowers in the game world.
- `started`: Boolean indicating if the game has started.

### `AStarPathFinder`
#### Description
The `AStarPathFinder` class implements the A* pathfinding algorithm to find the shortest path between two points on a grid. It uses a heuristic to guide the search for efficiency.
#### Inner Class
- `Cell`: A helper class to store pathfinding details like cost metrics (f, g, h) and parent cell indices for each grid cell.
### `WeightedQuickUnion`
#### Description
`WeightedQuickUnion` implements the Union-Find data structure with path compression and weighting. It is used to keep track of connectivity of the rooms in the world.
#### Instance Variables
- `parent` : An integer array where each element points to the parent of the current element. For a root element, it points to itself.
- `size` : An integer array that keeps track of the size of each tree, ensuring that the smaller tree is merged into the larger tree during union operations.
### `Edge`
#### Description
The Edge class represents a connection between two rooms in the world, commonly used to form hallways or paths. Each edge has an associated weight, which typically represents the distance from one room to the other.
#### Instance Variables
- `room1` : A Room variable to store the first room
- `room2` : A Room variable to store the second room
- `weight` : Euclidian Distance between `room1` and `room2`
### `Point`
#### Description
The Point object is used to represent a single point in the 2D world. All Point objects have x and y values indicating its position in the 2D world.
#### Instance Variables
- `x` : An integer to store the x-axis location of the point
- `y` : An integer to store the y-axis location of the point
### `Room`
#### Description
The Room object is used to represent a single room in the 2D world. All Room objects have height, width, and points representing the bottom left and top right points
#### Instance Variables
- `bottomLeft` : A Point variable to store the bottom left corner of the room object
- `topRight` : A Point variable to store the top right corner of the room object
- `height` : An integer to store the height of the room
-  `width` : An integer to store the width of the room
### `Avatar`
#### Description
The Avatar class represents a character within the world, having a position, an image, and life points.
#### Instance Variables
- `startPos` : The starting point of the avatar when it is initialized. Used for returning main character to its starting position when it is caught by the enemy.
- `pos` : The current position of the avatar
- `img` : TETile object to store the tile representing the avatar
- `life` : An integer to store the remaining lives of the avatar.


## Algorithms

#### Key Methods
- `void buildWorld()`: Sets up the game world, generating rooms and hallways.
- `void begin()`: Initializes and runs the game loop based on the input seed and game state.
- `void generateRooms(int numRooms)` : Generates a random number of rooms based on the specified maximum and places them in the world.
- `void connectRooms()`: Connects all rooms with hallways to ensure navigability. Used Minimum-Spanning-Tree via distance between middle points of the rooms.
- `void interactWithKeyboard()`: Handles user input for game controls.
- `void updateWorld(char m)`: Updates the world based on player's movement command.
- `void changeLineOfSight()`: Toggles the line of sight feature for the avatar, allowing or preventing the player from seeing through walls.
- `void changeShowEnemyPath()`: Toggles the visibility of the path the enemy will take towards the player's avatar.
- `boolean isWithinBounds(int x, int y)` : Checks if a given x,y coordinate is within the bounds of the world grid.
- `void resetVisibility()` : Resets the visibility map around the avatar to false within a certain radius.
- `void calculateVisibility()`: Updates which parts of the world are visible to the player.
- `void drawEnemyPos()`: Positions the enemy character in the world.
- `void handleGivenAvatarLife(String lives)` : Sets the avatar's life count based on the given string.
- `void drawFlowers()` : Draws flowers at their designated positions on the world grid.
- `boolean checkIfCatchFlower()` : Checks if the avatar has caught a flower and updates the flower count and positions.
- `boolean checkIfWin() / checkIfLost()`: Determine the win or lose state of the game.
- `boolean checkIfEnemyCatch` : Checks if the enemy has caught the avatar and updates avatar's life accordingly.
- `void drawWinningFrame() / drawLosingFrame()` : Displays the winning or losing frame
- `void placeAvatarAtBegin()` : Places the avatar back at the starting position.
- `void addPathToBegin()` : Adds the path back to the starting position to the movement history.
- `void drawHud()` : Draws the Heads-Up Display (HUD) showing the game's status like remaining life and flowers count.
- `void moveEnemy()`: Moves the enemy using the AStarPathFinder.
- `void writeHistoryToFile(Integer slot)` : Writes the current game state to a save file for the specified slot.
- `void drawStart()`: Draws the start menu of the game.
- `String getSeed()`: Gets the seed input from the player and constructs the seed string.
- `char getOption()` : Waits for the player to press a key to choose an option from the start menu.
- `TETile[][] getWorldFromInput(String input)` : Reads and processes the input string to set up the world.
- `void drawRoom(Room r)`: Renders a specific room in the world.
- `void drawWalls(Room r)` : Draws the walls of the given room in the world. The walls are placed at the outer edges of the room.
- `void fillFloor(Room r)` : Fills the floor area of the given room in the world. The floor is placed within the walls of the room.
- `void drawHallway(Room r1, Room r2)` : Draws a hallway between two rooms
- `void drawSegment(Point start, Point end, boolean isHorizontal)` : Draws a segment of a hallway between two points in the world.
- `void drawHallwayTile(int x, int y)` : Draws a single hallway tile and its surrounding walls at the specified coordinates.
- `Room randomRoomGenerator()` : Generates a random room within the world bounds.
- `boolean doesIntersect(Room newRoom)` : Given a room object, checks whether the given room intersects with the other rooms in the world
- `void initWorld()`: Initializes the game world to its default state.
- `Point randomPointOnWorld()` : Gets a random point on the world grid that could potentially be the bottom left of a room.

### `AStarPathFinder`

#### Key Methods
- `List<Point> tracePath(Cell[][] cellDetails, Point goal)` : Reconstructs the path from the start to the goal point using the details stored in cells.
- `List<Point> findPath(TETile[][] world, Point start, Point goal)` : Finds the shortest path from the start to the goal point using the A* algorithm.
- `boolean isDestination(int x, int y, Point dest)` : Checks if a point is the destination.
- `boolean isValid(int x, int y)` : Validates if a coordinate is within the boundaries of the grid.
- `int manhattanDistance(int x, int y, Point b)` : Calculates the Manhattan distance between two points, which is used as a heuristic for the A* algorithm.
  
### `WeightedQuickUnion`

#### Key Methods
- `void validate(int v)` : Validates if the given index is valid
- `int find(int v)` : Returns the root of the element
- `boolean connected(int p, int q)` : Returns true if the two elements are in the same set
- `void union(int p, int q)` : Connect two elements

### `Edge`

#### Key Methods
- `Room getRoom1()` : Retrieves the first room connected by this edge.
- `Room getRoom2()` : Retrieves the second room connected by this edge.
- `int getWeight()` : Retrieves the weight of this edge.
- `int compareTo(Edge other)` : Compares this edge with another edge based on their weights. This method is used for sorting purposes and follows the contract specified by the Comparable interface.
### `Point`

#### Key Methods
- `Getters / Setters` : Returns the positions / Sets the positions
- `boolean equals(Object obj)` : Compares this point to the specified object for equality.


### `Room`

#### Key Methods
- `Getters` : Getter functions for `bottomLeft` and `topRight`
- `Point getCenter()` : Return the middle point of the room
- `int distanceFromOtherRoom(Room other)` : Given a room object, returns a distance between this and the other room.
- `boolean intersects(Room other)` : Given a room object, checks whether the given room intersects with this room
- `Point getRandomPointInRoom(Random rand)` : Retrieves a random point within the given room that is not a wall.
### `Avatar`

#### Key Methods
- `Getters` : Getter functions for all variables
- `void setLife(int x)` : Sets the life points of the avatar to the specified value.
- `void reduceLife()` : Reduces the life points of the avatar by 1, down to a minimum of 0.
## Persistence
The game's state is saved in text files, allowing players to save and load their progress. Key methods involved in this process include:
- `writeHistoryToFile(Integer slot)`: Saves the current game state to a file.
- `getOption()`: Reads player input to select a saved game to load.
- `getWorldFromInput(String input)`: Reconstructs the game world from a saved state.
  
### In text files:
- First line - String representing "n#s" + movements
- Second line - String in format "x y", representing the x, y position of the enemy when game is saved.
- Third line - String representing the lives of the avatar when the game is saved.


