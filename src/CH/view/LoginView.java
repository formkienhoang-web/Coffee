package CH.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView extends JFrame {

    private final Color PRIMARY_COLOR = new Color(0, 120, 215);
    private final Color BACKGROUND_COLOR = new Color(240, 242, 245);
    private final Color PANEL_COLOR = Color.WHITE;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JButton btnLogin;

    public LoginView() {
        setTitle("Đăng nhập hệ thống");
        setSize(500, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(BACKGROUND_COLOR);
        add(container);

        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setPreferredSize(new Dimension(400, 460));
        card.setBackground(PANEL_COLOR);
        card.setBorder(new EmptyBorder(20, 25, 20, 25));
        container.add(card);

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ QUÁN DINOCOFFEE", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(PRIMARY_COLOR);
        card.add(lblTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 1, 10, 10));
        form.setBackground(PANEL_COLOR);

        txtUsername = createTextField("Tên đăng nhập");
        txtUsername.setText("admin");

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createTitledBorder("Mật khẩu"));

        chkShowPassword = new JCheckBox("Hiện mật khẩu");
        chkShowPassword.setBackground(PANEL_COLOR);
        chkShowPassword.addActionListener(e ->
                txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : '•')
        );

        form.add(txtUsername);
        form.add(txtPassword);
        form.add(chkShowPassword);
        card.add(form, BorderLayout.CENTER);

        // ===== NÚT ĐĂNG NHẬP =====
        btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setForeground(PRIMARY_COLOR);
        btnLogin.setBackground(PRIMARY_COLOR);
        btnLogin.setPreferredSize(new Dimension(150, 42));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnLogin.setContentAreaFilled(true);
        btnLogin.setOpaque(true);

        // Hover effect
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(new Color(30, 144, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(PRIMARY_COLOR);
            }
        });

        // --- XÓA DÒNG addActionListener Ở ĐÂY ---
        // btnLogin.addActionListener(e -> performLogin());  <-- XÓA

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(PANEL_COLOR);
        btnPanel.add(btnLogin);
        card.add(btnPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnLogin);
    }

    private JTextField createTextField(String title) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createTitledBorder(title));
        return field;
    }

    // --- THÊM CÁC HÀM GETTER ĐỂ CONTROLLER GỌI ---
    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JTextField getTxtUsername() {
        return txtUsername;
    }

    public JPasswordField getTxtPassword() {
        return txtPassword;
    }
}