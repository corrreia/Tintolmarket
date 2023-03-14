package handlers;

import java.util.HashMap;

import objects.User;
import objects.WineStore;
import objects.WineUser;

public class StateHandler {
    private static final int STARTING_BALANCE = 200;
    private static final String USERS_JSON = "users.json";
    private static final String WINES_JSON = "wines.json";

    private static StateHandler instance = null;

    //--- these need to be saved to a json file
    private HashMap<Integer, User> users;
    private HashMap<Integer, WineStore> wines;
    private static int nextUserId = 0;
    private static int nextWineId = 0;
    //---

    private StateHandler() {
        this.users = new HashMap<Integer, User>();
        this.wines = new HashMap<Integer, WineStore>();
    }

    public static StateHandler getInstance() {  // Singleton
        if (instance == null) 
            instance = new StateHandler();
        return instance;
    }

    public void registerUser(String name) {
        User user = new User(nextUserId, name, STARTING_BALANCE);
        this.users.put(nextUserId, user);
        nextUserId++;
    }

    public void registerWine(String name, String image) {
        WineStore wine = new WineStore(nextWineId, name, image);
        this.wines.put(nextWineId, wine);
        nextWineId++;
    }

    public void addWineListingToUser(int userId, int wineId, int quantity, float price) {
        if (!wines.containsKey(wineId))
            throw new IllegalArgumentException("Wine does not exist");
        
        if (!users.containsKey(userId))
            throw new IllegalArgumentException("User does not exist");

        WineStore wine = wines.get(wineId);
        users.get(userId).addWineListing(new WineUser(wineId, wine.getName(), price, quantity));
    }

    public void buySellWine(int sellerID, int buyerID, int wineID, int quantity) {
        if (!wines.containsKey(wineID))
            throw new IllegalArgumentException("Wine does not exist");
        
        if (!users.containsKey(sellerID))
            throw new IllegalArgumentException("Seller does not exist");

        if (!users.containsKey(buyerID))
            throw new IllegalArgumentException("Buyer does not exist");

        WineUser wine = users.get(sellerID).getWine(wineID);
        if (wine == null)
            throw new IllegalArgumentException("Seller does not have this wine");

        if (wine.getQuantity() < quantity)
            throw new IllegalArgumentException("Seller does not have enough wine");

        User seller = users.get(sellerID);
        User buyer = users.get(buyerID);

        float price = wine.getPrice() * quantity;
        if (buyer.getBalance() < price)
            throw new IllegalArgumentException("Buyer does not have enough money");

        seller.setBalance(seller.getBalance() + price);
        buyer.setBalance(buyer.getBalance() - price);

        wine.setQuantity(wine.getQuantity() - quantity);
        if (wine.getQuantity() == 0)
            seller.removeWine(wineID);
    }

    public float getBalance(int userID){
        if (!users.containsKey(userID))
            throw new IllegalArgumentException("User does not exist");
        return users.get(userID).getBalance();
    }
}
