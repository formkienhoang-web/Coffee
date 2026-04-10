package CH.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import CH.model.KhachHang; 
import javax.swing.event.DocumentListener;

public class KhachHangView extends JPanel {

    // Colors
    private final Color TEAL_COLOR = new Color(0, 77, 77);
    
    // Components
    private JTextField txtMaKH, txtTenKH, txtTheLoai, txtEmail, txtSDT, txtDiaChi, txtTimKiem;
    private JRadioButton rdoNam, rdoNu;
    private ButtonGroup btnGroupGender;
    private JTable tableKhachHang;
    private DefaultTableModel tableModel;
    private JButton btnThem, btnSua, btnXoa, btnReset, btnTimKiem;

    public KhachHangView() {
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // 1. Form Area
        JPanel pnlFormContainer = new JPanel();
        pnlFormContainer.setLayout(new BoxLayout(pnlFormContainer, BoxLayout.Y_AXIS));
        pnlFormContainer.setBackground(TEAL_COLOR);
        pnlFormContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblFormTitle = new JLabel("Thông tin Khách hàng");
        lblFormTitle.setForeground(Color.WHITE);
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblFormTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlFormContainer.add(lblFormTitle);
        pnlFormContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(TEAL_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Mã KH + Tên KH
        addFormRow(pnlForm, gbc, 0, "Mã khách hàng", txtMaKH = new JTextField(15));
        txtMaKH.setEditable(false);
        txtMaKH.setBackground(new Color(230, 230, 230));
        txtMaKH.setText("Tự động sinh");
        
        addFormRow(pnlForm, gbc, 2, "Tên khách hàng", txtTenKH = new JTextField(15));
        
        // Row 2: Thể loại + Giới tính
        gbc.gridy = 1;
        addFormRow(pnlForm, gbc, 0, "Thể loại (VIP/Thường)", txtTheLoai = new JTextField(15)); // [MỚI]

        // Giới tính
        gbc.gridx = 2; gbc.weightx = 0;
        JLabel lblGender = new JLabel("Giới tính");
        lblGender.setForeground(Color.WHITE);
        pnlForm.add(lblGender, gbc);

        JPanel pnlGender = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlGender.setBackground(TEAL_COLOR);
        pnlGender.setBorder(new LineBorder(Color.WHITE));
        rdoNam = new JRadioButton("Nam"); rdoNam.setBackground(TEAL_COLOR); rdoNam.setForeground(Color.WHITE);
        rdoNu = new JRadioButton("Nữ"); rdoNu.setBackground(TEAL_COLOR); rdoNu.setForeground(Color.WHITE);
        btnGroupGender = new ButtonGroup(); btnGroupGender.add(rdoNam); btnGroupGender.add(rdoNu);
        pnlGender.add(rdoNam); pnlGender.add(rdoNu);
        
        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlForm.add(pnlGender, gbc);

        // Row 3: Email + SĐT
        gbc.gridy = 2; // Xuống dòng tiếp theo
        addFormRow(pnlForm, gbc, 0, "Email", txtEmail = new JTextField(15)); // [MỚI]
        addFormRow(pnlForm, gbc, 2, "Số điện thoại", txtSDT = new JTextField(15));

        // Row 4: Địa chỉ
        gbc.gridy = 3;
        addFormRow(pnlForm, gbc, 0, "Địa chỉ", txtDiaChi = new JTextField(15));

        pnlFormContainer.add(pnlForm);

        // Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlButtons.setBackground(TEAL_COLOR);
        btnThem = createStyledButton("Thêm"); btnSua = createStyledButton("Sửa"); btnXoa = createStyledButton("Xóa");
        btnReset = createStyledButton("RESET");
        pnlButtons.add(btnThem); pnlButtons.add(btnSua); pnlButtons.add(btnXoa);
        pnlButtons.add(btnReset);
        pnlFormContainer.add(pnlButtons);

        // Search
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setBackground(TEAL_COLOR);
        txtTimKiem = new JTextField(15);
        btnTimKiem = createStyledButton("Tìm kiếm");
        pnlSearch.add(new JLabel("Tìm kiếm theo tên KH: ")).setForeground(Color.WHITE);
        pnlSearch.add(txtTimKiem);
        pnlFormContainer.add(pnlSearch);

        add(pnlFormContainer, BorderLayout.NORTH);

        // 2. Table Area
        // [CẬP NHẬT] Tên cột khớp với Model Khách Hàng
        String[] columnNames = {"Mã KH", "Tên khách hàng", "Thể loại", "Giới tính", "Email", "SĐT", "Địa chỉ"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tableKhachHang = new JTable(tableModel);
        tableKhachHang.setRowHeight(25);
        tableKhachHang.getTableHeader().setBackground(new Color(230, 230, 230));
        tableKhachHang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(tableKhachHang);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Helper methods
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int x, String labelText, Component field) {
        gbc.gridx = x; gbc.weightx = 0; gbc.fill = GridBagConstraints.BOTH;
        JLabel label = new JLabel(labelText); label.setForeground(Color.WHITE);
        panel.add(label, gbc);
        gbc.gridx = x + 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        field.setPreferredSize(new Dimension(150, 25));
        panel.add(field, gbc);
        gbc.weightx = 0;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE); btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(80, 30)); btn.setFocusPainted(false);
        return btn;
    }

    // [QUAN TRỌNG] Sửa kiểu trả về là KhachHang
    public KhachHang getKhachHangInfo() {
        String gt = rdoNam.isSelected() ? "Nam" : "Nữ";
        return new KhachHang(
            txtMaKH.getText(), 
            txtTenKH.getText(), 
            txtTheLoai.getText(), // Thay cho NgaySinh
            gt, 
            txtEmail.getText(),   // Thay cho ChucVu
            txtSDT.getText(), 
            txtDiaChi.getText()
        );
    }

    
    public void fillForm(KhachHang kh) {
        txtMaKH.setText(kh.getMaKH());
        txtTenKH.setText(kh.getTenKH());
        txtTheLoai.setText(kh.getTheLoai());
        txtEmail.setText(kh.getEmail());
        txtSDT.setText(kh.getSoDienThoai());
        txtDiaChi.setText(kh.getDiaChi());
        if (kh.getGioiTinh() != null && kh.getGioiTinh().equals("Nam")) rdoNam.setSelected(true); else rdoNu.setSelected(true);
    }

    public void clearForm() {
        txtMaKH.setText("Tự động sinh"); 
        txtTenKH.setText("");
        txtTheLoai.setText(""); 
        txtEmail.setText("");
        txtSDT.setText(""); 
        txtDiaChi.setText("");
        btnGroupGender.clearSelection();
    }
    
    public JTable getTable() { return tableKhachHang; }
    public void clearTable() { tableModel.setRowCount(0); }
    
    // [QUAN TRỌNG] Sửa tham số là KhachHang
    public void addRowToTable(KhachHang kh) { 
        tableModel.addRow(kh.toObjectArray()); 
    }
    
    public int getSelectedRow() { return tableKhachHang.getSelectedRow(); }
    
    // [QUAN TRỌNG] Update theo Model KhachHang
    public void updateRowInTable(KhachHang kh, int row) {
        tableModel.setValueAt(kh.getMaKH(), row, 0); 
        tableModel.setValueAt(kh.getTenKH(), row, 1);
        tableModel.setValueAt(kh.getTheLoai(), row, 2); 
        tableModel.setValueAt(kh.getGioiTinh(), row, 3);
        tableModel.setValueAt(kh.getEmail(), row, 4); 
        tableModel.setValueAt(kh.getSoDienThoai(), row, 5);
        tableModel.setValueAt(kh.getDiaChi(), row, 6);
    }
    
    public void removeRowInTable(int row) { tableModel.removeRow(row); }
    public void addTableSelectionListener(javax.swing.event.ListSelectionListener listener) { tableKhachHang.getSelectionModel().addListSelectionListener(listener); }
    public void addThemListener(ActionListener al) { btnThem.addActionListener(al); }
    public void addSuaListener(ActionListener al) { btnSua.addActionListener(al); }
    public void addXoaListener(ActionListener al) { btnXoa.addActionListener(al); }
    public void addResetListener(ActionListener al) { btnReset.addActionListener(al); }
    public String getTuKhoaTimKiem() {
        return txtTimKiem.getText().trim();
    }
    
    public void addTimKiemListener(ActionListener al) {
        btnTimKiem.addActionListener(al);
        // Hỗ trợ nhấn Enter trong ô tìm kiếm
        txtTimKiem.addActionListener(al); 
    }
    public void addLiveSearchListener(DocumentListener dl) {
        // Lắng nghe sự thay đổi của văn bản bên trong ô txtTimKiem
        txtTimKiem.getDocument().addDocumentListener(dl);
    }
}