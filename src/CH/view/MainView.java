package CH.view;

import CH.controller.*;
import CH.model.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class MainView extends JFrame {

    // ======================
    // NEW COLOR THEME (GIỐNG ẢNH)
    // ======================
    private final Color SIDEBAR_COLOR = new Color(52, 87, 87);
    private final Color HOVER_COLOR   = new Color(72, 117, 117);
    private final Color ACTIVE_COLOR  = new Color(96, 140, 140);
    private final Color TEXT_COLOR    = Color.WHITE;
    private final Color LOGOUT_COLOR  = new Color(52, 87, 87);

    private CardLayout cardLayout;
    private JPanel pnlContent;
    private JLabel lblRole;
    private JLabel lblUser;

    private final Map<String, JButton> menuButtons = new HashMap<>();

    private TrangChuView trangChuView;
    private DatMonView datMonView;
    private ThucDonView thucDonView;
    private NhanVienView nhanVienView;
    private KhachHangView khachHangView;
    private HoaDonView hoaDonView;
    private KhoView khoView;
    private DoanhThuView doanhThuView;
    private DanhMucView danhMucView;

    public MainView() {
        setTitle("HỆ THỐNG QUẢN LÝ QUÁN DINOCOFFEE");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initHeader();
        initContent();
        initSidebar();

        cardLayout.show(pnlContent, "Trang chủ");
        updateActiveButton("Trang chủ");
    }

    // ======================
    // HEADER
    // ======================
    private void initHeader() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ QUÁN DINOCOFFEE");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(SIDEBAR_COLOR);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlRight.setBackground(Color.WHITE);

        lblUser = new JLabel("Xin chào, Vui lòng đăng nhập");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setForeground(Color.WHITE);

// nền giống sidebar
        btnLogout.setBackground(SIDEBAR_COLOR);

// bo góc + padding
        btnLogout.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

// QUAN TRỌNG: để màu hiển thị đúng
        btnLogout.setContentAreaFilled(true);
        btnLogout.setOpaque(true);
        btnLogout.setBorderPainted(false);

// hover nhẹ giống mẫu
        btnLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogout.setBackground(new Color(72, 117, 117)); // sáng hơn nhẹ
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnLogout.setBackground(SIDEBAR_COLOR);
            }
        });

        btnLogout.addActionListener(e -> handleLogout());

        pnlRight.add(lblUser);
        pnlRight.add(btnLogout);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlRight, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            java.awt.EventQueue.invokeLater(() -> {
                LoginView loginView = new LoginView();
                new LoginController(loginView);
                loginView.setVisible(true);
            });
        }
    }

    // ======================
    // CONTENT
    // ======================
    private void initContent() {
        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);

        trangChuView  = new TrangChuView();
        datMonView    = new DatMonView();
        thucDonView   = new ThucDonView();
        nhanVienView  = new NhanVienView();
        khachHangView = new KhachHangView();
        hoaDonView    = new HoaDonView();
//        khoView       = new KhoView();
        doanhThuView  = new DoanhThuView();
        danhMucView = new DanhMucView();
//        new DanhMucController(danhMucView);

        new TrangChuController(trangChuView);
        new DoanhThuController(doanhThuView);
        new NhanVienController(nhanVienView);

        HoaDonController hoaDonController = new HoaDonController(hoaDonView);
//        KhoController khoController = new KhoController(khoView);
        KhachHangController khachHangController = new KhachHangController(khachHangView);

        DatMonController datMonController = new DatMonController(datMonView, hoaDonController);
//        datMonController.setKhoController(khoController);
        datMonController.setKhachHangController(khachHangController);

        ThucDonController thucDonController = new ThucDonController(thucDonView, datMonController);
//        khoController.setThucDonController(thucDonController);

        DanhMucController danhMucController = new DanhMucController(danhMucView);
        danhMucController.setThucDonController(thucDonController);
        danhMucController.setDatMonController(datMonController);


        pnlContent.add(trangChuView,  "Trang chủ");
        pnlContent.add(datMonView,    "Đặt Món");
        pnlContent.add(thucDonView,   "Thực đơn");
        pnlContent.add(nhanVienView,  "Nhân viên");
        pnlContent.add(khachHangView, "Khách hàng");
        pnlContent.add(hoaDonView,    "Hóa đơn");
//        pnlContent.add(khoView,       "Kho");
        pnlContent.add(doanhThuView,  "Doanh thu");
        pnlContent.add(danhMucView, "Danh mục");

        add(pnlContent, BorderLayout.CENTER);
    }

    // ======================
    // SIDEBAR
    // ======================
    private void initSidebar() {
        JPanel pnlSidebar = new JPanel();
        pnlSidebar.setPreferredSize(new Dimension(230, 0));
        pnlSidebar.setBackground(new Color(111, 78, 55));
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));

        lblRole = new JLabel("ADMIN");
        lblRole.setForeground(Color.WHITE);
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlSidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        pnlSidebar.add(lblRole);
        pnlSidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        String[] menus = {
                "Trang chủ", "Đặt Món", "Thực đơn","Danh mục", "Nhân viên",
                "Khách hàng", "Hóa đơn", "Doanh thu", "Thoát"
        };

        for (String m : menus) {
            JButton btn = createMenuButton(m);
            menuButtons.put(m, btn);
            pnlSidebar.add(btn);
            pnlSidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        add(pnlSidebar, BorderLayout.WEST);
    }

    // ======================
    // MENU BUTTON
    // ======================
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(230, 45));
        btn.setBackground(SIDEBAR_COLOR);
        btn.setForeground(TEXT_COLOR);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 25, 0, 0));

        btn.setIcon(loadIcon(text));

        btn.addActionListener(e -> {
            if ("Thoát".equals(text)) {
                if (JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có muốn thoát chương trình không?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION
                ) == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            } else {
                cardLayout.show(pnlContent, text);
                updateActiveButton(text);
            }
        });

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!btn.getBackground().equals(new Color(111, 78, 55))) {
                    btn.setBackground(HOVER_COLOR);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (!btn.getBackground().equals(new Color(111, 78, 55))) {
                    btn.setBackground(SIDEBAR_COLOR);
                }
            }
        });

        return btn;
    }

    private ImageIcon loadIcon(String name) {
        String path = switch (name) {
            case "Trang chủ"  -> "/CH/icons/house.png";
            case "Đặt Món"    -> "/CH/icons/fast-food.png";
            case "Thực đơn"   -> "/CH/icons/menu.png";
            case "Nhân viên"  -> "/CH/icons/employee.png";
            case "Danh mục"   -> "/CH/icons/list.png";
            case "Khách hàng" -> "/CH/icons/rating.png";
            case "Hóa đơn"    -> "/CH/icons/invoice.png";
//            case "Kho"        -> "/CH/icons/warehouse.png";
            case "Doanh thu"  -> "/CH/icons/salary.png";
            case "Thoát"      -> "/CH/icons/exit.png";
            default -> null;
        };

        if (path == null) return null;

        try {
            Image img = new ImageIcon(getClass().getResource(path))
                    .getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    private void updateActiveButton(String active) {
        menuButtons.forEach((k, v) -> {
            if (k.equals(active)) {
                v.setBackground(new Color(111, 78, 55));
                v.setFont(new Font("Segoe UI", Font.BOLD, 15));
            } else {
                v.setBackground(new Color(111, 78, 55));
                v.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            }
            v.setForeground(TEXT_COLOR);
        });
    }

    public void setRole(String role) {
        lblRole.setText(role);
        if ("NHÂN VIÊN".equals(role)) {
            hideMenu("Nhân viên");
//            hideMenu("Kho");
            hideMenu("Doanh thu");
            hideMenu("Danh mục");
        }
        if (role != null) {
            lblUser.setText("Xin chào, " + Session.tenNV);
            trangChuView.setUserInfo(role, role);
        }
    }

    private void hideMenu(String name) {
        JButton btn = menuButtons.get(name);
        if (btn != null) btn.setVisible(false);
    }
}