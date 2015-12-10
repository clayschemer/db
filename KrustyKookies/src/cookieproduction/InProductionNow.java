package cookieproduction;

/**
 * CurrentUser represents the current user that has logged on to
 * the movie booking system. It is a singleton class.
 */
public class InProductionNow {
    /**
     * The single instance of this class
     */
    private static InProductionNow instance;
        
    /**
     * The name of the current user.
     */
    private String currentCookieName;
        
    /**
     * Create a CurrentUser object.
     */
    private InProductionNow() {
        currentCookieName = null;
    }
        
    /**
     * Returns the single instance of this class.
     *
     * @return The single instance of the class.
     */
    public static InProductionNow instance() {
        if (instance == null)
            instance = new InProductionNow();
        return instance;
    }
        
    /**
     * Check if a user has logged in.
     *
     * @return true if a user has logged in, false otherwise.
     */
    public boolean inProduction() {
        return currentCookieName != null;
    }
        
    /** 
     * Get the user id of the current user. Should only be called if
     * a user has logged in.
     *
     * @return The user id of the current user.
     */
    public String getCurrentCookieName() {
        return currentCookieName == null ? "<none>" : currentCookieName;
    }
        
    /**
     * A new user logs in.
     *
     * @param cookieName The user id of the new user.
     */
    public void produceCookie(String cookieName) {
        currentCookieName = cookieName;
    }
}
