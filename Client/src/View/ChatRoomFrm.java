package View;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

import Common.ChatroomInfo;
import Common.User;
import Common.Message.*;
import Common.Web.WebConnection;
import View.Utils.RoundBorder;
import WebHandler.ChatRoomWebHandlerThread;

public class ChatRoomFrm extends JFrame{
    MessageArea messageArea;
    FunctionalArea functionalArea;
    InputArea inputArea;

    String msgSavePath;

    ArrayList<Message> messages = new ArrayList<Message>();

    ChatRoomWebHandlerThread webHandler;
    User user;

    public ChatRoomFrm(WebConnection connection, User user, ChatroomInfo info) {
        super("聊天：" + info.name);

        this.user = user;
        webHandler = new ChatRoomWebHandlerThread(this, connection);
        msgSavePath = "msg/" + info.name;
        //---------------------

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500, 500, 1000, 800);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        messageArea = new MessageArea(this);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        contentPanel.add(scrollPane);

        functionalArea = new FunctionalArea(this);
        contentPanel.add(functionalArea);

        inputArea = new InputArea(this);
        contentPanel.add(inputArea);

        add(contentPanel);
        //------------------------
        // first load message history from disk
        loadMessageHistoryFromDisk();

        webHandler.start();
        webHandler.syncWithServer();
    }

    public void loadMessageHistoryFromDisk() {
        File f = new File(msgSavePath);

        if (!f.exists()) {
            // no message history
            return;
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            MessageHistory history = (MessageHistory)ois.readObject();
            receiveMessageHistory(history);
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveMessageHistory(MessageHistory history) {
        if (this.messages.size() < history.messages.size()) {
            for (int i=this.messages.size(); i<history.messages.size(); i++) {
                receiveMessage(history.messages.get(i));
            }
        }
    }

    public void receiveMessage(Message message) {
        messages.add(message);
        messageArea.addMessage(message);
    }
}

class MessageArea extends JPanel {
    ChatRoomFrm chatRoomFrm;

    public MessageArea(ChatRoomFrm chatRoomFrm) {
        this.chatRoomFrm = chatRoomFrm;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void addMessage(Message m) {
        int type;
        if (m.sender.equals(chatRoomFrm.user.name)) {
            type = MessageDisplayPane.RIGHT;
        }
        else {
            type = MessageDisplayPane.LEFT;
        }

        add(new MessageDisplayPane(m, type));
    }
}

class MessageDisplayPane extends JPanel {
    final public static int LEFT = 0;
    final public static int RIGHT = 1;

    MessageDisplayPane(Message m, int type) {
        JLabel timeLabel = new JLabel(m.send_time.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        ));
        if (type == RIGHT)
            timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(timeLabel, BorderLayout.NORTH);
        
        JLabel nameLabel = new JLabel(m.sender);
        if (type == RIGHT)
            nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(nameLabel);

        if (m instanceof TextMessage) {
            TextMessage tm = (TextMessage)m;
            JLabel textLabel = new JLabel(tm.text);
            textLabel.setBorder(new RoundBorder());

            placeMessage(this, textLabel, type);
        }

        else if (m instanceof PicMessage) {
            PicMessage pm = (PicMessage)m;

            BufferedImage image = null;
            try {
                image = ImageIO.read(new ByteArrayInputStream(pm.fileBytes));
            } catch (Exception e) {

            }   

            JLabel imgLabel = new JLabel(new ImageIcon(image));
            placeMessage(this, imgLabel, type);
        }
    }

    private void placeMessage(JPanel f, Component s, int type) {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new RoundBorder());
        GridBagConstraints c = new GridBagConstraints();

        // 设定GridBagConstraints  
        Component leftComponent;
        Component rightComponent;

        if (type == LEFT) {
            leftComponent = s;
            rightComponent = new JPanel();
        }
        else {
            leftComponent = new JPanel();
            rightComponent = s;
        }

        c.gridx = 0; // 第一列  
        c.gridy = 0; // 第一行  
        c.fill = GridBagConstraints.BOTH; // 水平和垂直方向都填充  
        c.weightx = 0.67; // 占据容器的三分之一宽度  
        c.weighty = 1.0; // 占据容器的全部高度  
        contentPanel.add(leftComponent, c);

        c.gridx = 1; // 第二列  
        c.weightx = 0.33; // 占据剩余的三分之二宽度  
        contentPanel.add(rightComponent, c);

        f.add(contentPanel);
    }   
}

class FunctionalArea extends JPanel {
    ChatRoomFrm ChatRoomFrm;

    public FunctionalArea(ChatRoomFrm frm) {
        ChatRoomFrm = frm;

        setLayout(new FlowLayout());
    }
}

class InputArea extends JPanel {
    ChatRoomFrm chatRoomFrm;

    public InputArea(ChatRoomFrm frm) {
        chatRoomFrm = frm;
    }
}