package de.szut.zuul.gamecontrol;

import de.szut.zuul.exceptions.ItemNotFoundException;
import de.szut.zuul.model.Herb;
import de.szut.zuul.model.Item;
import de.szut.zuul.model.Player;
import de.szut.zuul.model.Room;

/**
 * This class is the main class of the "World of Zuul" application.
 * "World of Zuul" is a very simple, text based adventure game.  Users
 * can walk around some scenery. That's all. It should really be extended
 * to make it more interesting!
 * <p>
 * To play this game, create an instance of this class and call the "play"
 * method.
 * <p>
 * This main class creates and initialises all the others: it creates all
 * rooms, creates the parser and starts the game.  It also evaluates and
 * executes the commands that the parser returns.
 *
 * @author Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

public class Game {
    private Parser parser;
    private Player player;


    /**
     * Create the game and initialise its internal map.
     */
    public Game() {
        this.player = new Player();
        createRooms();
        parser = new Parser();

    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms() {
        Room marketsquare, templePyramid, tavern, sacrificialSite, hut, jungle, secretPassage, cave, beach, roomOfMage, basement;
        Item bow, treasure, arrow, plant, cacoa, knife, spear, food, jewelry, muffin;

        // create the rooms
        marketsquare = new Room("on the market square");
        templePyramid = new Room("in a temple pyramid");
        tavern = new Room("in the tavern at the market square");
        sacrificialSite = new Room("at a sacrificial site");
        hut = new Room("in a hut");
        jungle = new Room("in the jungle");
        secretPassage = new Room("in a secret passage");
        cave = new Room("in a cave");
        beach = new Room("on the beach");
        roomOfMage = new Room("in the room of the mage");
        basement = new Room("in a dark basement");
        //create the items

        //  bow = new Item("Bow", "A wood bow", 9.1);
        muffin = new Item("Muffin", "muffin", 0,true);
        treasure = new Item("Treasure", "a little treasure with coins", 7.5,false);
        arrow = new Item("Arrow", "A quiver with various arrows", 1,false);
        plant = new Herb("Plant", "A healing plant", 0.5,true);
        cacoa = new Item("Cacoa", "A small cacoatree", 5,true);
        knife = new Item("Knife", "A very sharp and big knife", 1,true);
        spear = new Item("Spear", "a spear with a sharp tip", 5,false);
        food = new Item("Food", "a plate with hearty meat and corn porridge", 0.5,false);
        jewelry = new Item("Jewelry", "A nice hat", 1,false);

        // initialise room exits
        marketsquare.setExit("west", sacrificialSite);
        marketsquare.setExit("north", tavern);
        marketsquare.setExit("east", templePyramid);
        templePyramid.setExit("west", marketsquare);
        templePyramid.setExit("north", hut);
        templePyramid.setExit("up", roomOfMage);
        templePyramid.setExit("down", basement);
        tavern.setExit("south", marketsquare);
        tavern.setExit("east", hut);
        sacrificialSite.setExit("east", marketsquare);
        sacrificialSite.setExit("down", cave);
        hut.setExit("west", tavern);
        hut.setExit("south", templePyramid);
        hut.setExit("east", jungle);
        jungle.setExit("west", hut);
        secretPassage.setExit("west", cave);
        secretPassage.setExit("east", basement);
        cave.setExit("up", sacrificialSite);
        cave.setExit("east", secretPassage);
        cave.setExit("south", beach);
        beach.setExit("north", cave);
        roomOfMage.setExit("down", templePyramid);
        basement.setExit("up", templePyramid);
        basement.setExit("west", secretPassage);


        //Putting Items in Room
        marketsquare.putItem(muffin);
        marketsquare.putItem(plant);
        cave.putItem(treasure);
        roomOfMage.putItem(arrow);
        jungle.putItem(plant);
        jungle.putItem(cacoa);
        sacrificialSite.putItem(knife);
        hut.putItem(spear);
        tavern.putItem(food);
        basement.putItem(jewelry);


        player.goTo(marketsquare); // start game on marketsquare

    }

    /**
     * Main play routine.  Loops until end of play.
     */
    public void play() {
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    private void printRaumInfo() {

        System.out.println(player.getCurrentRoom().getLongDescription());

    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        printRaumInfo();
    }

    /**
     * Given a command, process (that is: execute) the command.
     *
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) {
        boolean wantToQuit = false;

        if (command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        } else if (commandWord.equals("go")) {
            goRoom(command);
        } else if (commandWord.equals("look")) {
            look();

        } else if (commandWord.equals("take")) {
            takeItem(command);
            System.out.println(player.showStatus());
            System.out.println(player.getCurrentRoom().getLongDescription());

        } else if (commandWord.equals("drop")) {
            dropItem(command);
            System.out.println(player.showStatus());
            System.out.println(player.getCurrentRoom().getLongDescription());
        } else if (commandWord.equals("eat")) {
            eatItem(command);

        } else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }

        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the
     * command words.
     */
    private void printHelp() {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("through the jungle. At once there is a glade. On it there a buildings...");
        System.out.println();
        System.out.println("Your command words are:");
        System.out.println(parser.showCommands());
    }

    /**
     * Try to go in one direction. If there is an exit, enter
     * the new room, otherwise print an error message.
     */
    private void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = null;
        nextRoom = player.getCurrentRoom().getExit(direction);


        if (nextRoom == null) {
            System.out.println("There is no door!");
        } else {
            player.goTo(nextRoom);
            printRaumInfo();
        }
    }

    private void look() {
        System.out.println(player.getCurrentRoom().getLongDescription());
    }

    private void takeItem(Command command) {
        if (player.takeItem(player.getCurrentRoom().itemList.get(command.getSecondWord()))) {
            player.getCurrentRoom().removeItem(command.getSecondWord());

        } else {
            System.out.println("Item ist zu schwer oder konnte nicht gefunden werden");
        }

    }

    private void dropItem(Command command) {

        try {
            player.dropItem(command.getSecondWord());
            throw new ItemNotFoundException();
        } catch (ItemNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void eatItem(Command command) {
        System.out.println(player.eatItem(command.getSecondWord()));
        if(player.eatItem(command.getSecondWord())){
            player.eatItem(command.getSecondWord());
        }else{
            System.out.println("Das kann ich nicht essen");
        }




    }

    /**
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     *
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) {
        if (command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        } else {
            return true;  // signal that we want to quit
        }
    }
}
