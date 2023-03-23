package handlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import objects.User;
import objects.WineStore;
import objects.WineUser;

public class StateHandler {
    private static final int STARTING_BALANCE = 200;
    private static final String USERS_FILE = "users.ser";
    private static final String WINES_FILE = "wines.ser";

    // return codes
    public static final int SUCCESS = 0;
    public static final int WINE_DOES_NOT_EXIST = -1;
    public static final int SELLER_DOES_NOT_EXIST = -2;
    public static final int USER_DOES_NOT_EXIST = -3;
    public static final int SELLER_DOES_NOT_HAVE_WINE = -4;
    public static final int SELLER_DOES_NOT_HAVE_ENOUGH_WINE = -5;
    public static final int BUYER_DOES_NOT_HAVE_ENOUGH_MONEY = -6;
    public static final int WINE_ALREADY_EXISTS = -7;
    public static final int USER_ALREADY_EXISTS = -8;

    private static StateHandler instance = null;

    // --- these need to be saved to a json file
    private HashMap<String, User> users;
    private HashMap<String, WineStore> wines;
    // ---

    private StateHandler() {
        this.users = new HashMap<String, User>();
        this.wines = new HashMap<String, WineStore>();
        loadUsers();
        loadWines();
    }

    public static StateHandler getInstance() { // Singleton
        if (instance == null)
            instance = new StateHandler();
        return instance;
    }

    public int addUser(String name) {
        if (users.containsKey(name)) // User already exists
            return USER_ALREADY_EXISTS;
        User user = new User(name, STARTING_BALANCE);
        this.users.put(name, user);
        syncUsers();
        return 0; // Success
    }

    public int addWine(String name, String image) {
        if (wines.containsKey(name)) // Wine already exists
            return WINE_ALREADY_EXISTS;
        WineStore wine = new WineStore(name, image);
        this.wines.put(name, wine);
        syncWines();
        return 0; // Success
    }

    public int addWineListingToUser(String user, String wine, int quantity, float price) {
        if (!wines.containsKey(wine)) // Wine does not exist
            return WINE_DOES_NOT_EXIST;

        if (!users.containsKey(user)) // User does not exist
            return USER_DOES_NOT_EXIST;

        users.get(user).addWineListing(new WineUser(wine, price, quantity));
        syncUsers();
        return SUCCESS; // Success
    }

    public String wineView(String wine) {
        System.out.println(wines.toString());

        if (!wines.containsKey(wine.trim())) // Wine does not exist
            return "Wine does not exist";

        WineStore wineO = wines.get(wine.trim()); // trim just in case

        StringBuilder result = new StringBuilder();

        result.append("-------------------------\n");
        result.append("Name : " + wineO.getName() + "\n");
        result.append("Image path: " + wineO.getImage() + "\n");
        result.append("Average rating : " + wineO.getEvaluation() + " (" + wineO.getNrOfEvaluations() + ")\n");
        result.append("Listings : \n");

        users.forEach((k, v) -> {
            for (WineUser wineO2 : v.getWines())
                if (wineO2.getName().equals(wine))
                    result.append(
                            "\t" + v.getName() + " : " + wineO2.getQuantity() + " bottles at " + wineO2.getPrice()
                                    + "\n");
        });

        result.append("-------------------------\n");

        return result.toString();
    }

    public int buySellWine(String seller, String buyer, String wine, int quantity) {
        if (!wines.containsKey(wine)) // Wine does not exist
            return WINE_DOES_NOT_EXIST;

        if (!users.containsKey(seller)) // Seller does not exist
            return SELLER_DOES_NOT_EXIST;

        if (!users.containsKey(buyer)) // Buyer does not exist
            return USER_DOES_NOT_EXIST;

        WineUser wineO = users.get(seller).getWine(wine);
        if (wineO == null) // Seller does not have wine
            return SELLER_DOES_NOT_HAVE_WINE;

        if (wineO.getQuantity() < quantity) // Seller does not have enough wine
            return SELLER_DOES_NOT_HAVE_ENOUGH_WINE;

        User sellerO = users.get(seller);
        User buyerO = users.get(buyer);

        float price = wineO.getPrice() * quantity;
        if (buyerO.getBalance() < price) // Buyer does not have enough money
            return BUYER_DOES_NOT_HAVE_ENOUGH_MONEY;

        sellerO.setBalance(sellerO.getBalance() + price);
        buyerO.setBalance(buyerO.getBalance() - price);

        wineO.setQuantity(wineO.getQuantity() - quantity);
        if (wineO.getQuantity() == 0)
            sellerO.removeWine(wine);

        System.out.println(quantity + " bottles of " + wine + "bought/sold successfully");

        syncUsers();
        return SUCCESS;
    }

    public float getBalance(String user) {
        if (!users.containsKey(user))
            return USER_DOES_NOT_EXIST;
        return users.get(user).getBalance();
    }

    public int classify(String wine, String stars) {
        if (!wines.containsKey(wine)) // Wine does not exist
            return WINE_DOES_NOT_EXIST;

        WineStore wineO = wines.get(wine);
        wineO.newEvaluation(Float.parseFloat(stars));

        System.out.println("tens truwe");
        syncWines();
        System.out.println("che dred");
        return SUCCESS;
    }

    public int talk(String from, String to, String message) {
        if (!users.containsKey(to)) // User does not exist
            return USER_DOES_NOT_EXIST;

        User userO = users.get(to);
        userO.addMessage(from, message);
        syncUsers();
        return SUCCESS;
    }

    public List<String> read(String user) {
        if (!users.containsKey(user)) // User does not exist
            return null;

        List<String> result = new LinkedList<>();

        users.get(user).readInbox().forEach((k, v) -> {
            result.add(k + " : " + v);
        });
        syncUsers(); // because of when the user reads the messages, they are deleted
        return result;
    }

    public void syncUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (FileNotFoundException e) {
            System.out.println("Users file not found");
        } catch (IOException e) {
            System.out.println("Cant serialize users");
        }
    }

    public void syncWines() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(WINES_FILE))) {
            oos.writeObject(wines);
        } catch (FileNotFoundException e) {
            System.out.println("Wines file not found");
        } catch (IOException e) {
            System.out.println("Cant serialize wines");
        }
    }

    public void sync() {
        syncUsers();
        syncWines();
    }

    public void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            users = (HashMap<String, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Users file not found");
        } catch (IOException e) {
            System.out.println("Error reading users file");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
        }
    }

    public void loadWines() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(WINES_FILE))) {
            wines = (HashMap<String, WineStore>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Wines file not found");
        } catch (IOException e) {
            System.out.println("Error reading wines file");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
        }
    }

    public void load() {
        loadUsers();
        loadWines();
    }
}
