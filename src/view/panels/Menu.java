package view.panels;

import controller.Controller;
import view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends Viewer {

    private JTextField ip;
    private JButton serverBtn;
    private JButton connectBtn;
    private JButton simpleConnect;
    private Controller controller;

    public Menu(String title, int width, int height, Controller controller) {
        super(title, width, height);
        this.controller = controller;
        add(content());
    }

    @Override
    public JPanel content() {
        Action action = new Action(this, controller);
        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        JPanel inner = new JPanel();
        inner.setLayout(new GridLayout(4,1,0,0));

        inner.setPreferredSize(new Dimension(150,100));

        ip = new JTextField();
        ip.setText("localhost");
        ip.setHorizontalAlignment(JTextField.CENTER);

        inner.add(ip);
        connectBtn = new JButton("Connect");
        connectBtn.addActionListener(action);
        inner.add(connectBtn);

        serverBtn = new JButton("Run Server");
        serverBtn.addActionListener(action);
        inner.add(serverBtn);
        panel.add(inner, BorderLayout.SOUTH);
        return panel;
    }

    public String getIP() {
        return ip.getText();
    }

    public class Action implements ActionListener {

        private Menu menu;
        private Controller controller;

        public Action(Menu menu, Controller controller) {
            this.menu = menu;
            this.controller = controller;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(serverBtn)) {
                if (controller.serverStarts()) {
                    Viewer server = new ServerLog("Server Log", 500, 400, controller);
                    server.location(0, 300);
                    server.show();
                }else{
                    JOptionPane.showMessageDialog(null, "Couldn't start server, might already be running.");
                }
            }
            if (e.getSource().equals(connectBtn)) {
                if (controller.connect(ip.getText())) {
                    Viewer cl = new Login(menu,"Client Log", 700, 500);
                    int x = (Toolkit.getDefaultToolkit().getScreenSize().width-cl.getWidth());
                    cl.setLocation(x, 300);
                    cl.show();
                } else {
                    JOptionPane.showMessageDialog(null, "Connection to " + ip.getText() + " failed.");
                }
            }
        }
    }
}
