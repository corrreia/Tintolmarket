package handlers;

import java.util.HashMap;

import objects.User;
import objects.WineStore;
import objects.WineUser;

public class StateHandler {
    private static StateHandler instance = null;
    private static final String userJson = "users.json";
    private static final String wineJson = "wines.json";

    private HashMap<Integer, User> users;
    private HashMap<Integer, WineStore> wines;

    private StateHandler() {
        this.users = new HashMap<Integer, User>();
        this.wines = new HashMap<Integer, WineStore>();
    }

    public static StateHandler getInstance() {  // Singleton
        if (instance == null) {
            instance = new StateHandler();
        }
        return instance;
    }

    //WINES:
    // id, name, evaluation, image 

    //USERS:
    // id , name, wines (id of wines, price, quantity), inbox (for messages)

    public void addUser(User user) {
        this.users.put(user.getId(), user);
    }
    
    public void addWine(WineStore wine) {
        this.wines.put(wine.getId(), wine);
    }

    public void addWineToUser(WineUser wine, int userId) {
        this.users.get(userId).addWine(wine);
    }
    
    public void removeUser(int userId) {
        this.users.remove(userId);
    }

    public void removeWine(int wineId) {
        this.wines.remove(wineId);
    }

    public void removeWineFromUser(int wineId, int userId) {
        this.users.get(userId).removeWine(wineId);
    }
}
