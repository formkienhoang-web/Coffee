package CH.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import CH.model.MonAn;

public class ThucDonView extends JPanel {
    private JTextField txtMaMon, txtTenMon, txtDonGia, txtDVT;

    // ❌ ĐÃ BỎ cboMaHH
    private JComboBox<String> cboDanhMuc;

    private JLabel lblHinhAnh;
    private JButton btnChonAnh;
    private String duongDanAnh = "";

    private JTable table;
    private DefaultTableModel model;
    private JButton btnThem, btnSua, btnXoa, btnReset;

    public ThucDonView() {
        setLayout(new BorderLayout());

        // TITLE
        JPanel pnlTitle = new JPanel();
        pnlTitle.setBackground(new Color(0, 77, 77));

        JLabel lblTitle = new JLabel("Thông tin Thực đơn");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        pnlTitle.add(lblTitle);

        add(pnlTitle, BorderLayout.NORTH);

        // --- FORM PANEL ---
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(new Color(0, 77, 77));
        pnlForm.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- INPUT ---
        addInput(pnlForm, gbc, 0, 0, "Mã món", txtMaMon = new JTextField(15));
        txtMaMon.setEditable(false);
        txtMaMon.setText("Tự động");

        addInput(pnlForm, gbc, 0, 1, "Tên món", txtTenMon = new JTextField(15));
        addInput(pnlForm, gbc, 0, 2, "Đơn giá", txtDonGia = new JTextField(15));
        addInput(pnlForm, gbc, 0, 3, "Đơn vị tính", txtDVT = new JTextField(15));

        // ❌ ĐÃ BỎ Mã HH → chỉ còn Danh mục
        cboDanhMuc = new JComboBox<>();
        addInput(pnlForm, gbc, 0, 4, "Danh mục", cboDanhMuc);

        // --- ẢNH ---
        lblHinhAnh = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblHinhAnh.setPreferredSize(new Dimension(150, 150));
        lblHinhAnh.setBorder(new LineBorder(Color.WHITE, 1));
        lblHinhAnh.setOpaque(true);
        lblHinhAnh.setBackground(Color.LIGHT_GRAY);

        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridheight = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 20, 5, 5);
        pnlForm.add(lblHinhAnh, gbc);

        btnChonAnh = new JButton("Chọn ảnh");
        btnChonAnh.setFocusPainted(false);
        btnChonAnh.setBackground(Color.WHITE);
        btnChonAnh.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridx = 2; gbc.gridy = 5;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlForm.add(btnChonAnh, gbc);

        btnChonAnh.addActionListener(e -> chonAnh());
        lblHinhAnh.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { chonAnh(); }
        });

        // --- BUTTONS ---
        JPanel pnlBtn = new JPanel();
        pnlBtn.setBackground(new Color(0, 77, 77));
        btnThem = createButton("Thêm");
        btnSua = createButton("Sửa");
        btnXoa = createButton("Xóa");
        btnReset = createButton("Reset");

        pnlBtn.add(btnThem);
        pnlBtn.add(btnSua);
        pnlBtn.add(btnXoa);
        pnlBtn.add(btnReset);

        JPanel pnlNorth = new JPanel(new BorderLayout());
        pnlNorth.add(pnlForm, BorderLayout.CENTER);
        pnlNorth.add(pnlBtn, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(pnlTitle, BorderLayout.NORTH);
        topPanel.add(pnlNorth, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // --- TABLE ---
        // ❌ ĐÃ BỎ CỘT Mã HH
        String[] cols = {
                "Mã món", "Tên món", "Đơn giá",
                "Đơn vị tính",
                "Danh mục",
                "Hình ảnh"
        };

        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void addInput(JPanel p, GridBagConstraints gbc, int x, int y, String lbl, Component cmp) {
        gbc.gridx = x; gbc.gridy = y;
        gbc.gridheight = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel l = new JLabel(lbl);
        l.setForeground(Color.WHITE);
        p.add(l, gbc);

        gbc.gridx = x+1;
        p.add(cmp, gbc);
    }
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 🔥 SIZE Ở ĐÂY
        btn.setPreferredSize(new Dimension(80, 30));

        return btn;
    }

    private void chonAnh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh", "jpg", "png", "jpeg"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            duongDanAnh = selectedFile.getAbsolutePath();
            setHinhAnhToLabel(duongDanAnh);
        }
    }

    public void setHinhAnhToLabel(String path) {
        if (path == null || path.isEmpty()) {
            lblHinhAnh.setIcon(null);
            lblHinhAnh.setText("Chưa có ảnh");
            return;
        }

        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            lblHinhAnh.setIcon(new ImageIcon(newImg));
            lblHinhAnh.setText("");
        } catch (Exception e) {
            lblHinhAnh.setText("Lỗi ảnh");
        }
    }

    public MonAn getMonAnInfo() {
        double gia = 0;
        try {
            String giaStr = txtDonGia.getText().trim().replace(",", "");
            if (!giaStr.isEmpty()) {
                gia = Double.parseDouble(giaStr);
            }
        } catch (Exception e) {
            gia = 0;
        }

        String tenDanhMuc = "";
        if (cboDanhMuc.getSelectedItem() != null) {
            tenDanhMuc = cboDanhMuc.getSelectedItem().toString();
        }

        return new MonAn(
                txtMaMon.getText(),
                txtTenMon.getText(),
                gia,
                txtDVT.getText(),
                duongDanAnh,
                tenDanhMuc
        );
    }

    public void fillForm(MonAn m) {
        txtMaMon.setText(m.getMaMon());
        txtTenMon.setText(m.getTenMon());
        txtDonGia.setText(String.format("%.0f", m.getDonGia()));
        txtDVT.setText(m.getDonViTinh());

        if(m.getTenDanhMuc() != null) {
            cboDanhMuc.setSelectedItem(m.getTenDanhMuc());
        }

        duongDanAnh = m.getHinhAnh();

        if (duongDanAnh != null && !duongDanAnh.isEmpty()) {
            setHinhAnhToLabel(duongDanAnh);
        } else {
            lblHinhAnh.setIcon(null);
            lblHinhAnh.setText("Chưa có ảnh");
        }
    }

    public void clearForm() {
        txtMaMon.setText("Tự động");
        txtTenMon.setText("");
        txtDonGia.setText("");
        txtDVT.setText("");

        if (cboDanhMuc.getItemCount() > 0) {
            cboDanhMuc.setSelectedIndex(0);
        }

        duongDanAnh = null; // ✅ nên dùng null
        lblHinhAnh.setIcon(null);
        lblHinhAnh.setText("Chưa có ảnh");
    }

    public JComboBox<String> getCboDanhMuc(){
        return cboDanhMuc;
    }

    public void addRow(MonAn m) {
        model.addRow(new Object[]{
                m.getMaMon(),
                m.getTenMon(),
                String.format("%,.0f", m.getDonGia()),
                m.getDonViTinh(),
                m.getTenDanhMuc(),
                m.getHinhAnh() // ✅ QUAN TRỌNG
        });
    }
    public void clearTable() { model.setRowCount(0); }
    public int getSelectedRow() { return table.getSelectedRow(); }
    public JTable getTable() { return table; }
    public void addThemListener(ActionListener al) { btnThem.addActionListener(al); }
    public void addSuaListener(ActionListener al) { btnSua.addActionListener(al); }
    public void addXoaListener(ActionListener al) { btnXoa.addActionListener(al); }
    public void addResetListener(ActionListener al) { btnReset.addActionListener(al); }
}