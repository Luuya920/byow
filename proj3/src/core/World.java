package core;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * The World class represents a randomly generated world consisting of rooms and connecting hallways.
 * This class is responsible for initializing the world, generating rooms, and connecting them with hallways.
 * Each room is placed at a random location within the world bounds, ensuring no overlap occurs.
 * Hallways are then generated to connect these rooms, creating a cohesive map layout.
 */
public class World {
    private final int maxRoomWid = 12;
    private final int maxRoomHeight = 12;
    private final int WIDTH = 100;
    private final int HEIGHT = 60;
    private final TETile[][] world;
    private final boolean[][] visibilityMap;
    private boolean isLineOfSight;
    private final AStarPathFinder pathFinder;
    private boolean showEnemyPath;
    private List<Point> enemyPath;
    private long lastEnemyMoveTime;
    private Avatar avatar;
    private Avatar enemy;
    private String movementHistory, seed;
    private final List<Room> rooms;
    private Random random;
    private final List<Point> flowerPositions;
    private int flowers;
    private boolean started;

    /**
     * The constructor for the World class.
     * It initializes the world grid with a specified WIDTH and HEIGHT,
     * sets up the visibility map, initializes the AStarPathFinder, and sets up other components.
     */
    public World() {
        world = new TETile[WIDTH][HEIGHT];
        visibilityMap = new boolean[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                visibilityMap[i][j] = false;
            }
        }
        pathFinder = new AStarPathFinder();
        isLineOfSight = false;
        showEnemyPath = false;
        enemyPath = new ArrayList<>();
        flowerPositions = new ArrayList<>();
        flowers = 3;
        avatar = null;
        enemy = null;
        lastEnemyMoveTime = System.currentTimeMillis();
        movementHistory = "";
        started = false;
        rooms = new ArrayList<>();
    }

    /**
     * Builds the world by first initializing random number generator with seed,
     * then it generates rooms and connects them with hallways.
     */
    public void buildWorld() {
        random = new Random(Long.parseLong(seed));
        initWorld();
        int maxNumberOfRooms = 20;
        int roomNumbers = RandomUtils.uniform(random, maxNumberOfRooms - 5) + 6;
        generateRooms(roomNumbers);
        connectRooms();
    }

    /**
     * Handles keyboard interactions for starting a new game, loading a game, or quitting.
     * It reads the user's input and proceeds with the corresponding option.
     */
    public void interactWithKeyboard() {
        StdDraw.setCanvasSize(WIDTH * 14, HEIGHT * 14);
        StdDraw.setFont(new Font("Apple Casual", Font.BOLD, 30));
        StdDraw.setXscale(0, WIDTH * 14);
        StdDraw.setYscale(0, HEIGHT * 14);
        StdDraw.enableDoubleBuffering();

        drawStart();
        char opt = getOption();
        In in;
        String line = "", enemyPos = "", lives = "";
        if (opt == 'n') {
            String s = getSeed();
            if(s.isEmpty()){
                System.exit(0);
            }
            begin(opt + s + "s", null, null);
        } else if (opt == 'l') {
            in = new In("./saves/save-slot-1.txt");

            if (!in.isEmpty()) {
                line = in.readLine();
            }
            if (!line.isEmpty()) {
                enemyPos = in.readLine();
            }
            if (!line.isEmpty()) {
                lives = in.readLine();
                begin(line, enemyPos, lives);
            } else {
                System.exit(0);
            }
        } else if (opt == '2') {
            in = new In("./saves/save-slot-2.txt");

            if (!in.isEmpty()) {
                line = in.readLine();
            }
            if (!line.isEmpty()) {
                enemyPos = in.readLine();
            }
            if (!line.isEmpty()) {
                lives = in.readLine();
                begin(line, enemyPos, lives);
            } else {
                System.exit(0);
            }
        } else if (opt == '3') {
            in = new In("./saves/save-slot-3.txt");

            if (!in.isEmpty()) {
                line = in.readLine();
            }
            if (!line.isEmpty()) {
                enemyPos = in.readLine();
            }
            if (!line.isEmpty()) {
                lives = in.readLine();
                begin(line, enemyPos, lives);
            } else {
                System.exit(0);
            }
        } else if (opt == 'q') {
            System.exit(0);
        }
    }

    /**
     * Toggles the line of sight feature for the avatar, allowing or preventing the player from seeing through walls.
     */
    private void changeLineOfSight() {
        this.isLineOfSight = !isLineOfSight;
    }

    /**
     * Toggles the visibility of the path the enemy will take towards the player's avatar.
     */
    private void changeShowEnemyPath() {
        this.showEnemyPath = !showEnemyPath;
    }

    /**
     * Checks if a given x,y coordinate is within the bounds of the world grid.
     *
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @return true if within bounds, false otherwise.
     */
    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    /**
     * Resets the visibility map around the avatar to false within a certain radius.
     */
    private void resetVisibility() {
        Point avatarPosition = avatar.getPos();
        for (int x = avatarPosition.getX() - 4; x <= avatarPosition.getX() + 4; x++) {
            for (int y = avatarPosition.getY() - 4; y <= avatarPosition.getY() + 4; y++) {
                if (isWithinBounds(x, y)) {
                    visibilityMap[x][y] = false;
                }
            }
        }
    }

    /**
     * Calculates which tiles are visible to the avatar within a certain radius and updates the visibility map.
     */
    public void calculateVisibility() {
        Point avatarPosition = avatar.getPos();

        for (int x = avatarPosition.getX() - 4; x <= avatarPosition.getX() + 4; x++) {
            for (int y = avatarPosition.getY() - 4; y <= avatarPosition.getY() + 4; y++) {
                if (isWithinBounds(x, y)) {
                    visibilityMap[x][y] = true;
                }
            }
        }
    }

    /**
     * Begins the game with given input for seed and other parameters.
     * It initializes the game state, places the avatar and enemy if loading, and handles the main game loop.
     *
     * @param input    The input string containing the seed and commands.
     * @param enemyPos The position of the enemy if loading.
     * @param lives    The number of lives the avatar has if loading.
     */
    public void begin(String input, String enemyPos, String lives) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT + 5);
        getWorldFromInput(input);
        drawEnemyPos(enemyPos);
        handleGivenAvatarLife(lives);

        StdDraw.enableDoubleBuffering();
        TETile[][] renderTiles = new TETile[WIDTH][HEIGHT];

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (!movementHistory.isEmpty() && movementHistory.charAt(movementHistory.length() - 1) == ':') {
                    if (key == 'q') {
                        movementHistory = movementHistory.substring(0, movementHistory.length() - 1);
                        writeHistoryToFile(1);
                        break;
                    } else if (key == '2') {
                        movementHistory = movementHistory.substring(0, movementHistory.length() - 1);
                        writeHistoryToFile(2);
                        break;
                    } else if (key == '3') {
                        movementHistory = movementHistory.substring(0, movementHistory.length() - 1);
                        writeHistoryToFile(3);
                        break;
                    }
                    movementHistory = movementHistory.substring(0, movementHistory.length() - 1);
                }
                updateWorld(key);
            }
            if (checkIfWin()) {
                drawWinningFrame();
                break;
            }
            drawFlowers();
            if (!started) {
                ter.renderFrame(world);
                drawHud();
                StdDraw.show();
                continue;
            }
            moveEnemy();
            checkIfEnemyCatch();
            if (checkIfLost()) {
                drawLosingFrame();
                break;
            }
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    renderTiles[x][y] = world[x][y];
                }
            }
            if (showEnemyPath) {
                if (enemyPath != null) {
                    for (Point point : enemyPath) {
                        if (!point.equals(avatar.getPos()) && !point.equals(enemy.getPos())) {
                            renderTiles[point.getX()][point.getY()] = Tileset.TREE;
                        }
                    }
                }
            }
            if (isLineOfSight) {
                for (int x = 0; x < WIDTH; x++) {
                    for (int y = 0; y < HEIGHT; y++) {
                        if (!visibilityMap[x][y]) {
                            renderTiles[x][y] = Tileset.NOTHING;
                        }
                    }
                }
            }
            ter.renderFrame(renderTiles);
            drawHud();
            StdDraw.show();
        }
    }

    /**
     * Draws the enemy's position on the world grid.
     *
     * @param enemyPos The position of the enemy in string format "x y".
     */
    public void drawEnemyPos(String enemyPos) {
        if (enemyPos != null) {
            String[] tokens;
            tokens = enemyPos.split(" ");
            int x = Integer.parseInt(tokens[0]), y = Integer.parseInt(tokens[1]);
            world[enemy.getPos().getX()][enemy.getPos().getY()] = Tileset.FLOOR;
            enemy.getPos().setX(x);
            enemy.getPos().setY(y);
            world[enemy.getPos().getX()][enemy.getPos().getY()] = enemy.getImg();
            enemyPath = pathFinder.findPath(world, enemy.getPos(), avatar.getPos());
            started = false;
        }
    }

    /**
     * Sets the avatar's life count based on the given string.
     *
     * @param lives The number of lives to set for the avatar.
     */
    public void handleGivenAvatarLife(String lives) {
        if (lives != null) {
            avatar.setLife(Integer.parseInt(lives));
            started = false;
        }
    }

    /**
     * Draws flowers at their designated positions on the world grid.
     */
    public void drawFlowers() {
        if (flowerPositions == null) {
            return;
        }
        for (Point f : flowerPositions) {
            world[f.getX()][f.getY()] = Tileset.FLOWER;
        }
    }

    /**
     * Checks if the avatar has caught a flower and updates the flower count and positions.
     */
    public void checkIfCatchFlower() {
        if (flowerPositions.contains(avatar.getPos())) {
            flowers--;
            flowerPositions.remove(avatar.getPos());
        }
    }

    /**
     * Checks if the win condition (collecting all flowers) has been met.
     *
     * @return true if all flowers are collected, false otherwise.
     */
    public boolean checkIfWin() {
        return flowers == 0;
    }

    /**
     * Checks if the losing condition (avatar life reaching zero) has been met.
     *
     * @return true if avatar life is zero, false otherwise.
     */
    public boolean checkIfLost() {
        return avatar.getLife() == 0;
    }

    /**
     * Checks if the enemy has caught the avatar and updates avatar's life accordingly.
     */
    public void checkIfEnemyCatch() {
        if (enemy.getPos().equals(avatar.getPos())) {
            avatar.reduceLife();
            if (avatar.getLife() > 0) {
                placeAvatarAtBegin();
            }
        }
    }

    /**
     * Displays the winning frame when the game is won.
     */
    public void drawWinningFrame() {
        StdDraw.setCanvasSize(300, 300);
        StdDraw.setFont(new Font("Apple Casual", Font.BOLD, 10));
        StdDraw.setXscale(0, 300);
        StdDraw.setYscale(0, 300);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(150, 150, "Congrats! You won");
        StdDraw.show();
    }

    /**
     * Displays the losing frame when the game is lost.
     */
    public void drawLosingFrame() {
        StdDraw.setCanvasSize(300, 300);
        StdDraw.setFont(new Font("Apple Casual", Font.BOLD, 10));
        StdDraw.setXscale(0, 300);
        StdDraw.setYscale(0, 300);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(150, 150, "Game Over! You Lost");
        StdDraw.show();
    }

    /**
     * Places the avatar back at the starting position.
     */
    public void placeAvatarAtBegin() {
        Point startPos = avatar.getStartPos();
        world[startPos.getX()][startPos.getY()] = avatar.getImg();
        resetVisibility();
        addPathToBegin();
        avatar.getPos().setX(startPos.getX());
        avatar.getPos().setY(startPos.getY());
        calculateVisibility();
    }

    /**
     * Adds the path back to the starting position to the movement history.
     */
    public void addPathToBegin() {
        List<Point> pathToBegin = pathFinder.findPath(world, avatar.getPos(), avatar.getStartPos());

        if (pathToBegin != null && pathToBegin.size() > 1) {
            StringBuilder path = new StringBuilder();

            Point current, previous;
            for (int i = 1; i < pathToBegin.size(); i++) {
                current = pathToBegin.get(i);
                previous = pathToBegin.get(i - 1);

                if (current.getX() > previous.getX()) {
                    // Moving right
                    path.append("d");
                } else if (current.getX() < previous.getX()) {
                    // Moving left
                    path.append("a");
                } else if (current.getY() > previous.getY()) {
                    // Moving up
                    path.append("w");
                } else if (current.getY() < previous.getY()) {
                    // Moving down
                    path.append("s");
                }
            }
            movementHistory += path.toString();
        }
    }

    /**
     * Draws the Heads-Up Display (HUD) showing the game's status like remaining life and flowers count.
     */
    public void drawHud() {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.rectangle(5, HEIGHT + 2, WIDTH, 2);

        StdDraw.text(5, HEIGHT + 2.5, "Remaining Life: ");

        for (int i = 1; i <= avatar.getLife(); i++) {
            StdDraw.text(8 + i, HEIGHT + 2.5, "♥");
        }
        StdDraw.text(5.8, HEIGHT + 1.5, " Number of Flowers: " + flowers);

        int mX = Math.min((int) StdDraw.mouseX(), WIDTH - 1);
        int mY = Math.min((int) StdDraw.mouseY(), HEIGHT - 1);
        StdDraw.text(62, HEIGHT + 2.5, "Cursor at: " + world[mX][mY].description());

        StdDraw.text(40, HEIGHT + 2.5, "Press v to toggle vision");
        StdDraw.text(42.5, HEIGHT + 1.5, "Press k to see enemy's coming path");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        StdDraw.text(WIDTH - 8, HEIGHT + 2.25, formatter.format(date));
    }

    /**
     * Moves the enemy using the AStarPathFinder.
     */
    private void moveEnemy() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastEnemyMoveTime > 200) {
            if (enemyPath != null && (enemyPath.size() > 1)) {
                Point enemyLoc = enemy.getPos();
                world[enemyLoc.getX()][enemyLoc.getY()] = Tileset.FLOOR;
                enemy.getPos().setX(enemyPath.get(1).getX());
                enemy.getPos().setY(enemyPath.get(1).getY());
                enemyPath.remove(1);
                world[enemy.getPos().getX()][enemy.getPos().getY()] = enemy.getImg();
            }

            if (random.nextInt(10) < 7) {
                enemyPath = pathFinder.findPath(world, enemy.getPos(), avatar.getPos());
            }
            lastEnemyMoveTime = currentTime;
        }

    }

    /**
     * Writes the current game state to a save file for the specified slot.
     *
     * @param slot The save slot number to write to.
     */
    private void writeHistoryToFile(Integer slot) {
        switch (slot) {
            case 2:
                Out out2 = new Out("./saves/save-slot-2.txt");
                out2.println("n" + seed + "s" + movementHistory);
                out2.println(enemy.getPos().getX() + " " + enemy.getPos().getY());
                out2.println(avatar.getLife());
                out2.close();
                System.exit(0);
                break;
            case 3:
                Out out3 = new Out("./saves/save-slot-3.txt");
                out3.println("n" + seed + "s" + movementHistory);
                out3.println(enemy.getPos().getX() + " " + enemy.getPos().getY());
                out3.println(avatar.getLife());
                out3.close();
                System.exit(0);
                break;
            default:
                Out out = new Out("./saves/save-slot-1.txt");
                out.println("n" + seed + "s" + movementHistory);
                out.println(enemy.getPos().getX() + " " + enemy.getPos().getY());
                out.println(avatar.getLife());
                out.close();
                System.exit(0);
        }
    }

    /**
     * Draws the start menu of the game.
     */
    public void drawStart() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(WIDTH * 7, HEIGHT * 10, "CS61B: THE GAME");
        StdDraw.text(WIDTH * 7, HEIGHT * 7, "NEW GAME (N)");
        for (int i = 1; i <= 3; i++) {
            String saveFileName = "save-slot-" + i + ".txt";
            String text = "LOAD GAME ";
            if (i == 1) {
                text += "1 (L)" + " (" + saveFileName + ")";
                StdDraw.text(WIDTH * 7, HEIGHT * (7 - i), text);
            } else {
                text += i + " (" + i + ")" + " (" + saveFileName + ")";
                StdDraw.text(WIDTH * 7, HEIGHT * (7 - i), text);
            }
        }
        StdDraw.text(WIDTH * 7, HEIGHT * 3, "QUIT (Q)");
        StdDraw.show();
    }

    /**
     * Waits for the player to press a key to choose an option from the start menu.
     *
     * @return The character corresponding to the player's choice.
     */
    public char getOption() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                key = Character.toLowerCase(key);

                if (key == 'n' || key == 'l' || key == '2' || key == '3' || key == 'q') {
                    return key;
                }
            }
        }
    }

    /**
     * Gets the seed input from the player and constructs the seed string.
     *
     * @return The seed string.
     */
    public String getSeed() {
        StringBuilder givenSeed = new StringBuilder();
        char tmp = '\0';
        StdDraw.text(WIDTH*7, HEIGHT*8, "SEED: ");
        StdDraw.show();
        int i = 1;
        while (tmp != 's') {
            if (StdDraw.hasNextKeyTyped()) {
                tmp = StdDraw.nextKeyTyped();
                tmp = Character.toLowerCase(tmp);
                if (tmp == '\b' && givenSeed.length() > 0) {
                    givenSeed.deleteCharAt(givenSeed.length() - 1);
                } else if (Character.isDigit(tmp)) {
                    givenSeed.append(tmp);
                }
                drawStart();
                StdDraw.text(WIDTH * 7, HEIGHT * 8, "SEED: " + givenSeed.toString());
                StdDraw.show();
            }
        }
        this.seed = givenSeed.toString();
        return givenSeed.toString();
    }

    /**
     * Reads and processes the input string to set up the world.
     *
     * @param input The input string containing the seed and commands.
     * @return The world grid after processing the input.
     */
    public TETile[][] getWorldFromInput(String input) {
        input = input.toLowerCase();
        if (input.charAt(0) == 'l') {
            In in = new In("data.txt");

            if (!in.isEmpty()) {
                input = in.readLine() + input.substring(1);
            }
            in.close();
        }

        StringBuilder givenSeed = new StringBuilder();
        int i = 1;
        while (input.charAt(i) != 's') {
            givenSeed.append(input.charAt(i));
            i++;
        }
        i++;
        this.seed = givenSeed.toString();


        buildWorld();

        Point rndPt;
        int avtrInd = random.nextInt(rooms.size());
        Room randRoom = rooms.get(avtrInd);

        rndPt = randRoom.getRandomPointInRoom(random);
        avatar = new Avatar(rndPt, new Point(rndPt.getX(), rndPt.getY()), Tileset.HERO);
        world[rndPt.getX()][rndPt.getY()] = avatar.getImg();


        for (int k = 0; k < flowers; k++) {
            randRoom = rooms.get(random.nextInt(rooms.size()));
            rndPt = randRoom.getRandomPointInRoom(random);
            flowerPositions.add(rndPt);
            world[rndPt.getX()][rndPt.getY()] = Tileset.FLOWER;
        }

        int enemyInd = random.nextInt(rooms.size());
        while (enemyInd != avtrInd) {
            enemyInd = random.nextInt(rooms.size());
        }
        randRoom = rooms.get(random.nextInt(rooms.size()));
        rndPt = randRoom.getRandomPointInRoom(random);


        enemy = new Avatar(rndPt, new Point(rndPt.getX(), rndPt.getY()), Tileset.ENEMY);
        world[enemy.getPos().getX()][enemy.getPos().getY()] = enemy.getImg();

        calculateVisibility();

        enemyPath = pathFinder.findPath(world, enemy.getPos(), avatar.getPos());


        while (i < input.length()) {
            updateWorld(input.charAt(i));
            i++;
        }

        this.movementHistory = "";
        return world;
    }

    /**
     * Updates the world based on a movement command from the player.
     *
     * @param m The character representing the movement command.
     */
    public void updateWorld(char m) {

        if (m == 'k') {
            changeShowEnemyPath();
            return;
        }
        if (m == 'v') {
            changeLineOfSight();
            return;
        }
        if (m != 'a' && m != 's' && m != 'w' && m != 'd' && m != ':' && m != 'q' && m != '2' && m != '3') {
            return;
        }

        if (!movementHistory.isEmpty() && movementHistory.charAt(movementHistory.length() - 1) == ':') {
            movementHistory = movementHistory.substring(0, movementHistory.length() - 1);
            if (m == 'q') {
                Out out = new Out("data.txt");
                out.println("n" + seed + "s" + movementHistory);
                out.println(enemy.getPos().getX() + " " + enemy.getPos().getY());
                out.println(avatar.getLife());
                out.close();
                return;
            }
        }

        int x = this.avatar.getPos().getX();
        int y = this.avatar.getPos().getY();

        switch (m) {
            case 'a':
                x -= 1;
                started = true;
                break;
            case 'w':
                y += 1;
                started = true;
                break;
            case 'd':
                x += 1;
                started = true;
                break;
            case 's':
                y -= 1;
                started = true;
                break;
            default:
                //DO NOTHING
        }
        if (!world[x][y].equals(Tileset.WALL_GRAY)) {

            resetVisibility();
            world[avatar.getPos().getX()][avatar.getPos().getY()] = Tileset.FLOOR;

            avatar.getPos().setX(x);
            avatar.getPos().setY(y);

            world[x][y] = avatar.getImg();
            checkIfCatchFlower();

            calculateVisibility();
        }
        this.movementHistory += m;

    }

    /**
     * Initializes the world by setting all tiles to a default state.
     */
    public void initWorld() {
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Generates a random number of rooms based on the specified maximum and places them in the world.
     *
     * @param numRooms The number of rooms to generate.
     */
    public void generateRooms(int numRooms) {
        while (rooms.size() < numRooms) {
            Room newR = randomRoomGenerator();
            if (newR != null) {
                rooms.add(newR);
                drawRoom(newR);
            }
        }
    }

    /**
     * Draws the specified room in the world with walls and floors.
     *
     * @param r The room to draw.
     */
    public void drawRoom(Room r) {
        drawWalls(r);
        fillFloor(r);
    }

    /**
     * Draws the walls of the given room in the world. The walls are placed at the outer edges
     * of the room, defined by the bottom left and top right points.
     *
     * @param r The room for which to draw walls.
     */
    private void drawWalls(Room r) {
        Point bottomLeft = r.getBottomLeft();
        Point topRight = r.getTopRight();

        for (int x = bottomLeft.getX(); x < topRight.getX(); x++) {
            world[x][bottomLeft.getY()] = Tileset.WALL_GRAY;
            world[x][topRight.getY() - 1] = Tileset.WALL_GRAY;
        }

        for (int y = bottomLeft.getY(); y < topRight.getY(); y++) {
            world[bottomLeft.getX()][y] = Tileset.WALL_GRAY;
            world[topRight.getX() - 1][y] = Tileset.WALL_GRAY;
        }
    }

    /**
     * Fills the floor area of the given room in the world. The floor is placed within the walls
     * of the room, defined by the bottom left and top right points.
     *
     * @param r The room for which to fill the floor.
     */
    private void fillFloor(Room r) {
        Point bottomLeft = r.getBottomLeft();
        Point topRight = r.getTopRight();

        for (int x = bottomLeft.getX() + 1; x < topRight.getX() - 1; x++) {
            for (int y = bottomLeft.getY() + 1; y < topRight.getY() - 1; y++) {
                world[x][y] = Tileset.FLOOR;
            }
        }
    }

    /**
     * Connects all rooms in the world with hallways, ensuring every room is reachable.
     */
    public void connectRooms() {

        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                int distance = rooms.get(i).distanceFromOtherRoom(rooms.get(j));
                edges.add(new Edge(rooms.get(i), rooms.get(j), distance));
            }
        }

        Collections.sort(edges);

        WeightedQuickUnion wqu = new WeightedQuickUnion(rooms.size());

        List<Edge> mst = new ArrayList<>();

        for (Edge edge : edges) {
            int r1 = rooms.indexOf(edge.getRoom1());
            int r2 = rooms.indexOf(edge.getRoom2());
            if (!wqu.connected(r1, r2)) {
                wqu.union(r1, r2);
                mst.add(edge);
            }
        }

        for (Edge edge : mst) {
            drawHallway(edge.getRoom1(), edge.getRoom2());
        }
    }

    /**
     * Draws a hallway between two rooms
     *
     * @param r1 The first room to connect.
     * @param r2 The second room to connect.
     */
    public void drawHallway(Room r1, Room r2) {
        Point start = r1.getRandomPointInRoom(random);
        Point end = r2.getRandomPointInRoom(random);

        boolean horizontal = Math.abs(start.getX() - end.getX()) > Math.abs(start.getY() - end.getY());
        Point turningPoint = horizontal ? new Point(end.getX(), start.getY()) : new Point(start.getX(), end.getY());

        drawSegment(start, turningPoint, horizontal);
        drawSegment(turningPoint, end, !horizontal);
    }

    /**
     * Draws a segment of a hallway between two points in the world. This method will draw a straight
     * line of floor tiles from the start point to the end point, adding walls on either side of the
     * segment if necessary. It is used as part of the hallway drawing process, typically for creating
     * straight hallway sections before a turn.
     *
     * @param start The starting point of the segment.
     * @param end   The ending point of the segment.
     */
    private void drawSegment(Point start, Point end, boolean isHorizontal) {
        if (isHorizontal) {
            for (int x = start.getX(); x != end.getX(); x += Integer.signum(end.getX() - start.getX())) {
                drawHallwayTile(x, start.getY());
            }
        } else {
            for (int y = start.getY(); y != end.getY(); y += Integer.signum(end.getY() - start.getY())) {
                drawHallwayTile(start.getX(), y);
            }
        }
    }

    /**
     * Draws a single hallway tile and its surrounding walls at the specified coordinates.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     */
    private void drawHallwayTile(int x, int y) {
        if (x <= 0 || x >= WIDTH - 1 || y <= 0 || y >= HEIGHT - 1) {
            return;
        }

        if (world[x][y] == Tileset.NOTHING || world[x][y] == Tileset.WALL_GRAY) {
            world[x][y] = Tileset.FLOOR;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {

                    if ((i == 0 && j == 0)) {
                        continue;
                    }
                    int wallX = x + i;
                    int wallY = y + j;

                    if (world[wallX][wallY] == Tileset.NOTHING) {
                        world[wallX][wallY] = Tileset.WALL_GRAY;
                    }
                }
            }
        }
    }

    /**
     * Generates a random room within the world bounds.
     *
     * @return A randomly generated room that does not overlap with existing rooms.
     */
    public Room randomRoomGenerator() {
        int attempts = 0;
        while (attempts < 100) {
            Point pos = randomPointOnWorld();
            int width = RandomUtils.uniform(random, 4, maxRoomWid);
            int height = RandomUtils.uniform(random, 4, maxRoomHeight);
            Room newRoom = new Room(pos, height, width);

            if (!doesIntersect(newRoom)) {
                return newRoom;
            }
            attempts++;
        }
        return null;
    }

    /**
     * Given a room object, checks whether the given room intersects with the other rooms in the world
     *
     * @param newRoom The Room object that might overlap with the other rooms
     */
    private boolean doesIntersect(Room newRoom) {
        for (Room room : rooms) {
            if (room.intersects(newRoom)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a random point on the world grid that could potentially be the bottom left of a room.
     *
     * @return A random point within the world boundaries.
     */
    public Point randomPointOnWorld() {
        int x = 1 + RandomUtils.uniform(random, world.length - maxRoomWid);
        int y = 1 + RandomUtils.uniform(random, world[0].length - maxRoomHeight);

        return new Point(x, y);
    }
}
