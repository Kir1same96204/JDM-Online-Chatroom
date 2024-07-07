package View;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;

import javax.print.DocFlavor.URL;
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

    final String bgImgPath = "image/chatroombg.jpg";
    private ImageIcon backgroundImage = new ImageIcon(bgImgPath);  
    
    public MainFrm(User user, WebConnection connection, HashSet<ChatroomInfo> chatroom_info){
        super("大厅");
        this.user = user;
        webHandler = new LobbyWebHandlerThread(this, connection, user);
        //--------------set View---------

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500, 500, 1000, 800);
        setResizable(false);
        setLocationRelativeTo(null);

        Container container = getContentPane();

        // Container container = new JPanel();
        // Container layerPane = new JLayeredPane();
        // getContentPane().add(layerPane);
        // layerPane.add(new JPanel() {
        //     {
        //         // setOpaque(true);
        //         setLayout(new BorderLayout());
        //         System.out.println("bgp created");
        //     }

        //     @Override  
        //     protected void paintComponent(Graphics g) {  
        //         super.paintComponent(g); // 调用父类的paintComponent方法  
        //         // 绘制背景图片  
        //         if (backgroundImage != null) {  
        //             // System.out.println("bg ok");
        //             g.drawImage(backgroundImage.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);  
        //         }  else{
        //             System.err.println("no bg");
        //         }
        //     }  
        // }, new Integer(0));
        // layerPane.add(container, new Integer(1));
        // setContentPane(container);


        // title
        JLabel titleLabel = new JLabel("当前聊天室", SwingConstants.CENTER);
        titleLabel.setBackground(Color.BLUE);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 70));

        JPanel titlePanel = new JPanel();  
        titlePanel.add(titleLabel);
        titlePanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        titlePanel.setBackground(Color.GRAY);
  
        container.add(BorderLayout.NORTH, titlePanel);

        chatRoomDisplayPanel = new ChatRoomDisplayPanel(this, chatroom_info);
        container.add(chatRoomDisplayPanel, BorderLayout.CENTER);

        repaint();
        //----------------------------------
        webHandler.start();
    }

    public void createChatroom() {
        String name = JOptionPane.showInputDialog(this,"新建聊天室名称", "新建聊天室", JOptionPane.PLAIN_MESSAGE);
        if (name != null)
            webHandler.CreateChatRoom(name);
    }

    public void enterChatroom(String name) {
        webHandler.EnterChatRoom(name);
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

        setBackground(Color.PINK);

        repaintView();
        validate();
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
            remove(card);
        }

        all_cards.clear();

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

        revalidate();  
        repaint();
    }

    ChatRoomDisplayPanel(MainFrm mainFrm, HashSet<ChatroomInfo> chatroom_info) {
        this.mainFrm = mainFrm;
        this.setLayout(new GridLayout(3,5,20,10));

        for (ChatroomInfo info : chatroom_info) {
            ChatRoomCard chatRoomCard = new ChatRoomCard(mainFrm, info);
            cards.put(info.name, chatRoomCard);
            chatroom_names.add(info.name);
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
    Color originColor;

    Card(MainFrm mainFrm) {
        this.setBorder(new RoundBorder());
        this.setVisible(false);
        originColor = getBackground();

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                setBackground(Color.GRAY);
            }

            public void mouseExited(MouseEvent e) {
                setBackground(originColor);
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
        this.setVisible(true);

        setLayout(new GridBagLayout());  
  
        // 创建一个GridBagConstraints对象来指定组件的放置  
        GridBagConstraints c = new GridBagConstraints();  
  
        // 设置GridBagConstraints以在容器中水平和垂直居中  
        c.fill = GridBagConstraints.BOTH; // 扩展组件以填充可用空间  
        c.weightx = 0.5; // 水平方向上的权重  
        c.weighty = 0.5; // 垂直方向上的权重  
        c.gridx = 0; // 网格的x位置  
        c.gridy = 0; // 网格的y位置  
  
        // 创建一个用于显示第一行文字的JLabel  
        Font font = new Font("微软雅黑", Font.BOLD, 30);
        JLabel label1 = new JLabel(info.name, SwingConstants.CENTER);  
        label1.setFont(font);
        // 将JLabel添加到JPanel中，使用之前设置的GridBagConstraints  
        add(label1, c);  
  
        // 由于GridBagLayout默认不会为下一个组件自动创建新行，我们需要手动设置gridy  
        c.gridy = 1; // 将下一个组件放在下一行  
  
        // 创建一个用于显示第二行文字的JLabel  
        JLabel label2 = new JLabel("在线人数:" + info.user_num, SwingConstants.CENTER);  
        label2.setFont(font);
        // 将第二个JLabel添加到JPanel中  
        add(label2, c);  

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mainFrm.enterChatroom(info.name);
            }
        });
    }
}