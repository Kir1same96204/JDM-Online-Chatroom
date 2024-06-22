package View;
import javax.swing.*;

import Common.Web.*;
import Common.Web.Package.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LogInFrm extends JFrame{
    // private static WebUtil webUtil= new WebUtil();

    public LogInFrm(){
        super("登录界面");

        // JPanel panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        this.setLayout(layout);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500, 500, 500, 400);
        setResizable(false);

        JLabel title = new JLabel("JDM在线聊天室", new ImageIcon("image/jdm.png"), SwingConstants.LEADING);
        title.setFont(new Font("微软雅黑", Font.BOLD, 40));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 5;
        constraints.gridheight = 2;
        // constraints.ipady = 10;
        constraints.insets = new Insets(10,10,20,10);
        layout.setConstraints(title, constraints);

        JLabel idLabel = new JLabel("用户名：");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridheight = constraints.gridwidth = 1;
        constraints.insets = new Insets(10,10,10,10);
        layout.setConstraints(idLabel, constraints);

        JTextField id = new JTextField();
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridheight = 1;
        constraints.gridwidth = 3;
        layout.setConstraints(id, constraints);

        JLabel pwdLabel = new JLabel("密 码：");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridheight = constraints.gridwidth = 1;
        layout.setConstraints(pwdLabel, constraints);

        JPasswordField pwd = new JPasswordField();
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridheight = 1;
        constraints.gridwidth = 3;
        layout.setConstraints(pwd, constraints);

        JButton loginBtn = new JButton("登录");
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        layout.setConstraints(loginBtn, constraints);
        loginBtn.addActionListener(new ActionListener() {
            // login
            public void actionPerformed(ActionEvent event){
                WebConnection connection;
                try {
                    connection = new WebConnection();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "无法与服务器建立连接！");
                    return;
                }

                try{
                    connection.send(new LoginRequest(id.getText(), new String(pwd.getPassword())));
                    LoginResult result = (LoginResult)connection.receive();

                    if (result.success){
                        MainFrm mainFrm = new MainFrm(result.user, connection);
                        LogInFrm.this.dispose();
                        mainFrm.setVisible(true);
                        return;
                    }
                    else{
                        connection.close();
                        String error_info="";
                        switch (result.error_info){
                            case LoginResult.ERROR_TYPE.NO_USER:
                                error_info = "此用户不存在！";
                                break;
                            case LoginResult.ERROR_TYPE.WRONG_PASSWORD:
                                error_info = "密码错误！";
                                break;
                        }
                        JOptionPane.showMessageDialog(null, error_info);
                    }
                }catch(Exception e){

                }
            }
        });

        JButton signupBtn = new JButton("注册");
        constraints.gridx = 3;
        constraints.gridy = 4;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        layout.setConstraints(signupBtn, constraints);
        signupBtn.addActionListener(new ActionListener() {
            // signup
            public void actionPerformed(ActionEvent event){
                WebConnection connection;
                try {
                    connection = new WebConnection();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "无法与服务器建立连接！");
                    return;
                }

                try{
                    connection.send(new SignupRequest(id.getText(), new String(pwd.getPassword())));
                    SignupResult result = (SignupResult)connection.receive();

                    if (result.success){
                        MainFrm mainFrm = new MainFrm(result.user, connection);
                        LogInFrm.this.dispose();
                        mainFrm.setVisible(true);
                        return;
                    }
                    else{
                        connection.close();
                        String error_info="";
                        switch (result.error_info){
                            case SignupResult.ERROR_TYPE.USER_EXIST:
                                error_info = "用户已存在！";
                                break;
                        }

                        JOptionPane.showMessageDialog(null, error_info);
                    }
                }catch(Exception e){

                }
            }
        });


        JPanel block1 = new JPanel();
        constraints.gridx = 2;
        constraints.gridy = 4;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        layout.setConstraints(block1, constraints);
        add(block1);

        JPanel block2 = new JPanel();
        constraints.gridx = 4;
        constraints.gridy = 4;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        layout.setConstraints(block2, constraints);
        add(block2);

        this.add(title);
        this.add(idLabel);
        this.add(id);
        this.add(pwdLabel);
        this.add(pwd);
        this.add(loginBtn);
        this.add(signupBtn);
    }   


    public static void main(String[] args){
        LogInFrm frame = new LogInFrm();
        frame.setVisible(true);
    }
}
