package me.vanjavk;

import me.vanjavk.dal.Repository;
import me.vanjavk.dal.RepositoryFactory;
import me.vanjavk.model.User;
import me.vanjavk.utils.MessageUtils;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Optional;

public class AuthPanel {
    private JPanel pnlAuth;
    private JTextField tfUsername;
    private JButton btnLogin;
    private JButton btnRegister;
    private JPasswordField pfPassword;
    private JLabel lbUsername;
    private JLabel lbPassword;
    private JLabel lbInfo;
    private final Main fMain;

    private Repository repository;
    private final Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder();
    private User user;

    public JPanel getPanel() {
        return pnlAuth;
    }

    public AuthPanel(Main mainInstance) {

        try {
            repository = RepositoryFactory.getRepository();

        } catch (Exception ex) {
            MessageUtils.showErrorMessage("Unrecoverable error", "Cannot initiate the form");
            System.exit(1);
        }

        fMain = mainInstance;

        btnLogin.addActionListener(e -> loginUser(tfUsername.getText().trim(), String.valueOf(pfPassword.getPassword())));
        btnRegister.addActionListener(e -> registerUser(tfUsername.getText().trim(), String.valueOf(pfPassword.getPassword())));
        tfUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginUser(tfUsername.getText().trim(), String.valueOf(pfPassword.getPassword()));
                }
            }
        });
        pfPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginUser(tfUsername.getText().trim(), String.valueOf(pfPassword.getPassword()));
                }
            }
        });
    }

    private void loginUser(String username, String password) {
        try {
            Optional<User> optUser = repository.selectUser(username);
            if (optUser.isPresent()) {
                user = optUser.get();
                System.out.println(user);
                if (argon2PasswordEncoder.matches(password, user.getPassword())) {
                    fMain.login(user);
                } else {
                    MessageUtils.showWarningMessage("Action failed", "Wrong password!");
                }
            } else {
                MessageUtils.showWarningMessage("Action failed", "User: \"" + username + "\" doesn't exist.");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            MessageUtils.showErrorMessage("Database error", "Unknown error.");
        }
    }

    private void registerUser(String username, String password) {
        if (username.length() < 5) {
            MessageUtils.showErrorMessage("Input error", "Username is too short! Please use 5 or more characters.");
            return;
        }
        if (password.length() < 8) {
            MessageUtils.showErrorMessage("Input error", "Password is too short! Please use 8 or more characters.");
            return;
        }
        try {
            repository.createUser(new User(username, argon2PasswordEncoder.encode(password)));
            MessageUtils.showInformationMessage("Action successful", "New user: \"" + username + "\" created successfully.");
        } catch (Exception exception) {
            if (exception instanceof SQLException) {
                MessageUtils.showWarningMessage("Action failed", exception.getMessage());
            } else {
                MessageUtils.showErrorMessage("Database error", "Unknown error.");
            }
        }
    }


}
