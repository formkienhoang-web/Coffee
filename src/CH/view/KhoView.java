package CH.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class KhoView extends JPanel {
    private JTable khoTable;
    private DefaultTableModel tableModel;
    
    // Components
    private JTextField txtMaHH, txtTenHH, txtSoLuong;
    private JButton btnThem, btnSua, btnXoa, btnTimKiem, btnReset; 
    private JTextField txtTimKiem;

    // Màu sắc chủ đạo (Đồng bộ với MainView/Hóa Đơn)
    private final Color PRIMARY_COLOR = new Color(0, 77, 77); // Xanh đậm
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color BUTTON_BG = Color.WHITE;
    private final Color BUTTON_TEXT = Color.BLACK;

    public KhoView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- PHẦN 1: PANEL NHẬP LIỆU (Màu Xanh) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1.1 Tiêu đề
        JLabel lblTitle = new JLabel("THÔNG TIN HÀNG HÓA", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_COLOR);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        topPanel.add(lblTitle, BorderLayout.NORTH);

        // 1.2 Form nhập liệu (Dùng GridBagLayout để căn thẳng hàng)
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(PRIMARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); // Khoảng cách giữa các ô
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Hàng 1
        addLabel(inputPanel, "Mã Hàng Hóa:", 0, 0, gbc);
        txtMaHH = createTextField();
        txtMaHH.setEditable(false);
        txtMaHH.setBackground(new Color(230, 230, 230));
        txtMaHH.setText("Tự động sinh");
        addComponent(inputPanel, txtMaHH, 1, 0, gbc);
        
        addLabel(inputPanel, "Tên Hàng Hóa:", 2, 0, gbc); // Cột 2
        txtTenHH = createTextField();
        addComponent(inputPanel, txtTenHH, 3, 0, gbc);

        // Hàng 2
        addLabel(inputPanel, "Số Lượng:", 0, 1, gbc);
        txtSoLuong = createTextField();
        addComponent(inputPanel, txtSoLuong, 1, 1, gbc);
        


        // 1.3 Panel chứa các Nút chức năng
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        btnPanel.setBackground(PRIMARY_COLOR);
        
        btnThem = createButton("Thêm");
        btnSua = createButton("Sửa");
        btnXoa = createButton("Xóa");
        btnReset = createButton("Reset"); 
        
        btnPanel.add(btnThem);
        btnPanel.add(btnSua);
        btnPanel.add(btnXoa);
        btnPanel.add(btnReset);

        // 1.4 Panel Tìm kiếm (Nằm bên phải các nút)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        searchPanel.setBackground(PRIMARY_COLOR);
        JLabel lblTimKiem = new JLabel("Tên HH:");
        lblTimKiem.setForeground(TEXT_COLOR);
        txtTimKiem = new JTextField(15);
        txtTimKiem.setPreferredSize(new Dimension(150, 30));
        btnTimKiem = createButton("Tìm Kiếm");

        searchPanel.add(lblTimKiem);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);

        // Ghép nút và tìm kiếm vào một dòng dưới cùng của TopPanel
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(PRIMARY_COLOR);
        bottomBar.add(btnPanel, BorderLayout.WEST);
        bottomBar.add(searchPanel, BorderLayout.EAST);
        bottomBar.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Đóng gói phần Input và Button vào TopPanel chính
        JPanel centerInputContainer = new JPanel(new BorderLayout());
        centerInputContainer.setBackground(PRIMARY_COLOR);
        centerInputContainer.add(inputPanel, BorderLayout.CENTER);
        centerInputContainer.add(bottomBar, BorderLayout.SOUTH);
        
        topPanel.add(centerInputContainer, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

        // --- PHẦN 2: BẢNG DỮ LIỆU ---
        String[] headers = {"Mã HH", "Tên HH", "Số Lượng"};
        tableModel = new DefaultTableModel(headers, 0);
        khoTable = new JTable(tableModel);
        khoTable.setRowHeight(28); // Hàng cao hơn chút cho dễ nhìn
        khoTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        khoTable.getTableHeader().setBackground(new Color(230, 230, 230));
        
        JScrollPane scrollPane = new JScrollPane(khoTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    // --- CÁC HÀM HỖ TRỢ TẠO GIAO DIỆN ---
    private void addLabel(JPanel p, String text, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y;
        gbc.weightx = 0; 
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(TEXT_COLOR); // Chữ màu trắng
        p.add(lbl, gbc);
    }
    
    private void addComponent(JPanel p, JComponent comp, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y;
        gbc.weightx = 1.0; 
        p.add(comp, gbc);
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(180, 30));
        return tf;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(BUTTON_BG); 
        btn.setForeground(BUTTON_TEXT); 
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- GETTERS (Giữ nguyên để Controller hoạt động) ---
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getTxtMaHH() { return txtMaHH; }
    public JTextField getTxtTenHH() { return txtTenHH; }
    public JTextField getTxtSoLuong() { return txtSoLuong; }

    public JTable getKhoTable() { return khoTable; }
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnTimKiem() { return btnTimKiem; }
    public JTextField getTxtTimKiem() { return txtTimKiem; }
    public JButton getBtnReset() { return btnReset; } 
}