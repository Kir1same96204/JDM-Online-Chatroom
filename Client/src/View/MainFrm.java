package View;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import Common.User;
import Common.Web.*;

public class MainFrm extends JFrame{
    private User user = null;
    private LobbyWebHandler webHandler = null;
    private Container container;
    
    public MainFrm(User user, WebConnection connection){
        super("大厅");
        this.user = user;
        this.webHandler = new LobbyWebHandler(connection);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500, 500, 1000, 800);
        setResizable(false);

        container = getContentPane();
        setLayout(new BorderLayout());

        // title
        JLabel titleLabel = new JLabel("当前聊天室", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 70));

        JPanel titlePanel = new JPanel();  
        titlePanel.add(titleLabel);
        titlePanel.setBorder(new EmptyBorder(20, 0, 20, 0));
  
        container.add(BorderLayout.NORTH, titlePanel);

        ChatRoomDisplayPanel chatRoomDisplayPanel = new ChatRoomDisplayPanel();
        container.add(chatRoomDisplayPanel, BorderLayout.CENTER);
    }
}


class ChatRoomDisplayPanel extends JPanel{
    private final static String ADD_CARD_NAME = "add";

    HashMap<String, Card> cards = new HashMap<String, Card>();

    ChatRoomDisplayPanel(){
        super(new GridLayout(3,5,20,10));
        cards.put(ADD_CARD_NAME, new CreateChatRoom());
    }

    // void add(String name, )
}

class Card extends JPanel{

}

class CreateChatRoom extends Card{

}