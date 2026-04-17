package CH.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DatMonView extends JPanel {

    // --- CẤU TRÚC GIỎ HÀNG MỚI (THAY THẾ JTABLE) ---
    private JTable tableGioHang;
    private JPanel pnlCartList;
    private List<Object[]> listGioHang = new ArrayList<>(); // [Tên, Size, SL, Đơn giá, Hình ảnh, Giá gốc]

    private JButton btnXoaMon, btnThanhToan;
    private JLabel lblTongTien;
    private JPanel pnlMenuGrid;

    private JComboBox<String> cbDanhMuc;
    private JTextField txtSearch;

    // 🎨 COFFEE THEME
    private final Color coffeeDark = new Color(111, 78, 55);
    private final Color coffee = new Color(141, 110, 99);
    private final Color coffeeLight = new Color(215, 204, 200);
    private final Color coffeeBg = new Color(239, 235, 233);

    public DatMonView() {
        setLayout(new GridLayout(1, 2, 10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // ================= TRÁI: THỰC ĐƠN (GIỮ NGUYÊN) =================
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setBackground(coffeeBg);
        pnlLeft.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(coffee),
                "THỰC ĐƠN",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                coffeeDark
        ));

        JPanel pnlTop = new JPanel(new BorderLayout(5, 5));
        pnlTop.setBackground(coffeeBg);
        cbDanhMuc = new JComboBox<>(new String[]{"Danh mục", "Tất cả"});
        cbDanhMuc.setPreferredSize(new Dimension(160, 35));
        txtSearch = new JTextField();
        txtSearch.setBorder(BorderFactory.createTitledBorder("Tìm món..."));
        pnlTop.add(cbDanhMuc, BorderLayout.WEST);
        pnlTop.add(txtSearch, BorderLayout.CENTER);
        pnlLeft.add(pnlTop, BorderLayout.NORTH);

        pnlMenuGrid = new JPanel(new GridLayout(0, 3, 8, 8));
        pnlMenuGrid.setBackground(coffeeBg);
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        wrapper.setBackground(coffeeBg);
        wrapper.add(pnlMenuGrid);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        scroll.setBorder(null);
        pnlLeft.add(scroll, BorderLayout.CENTER);

        // ================= PHẢI: GIỎ HÀNG (SỬA LẠI THEO MẪU MỚI) =================
        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setBackground(Color.WHITE);
        pnlRight.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(coffee),
                "GIỎ HÀNG",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                coffeeDark
        ));


        // Container chứa danh sách các món trong giỏ
        pnlCartList = new JPanel();
        pnlCartList.setLayout(new BoxLayout(pnlCartList, BoxLayout.Y_AXIS));
        pnlCartList.setBackground(Color.WHITE);

        JScrollPane scrollCart = new JScrollPane(pnlCartList);
        scrollCart.setBorder(null);
        scrollCart.getVerticalScrollBar().setUnitIncrement(15);
        pnlRight.add(scrollCart, BorderLayout.CENTER);

        // Footer giỏ hàng
        JPanel pnlFooter = new JPanel(new GridLayout(2, 1));
        pnlFooter.setBackground(coffeeBg);

        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ", SwingConstants.RIGHT);
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongTien.setForeground(new Color(60,60,60));
        lblTongTien.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBtns.setBackground(coffeeBg);
        btnXoaMon = new JButton("XOÁ HẾT");
        btnXoaMon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnXoaMon.setForeground(new Color(60,60,60));
        btnXoaMon.setBackground(coffeeLight);

        btnThanhToan = new JButton("THANH TOÁN");
        btnThanhToan.setForeground(new Color(60,60,60));
        btnThanhToan.setBackground(coffeeLight);
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 12));

        pnlBtns.add(btnXoaMon);
        pnlBtns.add(btnThanhToan);

        pnlFooter.add(lblTongTien);
        pnlFooter.add(pnlBtns);

        pnlRight.add(pnlFooter, BorderLayout.SOUTH);

        add(pnlLeft);
        add(pnlRight);

    }

    // 🔥 HÀM RENDER GIỎ HÀNG (VẼ LẠI KHI CÓ THAY ĐỔI)
    private void renderCart() {
        pnlCartList.removeAll();
        double tong = 0;

        for (int i = 0; i < listGioHang.size(); i++) {
            Object[] item = listGioHang.get(i);
            pnlCartList.add(createCartItemUI(item, i));
            tong += (int)item[2] * (double)item[3];
        }

        setTongTien(tong);
        pnlCartList.revalidate();
        pnlCartList.repaint();
    }

    // 🔥 TẠO GIAO DIỆN TỪNG DÒNG TRONG GIỎ
    private JPanel createCartItemUI(Object[] item, int index) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setMaximumSize(new Dimension(500, 100));
        row.setBackground(Color.WHITE);
        row.setBorder(new MatteBorder(0, 0, 1, 0, coffeeBg));

        // 1. Ảnh món ăn (Giữ nguyên)
        JLabel lblImg = new JLabel();
        String path = (String) item[4];
        ImageIcon icon = (path != null && !path.isEmpty()) ? new ImageIcon(path) : new ImageIcon("src/images/default.png");
        Image img = icon.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
        lblImg.setIcon(new ImageIcon(img));
        lblImg.setBorder(new EmptyBorder(5, 5, 5, 5));

        // 2. Thông tin (Tên + Size + Giá)
        JPanel pnlInfo = new JPanel(new GridLayout(3, 1, 0, 0));
        pnlInfo.setOpaque(false);

        JLabel lblName = new JLabel(item[0].toString());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // --- CHỈNH SIZE COMBOBOX GỌN LẠI ---
        JPanel pnlSizeWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlSizeWrapper.setOpaque(false);

        JComboBox<String> cbSize = new JComboBox<>(new String[]{"S", "M", "L"});
        cbSize.setSelectedItem(item[1].toString());

        // Ép kích thước nhỏ lại ở đây
        cbSize.setPreferredSize(new Dimension(40, 22));
        cbSize.setFont(new Font("Segoe UI", Font.BOLD, 11));

        cbSize.addActionListener(e -> {
            String newSize = cbSize.getSelectedItem().toString();
            double giaGoc = (double) item[5];
            double newGia = switch (newSize) {
                case "M" -> giaGoc + 10000;
                case "L" -> giaGoc + 20000;
                default -> giaGoc;
            };
            item[1] = newSize;
            item[3] = newGia;
            renderCart();
        });
        pnlSizeWrapper.add(cbSize);

        JLabel lblPrice = new JLabel(String.format("%,.0f VNĐ", (double)item[3]));
        lblPrice.setForeground(new Color(60,60,60));
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 12));

        pnlInfo.add(lblName);
        pnlInfo.add(pnlSizeWrapper); // Cho vào wrapper để không bị giãn hết chiều ngang
        pnlInfo.add(lblPrice);

        // 3. Bộ điều khiển (+ / - / ✕)
        JPanel pnlCtrl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 25));
        pnlCtrl.setOpaque(false);

        // Style cho nút cho đẹp đồng bộ
        JButton btnMinus = createSmallBtn("-");
        JLabel lblQty = new JLabel(item[2].toString());
        lblQty.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JButton btnPlus = createSmallBtn("+");

        JButton btnDel = new JButton("🗑");
        setupIcon(btnDel, new Color(231, 76, 60));

        btnMinus.addActionListener(e -> {
            int sl = (int) item[2];
            if (sl > 1) { item[2] = sl - 1; renderCart(); }
        });
        btnPlus.addActionListener(e -> {
            item[2] = (int) item[2] + 1;
            renderCart();
        });
        btnDel.addActionListener(e -> {
            listGioHang.remove(item);
            renderCart();
        });

        pnlCtrl.add(btnMinus);
        pnlCtrl.add(lblQty);
        pnlCtrl.add(btnPlus);
        pnlCtrl.add(Box.createHorizontalStrut(10)); // Khoảng cách tới nút xóa
        pnlCtrl.add(btnDel);

        row.add(lblImg, BorderLayout.WEST);
        row.add(pnlInfo, BorderLayout.CENTER);
        row.add(pnlCtrl, BorderLayout.EAST);

        return row;
    }
    private void setupIcon(JButton btn, Color color) {
        btn.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18)); // Size 15-16 là vừa đẹp
        btn.setForeground(color);
        btn.setBorder(null);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(30, 30)); // Đủ rộng để dễ bấm
    }
    // Hàm tạo nút nhỏ cho gọn giao diện
    private JButton createSmallBtn(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(30, 25));
        btn.setMargin(new Insets(0,0,0,0));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        return btn;
    }

    // ================= MENU CARD (GIỮ NGUYÊN) =================
    public void addMonCard(String maMon, String ten, double gia, String hinhAnh) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setPreferredSize(new Dimension(150, 180));

        JLabel lblImg = new JLabel();
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon icon = (hinhAnh != null && !hinhAnh.isEmpty()) ? new ImageIcon(hinhAnh) : new ImageIcon("src/images/default.png");
        Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        lblImg.setIcon(new ImageIcon(img));

        JLabel lblTen = new JLabel(ten, SwingConstants.CENTER);
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel lblGia = new JLabel(String.format("%,.0f VNĐ", gia), SwingConstants.CENTER);
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 10));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setBackground(Color.WHITE);
        info.add(lblTen);
        info.add(lblGia);

        card.add(lblImg, BorderLayout.CENTER);
        card.add(info, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                addMonToGio(maMon, ten, gia, hinhAnh);
            }
        });

        pnlMenuGrid.add(card);
    }

    // ================= GIỎ HÀNG LOGIC (SỬA ĐỂ PHÙ HỢP CẤU TRÚC MỚI) =================
    public void addMonToGio(String maMon, String ten, double gia, String hinhAnh) {
        for (Object[] item : listGioHang) {
            String sizeMacDinh = "S";

            if (item[0].equals(ten) && item[1].equals(sizeMacDinh)) {
                int sl = (int) item[2];
                item[2] = sl + 1;
                renderCart();
                return;
            }
        }
        listGioHang.add(new Object[]{ten, "S", 1, gia, hinhAnh, gia});
        renderCart();
    }

    // Các hàm này giữ nguyên tên để Controller không lỗi
    public List<Object[]> getCartData() { return listGioHang; }
    public void clearMenu() { pnlMenuGrid.removeAll(); pnlMenuGrid.repaint(); }
    public JComboBox<String> getCbDanhMuc() { return cbDanhMuc; }
    public JTextField getTxtSearch() { return txtSearch; }
    public void setTongTien(double tien) {
        lblTongTien.setText("Tổng tiền: " + String.format("%,.0f VNĐ", tien));
    }
    public void addXoaListener(ActionListener al) {
        btnXoaMon.addActionListener(al);
    }
    public void clearCart() {
        listGioHang.clear();
        renderCart();
    }
    public void addThanhToanListener(ActionListener al) { btnThanhToan.addActionListener(al); }
}