package View;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Common.*;
import Common.Web.*;
import View.Utils.RoundBorder;
import WebHandler.LobbyWebHandlerThread;

public class MainFrm extends JFrame{
    final static String ADD_CARD_NAME = "add";
    @SuppressWarnings("unused")
    private User user = null;
    LobbyWebHandlerThread webHandler;
    private Container container;
    ChatRoomDisplayPanel chatRoomDisplayPanel;
    
    public MainFrm(User user, WebConnection connection, HashSet<ChatroomInfo> chatroom_info){
        super("大厅");
        this.user = user;
        webHandler = new LobbyWebHandlerThread(this, connection, user);
        //--------------set View---------

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

        chatRoomDisplayPanel = new ChatRoomDisplayPanel(this, chatroom_info);
        container.add(chatRoomDisplayPanel, BorderLayout.CENTER);

        //----------------------------------
        
    }

    public void createChatroom() {
        String name = JOptionPane.showInputDialog(this,"新建聊天室名称", "新建聊天室", JOptionPane.PLAIN_MESSAGE);
        if (name != null)
            webHandler.CreateChatRoom(name);
    }

    public void InformNewChatroom(ChatroomInfo info) {
        chatRoomDisplayPanel.newCard(info);
    }

    public void InformDelChatroom(String name) {
        chatRoomDisplayPanel.delCard(name);
    }
}



class ChatRoomDisplayPanel extends JPanel{
    private final static int MAX_CARD_NUM = 15;

    ArrayList<String> chatroom_names = new ArrayList<String>();
    HashMap<String, Card> cards = new HashMap<String, Card>();
    HashSet<Card> all_cards = new HashSet<Card>();

    private MainFrm mainFrm;

    /**
     * only for create new Chatroom display card
     * @param name
     */
    public synchronized void newCard(ChatroomInfo info) {
        ChatRoomCard card = new ChatRoomCard(this.mainFrm, info);
        chatroom_names.add(chatroom_names.size()-1, info.name);
        cards.put(info.name, card);

        repaintView();
    }

    public synchronized void delCard(String name) {
        for (int i=0; i<chatroom_names.size(); i++) {
            if (chatroom_names.get(i).equals(name)) {
                chatroom_names.remove(i);
            }
        }

        cards.remove(name);
        repaintView();
    }

    /**
     * repaint the whole card display view based on current cards
     */
    private synchronized void repaintView() {
        // first remove all the cards including blank card
        for (Card card : all_cards) {
            mainFrm.remove(card);
        }

        for (String card_name : chatroom_names) {
            Card card = cards.get(card_name);
            all_cards.add(card);
            add(card);
        }

        while (all_cards.size() < MAX_CARD_NUM) {
            Card blankCard = new Card(mainFrm);
            all_cards.add(blankCard);
            add(blankCard);
        }
    }

    ChatRoomDisplayPanel(MainFrm mainFrm, HashSet<ChatroomInfo> chatroom_info) {
        this.mainFrm = mainFrm;
        this.setLayout(new GridLayout(3,5,20,10));

        for (ChatroomInfo info : chatroom_info) {
            ChatRoomCard chatRoomCard = new ChatRoomCard(mainFrm, info);
            cards.put(info.name, chatRoomCard);
        }

        chatroom_names.add(MainFrm.ADD_CARD_NAME);
        CreateChatRoomCard createChatRoom = new CreateChatRoomCard(mainFrm);
        cards.put(MainFrm.ADD_CARD_NAME, createChatRoom);

        repaintView();
    }

    // void add(String name, )
}

/**
 * Default to be transparent
 */
class Card extends JPanel{
    protected final static String FONT_NAME = "微软雅黑";
    MainFrm mainFrm;

    Card(MainFrm mainFrm) {
        this.setBorder(new RoundBorder());
        this.setVisible(false);

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });
    }
}

class CreateChatRoomCard extends Card {
    CreateChatRoomCard(MainFrm mainFrm) {
        super(mainFrm);

        setLayout(new BorderLayout());
        setVisible(true);
        JLabel label = new JLabel("创建新聊天室", SwingConstants.CENTER);
        label.setFont(new Font(FONT_NAME, Font.BOLD, 25));
        add(label, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            public synchronized void mousePressed(MouseEvent e) {
                mainFrm.createChatroom();
            }
        });
    }
}

class ChatRoomCard extends Card {
    ChatRoomCard(MainFrm mainFrm, ChatroomInfo info) {
        super(mainFrm);
    }
}