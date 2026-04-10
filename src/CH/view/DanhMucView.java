package CH.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DanhMucView extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtMaDM, txtTenDM;

    // 🔥 THÊM SEARCH
    private JTextField txtTimKiem;
    private JButton btnTimKiem;

    private JButton btnThem, btnSua, btnXoa, btnReset;

    private final Color PRIMARY_COLOR = new Color(0, 77, 77);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color BUTTON_BG = Color.WHITE;
    private final Color BUTTON_TEXT = Color.BLACK;

    public DanhMucView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===================== TOP =====================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("QUẢN LÝ DANH MỤC", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_COLOR);
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        topPanel.add(lblTitle, BorderLayout.NORTH);

        // ===================== FORM =====================
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(PRIMARY_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addLabel(inputPanel, "Mã danh mục:", 0, 0, gbc);
        txtMaDM = createTextField();
        txtMaDM.setText("Tự động sinh");
        txtMaDM.setEditable(false);
        addComponent(inputPanel, txtMaDM, 1, 0, gbc);

        addLabel(inputPanel, "Tên danh mục:", 2, 0, gbc);
        txtTenDM = createTextField();
        addComponent(inputPanel, txtTenDM, 3, 0, gbc);

        // ===================== BUTTON =====================
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(PRIMARY_COLOR);

        btnThem = createButton("Thêm");
        btnSua = createButton("Sửa");
        btnXoa = createButton("Xóa");
        btnReset = createButton("Reset");

        btnPanel.add(btnThem);
        btnPanel.add(btnSua);
        btnPanel.add(btnXoa);
        btnPanel.add(btnReset);
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(btnPanel, BorderLayout.SOUTH);

        // ===================== SEARCH =====================
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setBackground(PRIMARY_COLOR);

        JLabel lblSearch = new JLabel("Tìm theo tên danh mục:");
        lblSearch.setForeground(TEXT_COLOR);

        txtTimKiem = new JTextField(15);
        btnTimKiem = createButton("Tìm kiếm");

        pnlSearch.add(lblSearch);
        pnlSearch.add(txtTimKiem);
//        pnlSearch.add(btnTimKiem);

        // ===================== CENTER TOP =====================
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(PRIMARY_COLOR);
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(btnPanel, BorderLayout.CENTER);
        centerPanel.add(pnlSearch, BorderLayout.SOUTH);

        topPanel.add(centerPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // ===================== TABLE =====================
        String[] headers = {"Mã danh mục", "Tên danh mục"};
        model = new DefaultTableModel(headers, 0);

        table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    // ===================== UI HELPER =====================
    private void addLabel(JPanel p, String text, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0;

        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_COLOR);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        p.add(lbl, gbc);
    }

    private void addComponent(JPanel p, JComponent comp, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 1.0;
        p.add(comp, gbc);
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(180, 30));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return tf;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BUTTON_BG);
        btn.setForeground(BUTTON_TEXT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(80, 30)); btn.setFocusPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ===================== GET =====================
    public String getMaDM() { return txtMaDM.getText(); }
    public String getTenDM() { return txtTenDM.getText(); }

    public JTable getTable() { return table; }

    public void addRow(Object[] row) { model.addRow(row); }
    public void clearTable() { model.setRowCount(0); }

    public void setForm(String ma, String ten) {
        txtMaDM.setText(ma);
        txtTenDM.setText(ten);
    }

    public void clearForm() {
        txtMaDM.setText("Tự động sinh");
        txtTenDM.setText("");
    }

    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnReset() { return btnReset; }

    // ===================== 🔥 SEARCH =====================
    public String getTuKhoaTimKiem() {
        return txtTimKiem.getText().trim();
    }

    public void addTimKiemListener(java.awt.event.ActionListener al) {
        btnTimKiem.addActionListener(al);
        txtTimKiem.addActionListener(al);
    }

    public void addLiveSearchListener(javax.swing.event.DocumentListener dl) {
        txtTimKiem.getDocument().addDocumentListener(dl);
    }
}