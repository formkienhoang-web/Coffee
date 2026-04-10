package CH.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import CH.model.NhanVien;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.event.DocumentListener;

public class NhanVienView extends JPanel {

    // Colors
    private final Color TEAL_COLOR = new Color(0, 77, 77);
    
    // Components
    private JTextField txtMaNV, txtTenNV, txtChucVu, txtSDT, txtDiaChi, txtTimKiem;
    private JDateChooser txtNgaySinh;
    private JRadioButton rdoNam, rdoNu;
    private ButtonGroup btnGroupGender;
    private JTable tableNhanVien;
    private DefaultTableModel tableModel;
    private JButton btnThem, btnSua, btnXoa, btnReset, btnTimKiem;
    
    // Components cho tài khoản
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cboRole;

    public NhanVienView() {
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // 1. Form Area
        JPanel pnlTitle = new JPanel();
        pnlTitle.setBackground(TEAL_COLOR);

        JLabel lblTitle = new JLabel("Thông tin Nhân viên");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        pnlTitle.add(lblTitle);

        add(pnlTitle, BorderLayout.NORTH);
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(TEAL_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Mã - Tên
        addFormRow(pnlForm, gbc, 0, 0, "Mã nhân viên", txtMaNV = new JTextField(15));
        txtMaNV.setEditable(false); txtMaNV.setText("Tự động sinh");
        addFormRow(pnlForm, gbc, 2, 0, "Tên nhân viên", txtTenNV = new JTextField(15));

        // Dòng 2: Ngày sinh - Giới tính
        gbc.gridy = 1; 
        gbc.gridx = 0; gbc.weightx = 0;
        JLabel lblNgaySinh = new JLabel("Ngày sinh"); lblNgaySinh.setForeground(Color.WHITE); pnlForm.add(lblNgaySinh, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; 
        txtNgaySinh = new JDateChooser(); txtNgaySinh.setDateFormatString("dd/MM/yyyy"); txtNgaySinh.setPreferredSize(new Dimension(150, 25));
        pnlForm.add(txtNgaySinh, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        JLabel lblGioiTinh = new JLabel("Giới tính"); lblGioiTinh.setForeground(Color.WHITE); pnlForm.add(lblGioiTinh, gbc);
        
        JPanel pnlGender = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlGender.setBackground(TEAL_COLOR);
        rdoNam = new JRadioButton("Nam"); rdoNam.setBackground(TEAL_COLOR); rdoNam.setForeground(Color.WHITE);
        rdoNu = new JRadioButton("Nữ"); rdoNu.setBackground(TEAL_COLOR); rdoNu.setForeground(Color.WHITE);
        btnGroupGender = new ButtonGroup(); btnGroupGender.add(rdoNam); btnGroupGender.add(rdoNu);
        pnlGender.add(rdoNam); pnlGender.add(rdoNu);
        gbc.gridx = 3; pnlForm.add(pnlGender, gbc);

        // Dòng 3: Chức vụ - SĐT
        gbc.gridy = 2;
        addFormRow(pnlForm, gbc, 0, 2, "Chức vụ", txtChucVu = new JTextField(15));
        addFormRow(pnlForm, gbc, 2, 2, "Số điện thoại", txtSDT = new JTextField(15));

        // Dòng 4: Địa chỉ - Tài khoản (Username)
        gbc.gridy = 3;
        addFormRow(pnlForm, gbc, 0, 3, "Địa chỉ", txtDiaChi = new JTextField(15));
        addFormRow(pnlForm, gbc, 2, 3, "Tài khoản", txtUsername = new JTextField(15)); 

        // Dòng 5: Mật khẩu - Quyền (Role)
        gbc.gridy = 4;
        gbc.gridx = 0; gbc.weightx = 0;
        JLabel lblPass = new JLabel("Mật khẩu"); lblPass.setForeground(Color.WHITE); pnlForm.add(lblPass, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPassword = new JPasswordField(15);
        pnlForm.add(txtPassword, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        JLabel lblRole = new JLabel("Phân quyền"); lblRole.setForeground(Color.WHITE); pnlForm.add(lblRole, gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        cboRole = new JComboBox<>(new String[]{"ADMIN", "NHÂN VIÊN"});
        pnlForm.add(cboRole, gbc);

        // Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlButtons.setBackground(TEAL_COLOR);
        btnThem = createStyledButton("Thêm"); btnSua = createStyledButton("Sửa"); btnXoa = createStyledButton("Xóa"); btnReset = createStyledButton("RESET");
        pnlButtons.add(btnThem); pnlButtons.add(btnSua); pnlButtons.add(btnXoa); pnlButtons.add(btnReset);
        
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlForm, BorderLayout.CENTER);
        pnlTop.add(pnlButtons, BorderLayout.SOUTH);


        // Search Panel
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setBackground(TEAL_COLOR);
        txtTimKiem = new JTextField(15);
        btnTimKiem = createStyledButton("Tìm kiếm");
        JLabel lblSearch = new JLabel("Tìm kiếm theo tên NV: "); lblSearch.setForeground(Color.WHITE);
        pnlSearch.add(lblSearch); pnlSearch.add(txtTimKiem);
        pnlButtons.add(pnlSearch);
        
        // Table
        String[] columnNames = {"Mã NV", "Tên Nhân Viên", "Ngày sinh", "Giới tính", "Chức vụ", "SĐT", "Địa chỉ", "Tài khoản", "Quyền"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tableNhanVien = new JTable(tableModel);
        tableNhanVien.setRowHeight(25);
        tableNhanVien.getTableHeader().setBackground(new Color(230, 230, 230));
        tableNhanVien.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(tableNhanVien);

// panel chứa form + table
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(pnlTop, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int x, int y, String labelText, Component field) {
        gbc.gridx = x; gbc.gridy = y; 
        gbc.weightx = 0; gbc.fill = GridBagConstraints.BOTH;
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

    // --- CÁC HÀM GET/SET ---

    // [MỚI] Hàm set text cho Mã NV (Controller cần dùng hàm này)
    public void setMaNV(String text) {
        txtMaNV.setText(text);
    }

    public NhanVien getNhanVienInfo() {
        String gt = rdoNam.isSelected() ? "Nam" : "Nữ";
        String strNgaySinh = "";
        if (txtNgaySinh.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            strNgaySinh = sdf.format(txtNgaySinh.getDate());
        }

        return new NhanVien(
            txtMaNV.getText(), 
            txtTenNV.getText(), 
            strNgaySinh, 
            gt, 
            txtChucVu.getText(), 
            txtSDT.getText(), 
            txtDiaChi.getText(),
            txtUsername.getText(),
            new String(txtPassword.getPassword()), 
            cboRole.getSelectedItem().toString() 
        );
    }

    public void fillForm(NhanVien nv) {
        txtMaNV.setText(nv.getMaNV());
        txtTenNV.setText(nv.getTenNV());

        // Xử lý Ngày sinh an toàn hơn
        try {
            // Thử parse theo dd/MM/yyyy trước
            if (nv.getNgaySinh() != null && !nv.getNgaySinh().isEmpty()) {
                Date date;
                if(nv.getNgaySinh().contains("-")) {
                     // Nếu DB trả về yyyy-MM-dd
                     date = new SimpleDateFormat("yyyy-MM-dd").parse(nv.getNgaySinh());
                } else {
                     // Nếu DB trả về dd/MM/yyyy
                     date = new SimpleDateFormat("dd/MM/yyyy").parse(nv.getNgaySinh());
                }
                txtNgaySinh.setDate(date);
            } else {
                txtNgaySinh.setDate(null);
            }
        } catch (Exception e) {
            txtNgaySinh.setDate(null);
        }

        txtChucVu.setText(nv.getChucVu());
        txtSDT.setText(nv.getSoDienThoai());
        txtDiaChi.setText(nv.getDiaChi());
        
        if ("Nam".equalsIgnoreCase(nv.getGioiTinh())) rdoNam.setSelected(true); 
        else rdoNu.setSelected(true);
        
        txtUsername.setText(nv.getUsername());
        // Không set password lên form để bảo mật, hoặc set rỗng
        txtPassword.setText(""); 
        
        if(nv.getRole() != null) {
            cboRole.setSelectedItem(nv.getRole());
        }
    }

    public void clearForm() {
        txtMaNV.setText("Tự động sinh"); 
        txtTenNV.setText("");
        txtNgaySinh.setDate(null);
        txtChucVu.setText(""); txtSDT.setText(""); txtDiaChi.setText("");
        btnGroupGender.clearSelection();
        txtUsername.setText("");
        txtPassword.setText("");
        cboRole.setSelectedIndex(0);
    }
    
    public JTable getTable() { return tableNhanVien; }
    public void addTableSelectionListener(javax.swing.event.ListSelectionListener listener) { tableNhanVien.getSelectionModel().addListSelectionListener(listener); }
    public void addThemListener(ActionListener al) { btnThem.addActionListener(al); }
    public void addSuaListener(ActionListener al) { btnSua.addActionListener(al); }
    public void addXoaListener(ActionListener al) { btnXoa.addActionListener(al); }
    public void addResetListener(ActionListener al) { btnReset.addActionListener(al); }
    public String getTuKhoaTimKiem() {
        return txtTimKiem.getText().trim();
    }
    
    public void addTimKiemListener(ActionListener al) {
        btnTimKiem.addActionListener(al);
        txtTimKiem.addActionListener(al);
    }
    public void addLiveSearchListener(DocumentListener dl) {
        txtTimKiem.getDocument().addDocumentListener(dl);
    }
}