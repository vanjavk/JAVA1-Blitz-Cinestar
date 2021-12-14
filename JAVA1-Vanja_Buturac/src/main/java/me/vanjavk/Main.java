package me.vanjavk;

import me.vanjavk.model.User;

import javax.swing.*;

public class Main extends JFrame{
    private JTabbedPane tpContent;
    private JPanel pnlMain;
    private JMenuBar mbMenu;
    private JMenu mnAuth;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 750;

    public Main(String title)  {
        super(title);

        handleMenu();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentPane(pnlMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        tpContent.setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        init();
        setVisible(true);
    }

    private void handleMenu() {
        mbMenu = new javax.swing.JMenuBar();
        setJMenuBar(mbMenu);
        mnAuth = new javax.swing.JMenu();

        mnAuth.setText("Authentication");
        mbMenu.add(mnAuth);

        JMenuItem miLogout = new JMenuItem();
        miLogout.addActionListener(e ->logOut());
        miLogout.setText("Log out");
        mnAuth.add(miLogout);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main("Main"));
    }
    private void init(){
        tpContent.removeAll();
        tpContent.add("Login", new AuthPanel(this).getPanel());
    }
    public void logOut(){
        init();
    }
    public void login(User user){
        switch(user.getRole()){
            case 0 -> loadUser();
            case 1 -> loadAdmin();
        }
    }
    public void loadUser(){
        tpContent.removeAll();
        tpContent.add("Edit Movie panel", new EditMoviePanel().getPanel());
        tpContent.add("Edit Director panel", new EditDirectorPanel().getPanel());
        tpContent.add("Edit Actor panel",new EditActorPanel().getPanel());
    }
    public void loadAdmin(){
        tpContent.removeAll();
        tpContent.add("Import panel", new ImportPanel().getPanel());
        tpContent.add("Export panel", new ExportPanel().getPanel());

    }

}
