package handlers;

import java.util.HashMap;

import objects.User;
import objects.WineStore;
import objects.WineUser;

public class StateHandler {
    private static final int STARTING_BALANCE = 200;
    private static final String USERS_JSON = "users.json";
    private static final String WINES_JSON = "wines.json";

    //return codes
    public static final int SUCCESS = 0;
    public static final int WINE_DOES_NOT_EXIST = -1;
    public static final int SELLER_DOES_NOT_EXIST = -2;
    public static final int USER_DOES_NOT_EXIST = -3;
    public static final int SELLER_DOES_NOT_HAVE_WINE = -4;
    public static final int SELLER_DOES_NOT_HAVE_ENOUGH_WINE = -5;
    public static final int BUYER_DOES_NOT_HAVE_ENOUGH_MONEY = -6;

    private static StateHandler instance = null;

    //--- these need to be saved to a json file
    private HashMap<String, User> users;
    private HashMap<Integer, WineStore> wines;
    private static int nextWineId = 0;
    //---

    private StateHandler() {
        this.users = new HashMap<String, User>();
        this.wines = new HashMap<Integer, WineStore>();
    }

    public static StateHandler getInstance() {  // Singleton
        if (instance == null) 
            instance = new StateHandler();
        return instance;
    }

    public void registerUser(String name) {
        User user = new User(name, STARTING_BALANCE);
        this.users.put(name, user);
    }

    public void registerWine(String name, String image) {
        WineStore wine = new WineStore(nextWineId, name, image);
        this.wines.put(nextWineId, wine);
        nextWineId++;
    }

    public int addWineListingToUser(String user, int wineId, int quantity, float price) {
        if (!wines.containsKey(wineId)) // Wine does not exist
            return WINE_DOES_NOT_EXIST;
        
        if (!users.containsKey(user)) // User does not exist
            return USER_DOES_NOT_EXIST;

        WineStore wine = wines.get(wineId);
        users.get(user).addWineListing(new WineUser(wineId, wine.getName(), price, quantity));

        return 0; // Success
    }

    public int buySellWine(String seller, String buyer, int wineID, int quantity) {
        if (!wines.containsKey(wineID))  // Wine does not exist
            return WINE_DOES_NOT_EXIST;
        
        if (!users.containsKey(seller)) // Seller does not exist
            return SELLER_DOES_NOT_EXIST;

        if (!users.containsKey(buyer)) // Buyer does not exist
            return USER_DOES_NOT_EXIST;

        WineUser wine = users.get(seller).getWine(wineID); 
        if (wine == null) // Seller does not have wine
            return SELLER_DOES_NOT_HAVE_WINE;

        if (wine.getQuantity() < quantity) // Seller does not have enough wine
            return SELLER_DOES_NOT_HAVE_ENOUGH_WINE;

        User sellerO = users.get(seller);
        User buyerO = users.get(buyer);

        float price = wine.getPrice() * quantity;
        if (buyerO.getBalance() < price) // Buyer does not have enough money
            return BUYER_DOES_NOT_HAVE_ENOUGH_MONEY;

        sellerO.setBalance(sellerO.getBalance() + price);
        buyerO.setBalance(buyerO.getBalance() - price);

        wine.setQuantity(wine.getQuantity() - quantity);
        if (wine.getQuantity() == 0)
            sellerO.removeWine(wineID);

        return SUCCESS;
    }

    public float getBalance(String user){
        if (!users.containsKey(user))
            return USER_DOES_NOT_EXIST;
        return users.get(user).getBalance();
    }


    public void syncUsersJson() {
        //TODO
    }

    public void syncWinesJson() {
        //TODO
    }

    public void syncJson() {
        syncUsersJson();
        syncWinesJson();
    }

    public void loadUsersJson() {
        //TODO
    }

    public void loadWinesJson() {
        //TODO
    }

    public void loadJson() {
        loadUsersJson();
        loadWinesJson();
    }
}
