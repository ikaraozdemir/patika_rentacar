package view;

import business.UserManager;
import core.Helper;
import entity.User;

import javax.swing.*;
import java.awt.*;

public class LoginView extends Layout {
    private JPanel container;
    private JPanel w_top;
    private JLabel lbl_welcome;
    private JLabel lbl_welcome2;
    private JPanel w_bottom;
    private JTextField fld_username;
    private JPasswordField fld_pass;
    private JButton btn_login;
    private JLabel lbl_username;
    private JLabel lbl_pass;
    private final UserManager userManager;

    public LoginView() {
        this.userManager = new UserManager();
        this.add(container);
        this.guiInitialize(400, 400);
        btn_login.addActionListener(e -> {
            //Alanların boş olup olmadığını kontrol etme
            JTextField[] checkFieldLsit = {this.fld_username, this.fld_pass};
            if (Helper.isFieldListEmpty(checkFieldLsit)) {
                Helper.showMsg("fill");
            } else {
                // Eğer boş değilse veritabanında bu entry'nin olup olmadığını kontrol eder.
                User loginUser = this.userManager.findByLogin(this.fld_username.getText(), this.fld_pass.getText());
                if (loginUser == null) {
                    Helper.showMsg("notFound");
                } else {
                    AdminView adminView = new AdminView(loginUser);
                    dispose();
                }
            }
        });
    }
}
