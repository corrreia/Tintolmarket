package handlers;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import objects.User;
import objects.WineStore;
import objects.WineUser;

public class StateHandler {
    private static final int STARTING_BALANCE = 200;
    private static final String USERS_JSON = "users.json";
    private static final String WINES_JSON = "wines.json";

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
        return 0; // Success
    }

    public int addWine(String name, String image) {
        if (wines.containsKey(name)) // Wine already exists
            return WINE_ALREADY_EXISTS;
        WineStore wine = new WineStore(name, image);
        this.wines.put(name, wine);
        return 0; // Success
    }

    public int addWineListingToUser(String user, String wine, int quantity, float price) {
        if (!wines.containsKey(wine)) // Wine does not exist
            return WINE_DOES_NOT_EXIST;

        if (!users.containsKey(user)) // User does not exist
            return USER_DOES_NOT_EXIST;

        users.get(user).addWineListing(new WineUser(wine, price, quantity));

        return SUCCESS; // Success
    }

    public String wineView(String wine) {
        System.out.println(wines.toString());

        if (!wines.containsKey(wine.trim())) // Wine does not exist
            return "nao contem";

        WineStore wineO = wines.get(wine);

        StringBuilder result = new StringBuilder();

        result.append(wineO.getName() + " " + wineO.getImage());

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
        wineO.newEvaluation(Integer.parseInt(stars));

        return SUCCESS;
    }

    public int talk(String from, String to, String message) {
        if (!users.containsKey(to)) // User does not exist
            return USER_DOES_NOT_EXIST;

        User userO = users.get(to);
        // TODO

        return SUCCESS;
    }

    public List<String> read() {
        // TODO
        return null;
    }

    public void syncUsersJson() {
        // TODO
    }

    public void syncWinesJson() {
        // TODO
    }

    public void syncJson() {
        syncUsersJson();
        syncWinesJson();
    }

    public void loadUsersJson() {
        // TODO
    }

    public void loadWinesJson() {
        // TODO
    }

    public void loadJson() {
        loadUsersJson();
        loadWinesJson();
    }
}
