package Common.Web;

import java.io.IOException;

public class LobbyWebHandler{
    WebConnection connection = null;

    public LobbyWebHandler() throws IOException{
        if (connection == null){
            connection = new WebConnection();
        }
        

    }


    /**
     * Create a Lobby page with a existing connection
     * @param webConnection
     */
    public LobbyWebHandler(WebConnection webConnection){
        connection = webConnection;
    }
}
