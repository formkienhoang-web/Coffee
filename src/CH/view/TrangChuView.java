package CH.view;

import CH.model.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TrangChuView extends JPanel {
    // Các Label để hiển thị số liệu
    private JLabel lblTongDoanhThu;
    private JLabel lblSoHoaDon;
    private JLabel lblSoKhachHang;
    private JLabel lblSoMonAn;

    // Nút refresh
    private JButton btnLamMoi;

    // Greeting
    private JLabel lblGreeting;
    private JLabel lblDate;
    private JLabel lblRole;
    private JLabel lblMessage;

    // Màu sắc
    private final Color PRIMARY_COLOR = new Color(0, 91, 110);
    private final Color TEXT_COLOR = new Color(50, 50, 50);

    public TrangChuView() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 245, 249));

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 245, 249));
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel lblTitle = new JLabel("TỔNG QUAN CỬA HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY_COLOR);

        btnLamMoi = createRefreshButton("Làm mới dữ liệu");

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnLamMoi, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- THỐNG KÊ 4 CARD ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(new Color(240, 245, 249));
        statsPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        statsPanel.setPreferredSize(new Dimension(0, 150));

        lblTongDoanhThu = new JLabel("0 VNĐ");
        lblSoHoaDon = new JLabel("0");
        lblSoKhachHang = new JLabel("0");
        lblSoMonAn = new JLabel("0");

        statsPanel.add(createCard("DOANH THU", lblTongDoanhThu, new Color(255, 159, 67))); // cam
        statsPanel.add(createCard("HÓA ĐƠN", lblSoHoaDon, new Color(52, 152, 219)));      // xanh dương
        statsPanel.add(createCard("KHÁCH HÀNG", lblSoKhachHang, new Color(46, 204, 113))); // xanh lá
        statsPanel.add(createCard("MÓN ĂN", lblSoMonAn, new Color(155, 89, 182)));         // tím

        // --- GREETING CENTER PANEL ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(30, 20, 20, 20));

        lblGreeting = new JLabel("Chào Admin!");
        lblGreeting.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblGreeting.setForeground(PRIMARY_COLOR);
        lblGreeting.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblDate = new JLabel("Hôm nay là: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblDate.setForeground(TEXT_COLOR);
        lblDate.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblRole = new JLabel("Vai trò: ");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblRole.setForeground(TEXT_COLOR);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblMessage = new JLabel("Chúc bạn làm việc hiệu quả");
        lblMessage.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        lblMessage.setForeground(new Color(0, 120, 0));
        lblMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Thêm khoảng cách giữa các label
        centerPanel.add(statsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        centerPanel.add(lblGreeting);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(lblDate);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(lblRole);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(lblMessage);

        add(centerPanel, BorderLayout.CENTER);
    }

    // --- HÀM TẠO CARD THỐNG KÊ ---
    private JPanel createCard(String title, JLabel valueLabel, Color color) {
        JPanel content = new JPanel(new GridLayout(2, 1, 0, 5));
        content.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.GRAY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(TEXT_COLOR);

        content.add(lblTitle);
        content.add(valueLabel);

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(5, 0));

        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        card.add(colorBar, BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    // --- HÀM TẠO NÚT LÀM MỚI DỮ LIỆU ĐẸP ---
    private JButton createRefreshButton(String text) {
        JButton btn = new JButton(text){
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(53, 79, 78));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); // Chuẩn viên thuốc
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(160, 45));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 180, 160));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 150, 136));
                }
        });

            return btn;
    }

    // --- GETTERS ---
    public JLabel getLblTongDoanhThu() { return lblTongDoanhThu; }
    public JLabel getLblSoHoaDon() { return lblSoHoaDon; }
    public JLabel getLblSoKhachHang() { return lblSoKhachHang; }
    public JLabel getLblSoMonAn() { return lblSoMonAn; }
    public JButton getBtnLamMoi() { return btnLamMoi; }

    // --- CẬP NHẬT USER INFO ĐỘNG ---
    public void setUserInfo(String username, String role) {
        lblGreeting.setText(" Chào " + Session.tenNV + "!");
        lblRole.setText("Vai trò: " + role.toUpperCase());
    }

    public void refreshDate() {
        lblDate.setText("Hôm nay là: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}
