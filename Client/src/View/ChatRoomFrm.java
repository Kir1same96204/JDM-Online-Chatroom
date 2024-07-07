package View;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;

import Common.ChatroomInfo;
import Common.User;
import Common.Message.*;
import Common.Web.*;
import View.MessageArea.WrapColumnFactory;
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
        setSize(700, 800);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        messageArea = new MessageArea(this);

        JScrollPane scrollPane = new JScrollPane(messageArea) {
            public  void paintComponent (Graphics   g) {  

                setOpaque(false);
                Image image = new ImageIcon("image/chatroombg.jpg").getImage(); 
                g.drawImage(image,0,0,getWidth(), getHeight() ,this);  
                super.paintComponent(g);  
            }
        };
        contentPanel.add(scrollPane);

        inputArea = new InputArea(this);
        contentPanel.add(inputArea, BorderLayout.SOUTH);

        add(contentPanel);
        //------------------------
        // first load message history from disk
        loadMessageHistoryFromDisk();

        webHandler.start();
        webHandler.syncWithServer();
    }

    static class WrapEditorKit extends StyledEditorKit {
        ViewFactory defaultFactory = new WrapColumnFactory();

        @Override
        public ViewFactory getViewFactory() {
            return defaultFactory;
        }
    }

    public void syncWithServer() {
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

        validate();
        try {
            File dir = new File(msgSavePath).getParentFile();

            if (!dir.exists()) {
                dir.mkdirs();
            }

            FileOutputStream fos=new FileOutputStream(msgSavePath);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            MessageHistory mh = new MessageHistory();
            mh.messages = messages;
            oos.writeObject(mh);
            oos.close();
        } catch (Exception e) {

        }
    }

    public void send(Message m) {
        webHandler.sendMessage(m);
    }
}


class MessageArea extends JTextPane {
    ChatRoomFrm chatRoomFrm;
    private static StyledDocument doc;


    
    public MessageArea(ChatRoomFrm chatRoomFrm) {
        this.chatRoomFrm = chatRoomFrm;
        setBackground(Color.ORANGE); 

        // setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // add(new JPanel());
        setEditorKit(new WrapEditorKit());
        doc = getStyledDocument();
        setEditable(false);
        setText("");
    }

    static void appendText(String text) {
        try {
            doc.insertString(doc.getLength(), text + "\n", null);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(text);
        }
    }

    // 0 left 1 mid 2 right
    static void appendText(MessageArea ma,String text, int Align) {
        // if (Align == 0) {
            text += "\n";
        // }
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setAlignment(set, Align);
        doc.setParagraphAttributes(ma.getText().length(), doc.getLength()-ma.getText().length(), set, false);
        try {
            doc.insertString(doc.getLength(), text, set);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class WrapEditorKit extends StyledEditorKit {
        javax.swing.text.ViewFactory defaultFactory = new WrapColumnFactory();

        @Override
        public javax.swing.text.ViewFactory getViewFactory() {
            return defaultFactory;
        }
    }

    static class WrapColumnFactory implements javax.swing.text.ViewFactory {
        @Override
        public View create(javax.swing.text.Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                if (kind.equals(AbstractDocument.ContentElementName)) {
                    return new WrapLabelView(elem);
                } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                    return new ParagraphView(elem);
                } else if (kind.equals(AbstractDocument.SectionElementName)) {
                    return new BoxView(elem, View.Y_AXIS);
                } else if (kind.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                } else if (kind.equals(StyleConstants.IconElementName)) {
                    return new IconView(elem);
                }
            }

            return new LabelView(elem);
        }
    }

    static class WrapLabelView extends LabelView {
        public WrapLabelView(javax.swing.text.Element elem) {
            super(elem);
        }

        @Override
        public float getMinimumSpan(int axis) {
            switch (axis) {
                case View.X_AXIS:
                    return 0;
                case View.Y_AXIS:
                    return super.getMinimumSpan(axis);
                default:
                    throw new IllegalArgumentException("Invalid axis: " + axis);
            }
        }
    }

    public void addMessage(Message m) {
        int type;
        if (m.sender.equals(chatRoomFrm.user.name)) {
            type = MessageDisplayPane.RIGHT;
        }
        else {
            type = MessageDisplayPane.LEFT;
        }

        // MessageDisplayPane pane = new MessageDisplayPane(m, type);
        // Dimension d = getSize();
        // d.height = (int)d.getHeight() + pane.getHeight();
        // setSize(d);
        // add(pane);
        // validate();

        addMessage(m, type);
    }

    void addMessage(Message m, int type) {
        final int LEFT = 0;
        final int RIGHT = 2;

        appendText("");
        
        appendText(this,
            m.send_time.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        ),1);
        
        appendText(this, m.sender+" 说：", type);

        if (m instanceof TextMessage) {
            TextMessage tm = (TextMessage)m;
            appendText(tm.text);
        }

        else if (m instanceof PicMessage) {
            PicMessage pm = (PicMessage)m;

            displayimage(pm.fileBytes, pm.fileLen);
        }
        
        appendText("");
    }

    // util
    void displayimage(byte imagebytes[], int size) {
        byte imageBytes[] = new byte[size];
        System.arraycopy(imagebytes, 0, imageBytes, 0, size);
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            ImageIcon imageIcon = new ImageIcon(image);
            if (imageIcon != null) {
                int width = 200;
                float w = imageIcon.getIconWidth(), h = imageIcon.getIconHeight();
                float a = width * h / w;
                int aa = (int) a;
                Image imagee = imageIcon.getImage().getScaledInstance(width, aa, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(imagee);

                javax.swing.text.Style style = addStyle("iconStyle", null);
                StyleConstants.setIcon(style, scaledIcon);
                try {
                    doc.insertString(doc.getLength(), " ", style);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("图片读取失败！");
        }
    }
}

class MessageDisplayPane extends JPanel {
    final public static int LEFT = 0;
    final public static int RIGHT = 2;

    MessageDisplayPane(Message m, int type) {
        setPreferredSize(new Dimension(88,26));
        setMinimumSize(new Dimension(100 ,230));
        System.out.println(getPreferredSize());
        setLayout(new BorderLayout());
        JLabel timeLabel = new JLabel(m.send_time.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        ));
        if (type == RIGHT)
            timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(timeLabel, BorderLayout.NORTH);
        
        JLabel nameLabel = new JLabel(m.sender);
        if (type == RIGHT)
            nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(nameLabel,BorderLayout.CENTER);

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

        f.add(contentPanel, BorderLayout.SOUTH);
    }   
}

class FunctionalArea extends JPanel {
    ChatRoomFrm chatRoomFrm;

    public FunctionalArea(ChatRoomFrm frm) {
        chatRoomFrm = frm;
    
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JButton picButton = new JButton("图片");
        picButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                
                int option = fileChooser.showOpenDialog(chatRoomFrm);
                if(option == JFileChooser.APPROVE_OPTION){
                    File file = fileChooser.getSelectedFile();
                    
                    try {
                        PicMessage m = new PicMessage();
                        FileInputStream fis = new FileInputStream(file);
                        byte[] bs = new byte[(int)file.length()];
                        fis.read(bs);
                        m.fileBytes = bs;
                        m.fileLen = (int)file.length();
                        m.sender = chatRoomFrm.user.name;

                        chatRoomFrm.send(m);
                        fis.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        
        add(picButton);
        add(Box.createHorizontalStrut(5));

        JButton syncButton = new JButton("同步");
        syncButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chatRoomFrm.syncWithServer();
            }
        });
        
        add(syncButton);
        add(Box.createHorizontalStrut(5));
    }
}

class InputArea extends JPanel {
    ChatRoomFrm chatRoomFrm;

    public InputArea(ChatRoomFrm frm) {
        chatRoomFrm = frm;

        setLayout(new BorderLayout(0, 0));
        setBackground(Color.BLUE);
        
        FunctionalArea functionalArea = new FunctionalArea(frm);
        add(functionalArea, BorderLayout.NORTH);
        JTextArea textArea = new JTextArea(8, 40);
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 15)); 
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane textPane = new JScrollPane(textArea);
        textPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 

        add(textPane, BorderLayout.CENTER);

        JButton send = new JButton("发送");
        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = textArea.getText();
                TextMessage m = new TextMessage();
                m.text = text;
                m.sender = chatRoomFrm.user.name;
                
                chatRoomFrm.send(m);
                textArea.setText(new String());
            }
        });
        add(send, BorderLayout.EAST);
    }
}