package core;

/**
 * The Main class serves as the entry point for the world generation and interaction program.
 * It contains the main method which parses command line arguments and initializes the game world.
 * or lets user play from the keyboard
 */
public class Main {
    /**
     * The main method serves as the entry point for the application.
     * It processes command line arguments and initializes the world accordingly.
     *
     * @param args Command line arguments passed to the program. The program expects at most one argument.
     *             If one argument is provided, it is used as input to generate the world.
     *             If no arguments are provided, the program starts in interactive mode, allowing the user to
     *             interact with the world via keyboard.
     */
    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println("Can only have one argument - the input string");
            System.exit(0);
        } else if (args.length == 1) {
            World world = new World();
            world.getWorldFromInput(args[0]);
        } else {
            World world = new World();
            world.interactWithKeyboard();
        }
    }
}
