package CH.view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import CH.model.NhanVien;
import com.toedter.calendar.JDateChooser;
import java.util.Date;

public class NhanVienView extends JPanel {
    private JTextField txtMaNV, txtTenNV, txtChucVu, txtSDT, txtDiaChi, txtSearch, txtUsername;
    private JPasswordField txtPassword;
    private JDateChooser txtNgaySinh;
    private JRadioButton rdoNam, rdoNu;
    private ButtonGroup btnGroupGender;
    private JComboBox<String> cboRole;
    private JButton btnThem, btnSua, btnXoa, btnLuu;
    private JTable table;
    private DefaultTableModel model;
    private JDialog dialogForm;

    private final Color COLOR_PRIMARY = new Color(38, 70, 83);
    private final Color COLOR_ACCENT = new Color(42, 157, 143);
    private final Color COLOR_BG = Color.WHITE;

    public NhanVienView() {
        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Quản Lý Nhân Viên");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_PRIMARY);

        JLabel lblSubTitle = new JLabel("Quản lý thông tin, chức vụ và tài khoản đăng nhập của nhân viên");
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubTitle.setForeground(Color.GRAY);
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSubTitle);

        // --- 2. ACTION BAR ---
        JPanel pnlAction = new JPanel(new BorderLayout(25, 0));
        pnlAction.setOpaque(false);

        txtSearch = new JTextField("  🔍  Tìm kiếm theo tên nhân viên....") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtSearch.setPreferredSize(new Dimension(780, 45));
        txtSearch.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        txtSearch.setForeground(Color.GRAY);

        btnThem = new JButton("+  Thêm Nhân Viên") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(53, 79, 78));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Chuẩn viên thuốc
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btnThem.setPreferredSize(new Dimension(180, 45));
        btnThem.setContentAreaFilled(false);
        btnThem.setBorderPainted(false);
        btnThem.setFocusPainted(false);
        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlAction.add(txtSearch, BorderLayout.WEST);
        pnlAction.add(btnThem, BorderLayout.EAST);

        JPanel pnlTop = new JPanel(new BorderLayout(0, 25));
        pnlTop.setOpaque(false);
        pnlTop.add(pnlHeader, BorderLayout.NORTH);
        pnlTop.add(pnlAction, BorderLayout.CENTER);

        // --- 3. TABLE VỚI HIỆU ỨNG ---
        String[] cols = {"Mã NV", "Họ tên", "Ngày sinh", "Giới tính", "Chức vụ", "SĐT", "Hành động"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return c == 6; }
        };

        table = new JTable(model) {
            private int hoveredRow = -1;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                // Màu nền mặc định
                c.setBackground(Color.WHITE);

                // Hiệu ứng di chuột (Hover) - Nháy nhẹ xanh nhạt
                if (c instanceof JComponent) {
                    ((JComponent) c).putClientProperty(
                            RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                    );
                }

                // ✅ Đồng bộ font
                c.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                c.setBackground(Color.WHITE);

                // Hover
                if (row == hoveredRow) {
                    c.setBackground(new Color(245, 248, 250));
                }

                // Selected
                if (isRowSelected(row)) {
                    c.setBackground(new Color(204, 243, 229));
                    c.setForeground(new Color(0, 105, 92));
//                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else {
                    c.setForeground(new Color(60, 60, 60));
                }

                return c;
            }

            {
                // Bắt sự kiện di chuyển chuột trên bảng
                MouseAdapter ma = new MouseAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        int row = rowAtPoint(e.getPoint());
                        if (row != hoveredRow) {
                            hoveredRow = row;
                            repaint();
                        }
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hoveredRow = -1;
                        repaint();
                    }
                };
                addMouseMotionListener(ma);
                addMouseListener(ma);
            }
        };

        setupTableStyle();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(245, 245, 245)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(pnlTop, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        initDialogForm();
        initHiddenButtons();
        setupSearchPlaceholder();
    }

    private void setupTableStyle() {
        table.setRowHeight(60); // giống hoadon
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFocusable(false);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 50));

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setBackground(new Color(242, 245, 248));
                label.setForeground(Color.GRAY);
                if (column == 6) {
                    label.setHorizontalAlignment(JLabel.CENTER);
                } else {
                    label.setHorizontalAlignment(JLabel.LEFT);
                }

                // chỉ giữ 1 đường kẻ dưới
                label.setBorder(BorderFactory.createMatteBorder(
                        0, 0, 1, 0, new Color(240, 240, 240)));

                return label;
            }
        });

        table.getColumnModel().getColumn(6).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ActionButtonEditor(new JCheckBox()));
        table.getColumnModel().getColumn(1).setPreferredWidth(180); // Họ tên
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Loại KH
    }

    private void initDialogForm() {
        dialogForm = new JDialog();
        dialogForm.setModal(true);
        dialogForm.setSize(450, 750);
        dialogForm.setLayout(new BorderLayout());
        dialogForm.getContentPane().setBackground(Color.WHITE);
        dialogForm.setLocationRelativeTo(null);

        JPanel pnlMainForm = new JPanel();
        pnlMainForm.setLayout(new BoxLayout(pnlMainForm, BoxLayout.Y_AXIS));
        pnlMainForm.setOpaque(false);
        pnlMainForm.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Components
        txtMaNV = createModernTextField("Tự động sinh", false);
        txtTenNV = createModernTextField("Nhập họ tên...", true);
        txtNgaySinh = new JDateChooser();
        txtNgaySinh.setPreferredSize(new Dimension(0, 38));
        txtNgaySinh.setBorder(new ModernBorder(8));

        txtChucVu = createModernTextField("Nhập chức vụ...", true);
        txtSDT = createModernTextField("Nhập số điện thoại...", true);
        txtDiaChi = createModernTextField("Nhập địa chỉ...", true);

        rdoNam = new JRadioButton("Nam"); rdoNu = new JRadioButton("Nữ");
        rdoNam.setOpaque(false); rdoNu.setOpaque(false);
        btnGroupGender = new ButtonGroup(); btnGroupGender.add(rdoNam); btnGroupGender.add(rdoNu);
        JPanel pnlGender = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlGender.setOpaque(false); pnlGender.add(rdoNam); pnlGender.add(rdoNu);

        txtUsername = createModernTextField("Tên đăng nhập...", true);
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(0, 38));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(new ModernBorder(8), BorderFactory.createEmptyBorder(0,12,0,12)));

        cboRole = new JComboBox<>(new String[]{"Nhân viên", "Admin"});
        cboRole.setPreferredSize(new Dimension(0, 38));
        cboRole.setBorder(new ModernBorder(8));

        // Add to Form
        addModernInput(pnlMainForm, "Mã nhân viên", txtMaNV, false);
        addModernInput(pnlMainForm, "Họ và tên", txtTenNV, true);
        addModernInput(pnlMainForm, "Ngày sinh", txtNgaySinh, true);
        addModernInput(pnlMainForm, "Giới tính", pnlGender, true);
        addModernInput(pnlMainForm, "Chức vụ", txtChucVu, true);
        addModernInput(pnlMainForm, "Số điện thoại", txtSDT, true);
        addModernInput(pnlMainForm, "Tên đăng nhập", txtUsername, true);
        addModernInput(pnlMainForm, "Mật khẩu", txtPassword, true);
        addModernInput(pnlMainForm, "Quyền hạn", cboRole, true);

        btnLuu = createStyledButton("Xác nhận Lưu", COLOR_ACCENT);
        btnLuu.setPreferredSize(new Dimension(0, 45));

        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setOpaque(false);
        pnlFooter.setBorder(new EmptyBorder(10, 30, 20, 30));
        pnlFooter.add(btnLuu, BorderLayout.CENTER);

        dialogForm.add(pnlMainForm, BorderLayout.CENTER);
        dialogForm.add(pnlFooter, BorderLayout.SOUTH);
    }

    // --- RENDERER & EDITOR (FORMAT GIỐNG THUCDON) ---
    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnEditIcon = new JButton("✎");
        private JButton btnDeleteIcon = new JButton("🗑");
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
            setOpaque(true);
            setupIcon(btnEditIcon, COLOR_ACCENT);
            setupIcon(btnDeleteIcon, new Color(231, 76, 60));
            add(btnEditIcon); add(btnDeleteIcon);
        }
        private void setupIcon(JButton b, Color c) {
            b.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
            b.setForeground(c);
            b.setBorderPainted(false); b.setContentAreaFilled(false);
        }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            setBackground(t.prepareRenderer(t.getDefaultRenderer(Object.class), r, c).getBackground());
            return this;
        }
    }

    class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        private JButton btnEdit = new JButton("✎");
        private JButton btnDelete = new JButton("🗑");
        public ActionButtonEditor(JCheckBox cb) {
            super(cb);
            panel.setOpaque(true);
            setupIcon(btnEdit, COLOR_ACCENT);
            setupIcon(btnDelete, new Color(231, 76, 60));
            btnEdit.addActionListener(e -> { fireEditingStopped(); btnSua.doClick(); });
            btnDelete.addActionListener(e -> { fireEditingStopped(); btnXoa.doClick(); });
            panel.add(btnEdit); panel.add(btnDelete);
        }
        private void setupIcon(JButton b, Color c) {
            b.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
            b.setForeground(c);
            b.setBorderPainted(false); b.setContentAreaFilled(false);
        }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            panel.setBackground(t.getSelectionBackground());
            return panel;
        }
        @Override public Object getCellEditorValue() { return ""; }
    }

    // --- HELPER METHODS ---
    private void addModernInput(JPanel container, String title, JComponent input, boolean req) {
        JPanel pnlGroup = new JPanel(new BorderLayout(0, 5));
        pnlGroup.setOpaque(false);
        pnlGroup.setBorder(new EmptyBorder(0, 0, 10, 0));
        JLabel lblTitle = new JLabel(req ? "<html>" + title + " <font color='red'>*</font></html>" : title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlGroup.add(lblTitle, BorderLayout.NORTH);
        pnlGroup.add(input, BorderLayout.CENTER);
        container.add(pnlGroup);
    }

    private JTextField createModernTextField(String hint, boolean edit) {
        JTextField tf = new JTextField();
        tf.setEditable(edit);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(0, 38));
        tf.setBorder(BorderFactory.createCompoundBorder(new ModernBorder(8), BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        if (!edit) tf.setBackground(new Color(248, 248, 248));
        return tf;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void setupSearchPlaceholder() {
        txtSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if (txtSearch.getText().contains("🔍")) { txtSearch.setText(""); txtSearch.setForeground(Color.BLACK); } }
            public void focusLost(FocusEvent e) { if (txtSearch.getText().isEmpty()) { txtSearch.setText("  🔍  Tìm kiếm theo tên, mã hoặc SĐT..."); txtSearch.setForeground(Color.GRAY); } }
        });
    }

    // --- CÁC HÀM GETTER/SETTER CHO CONTROLLER ---
    public NhanVien getNhanVienInfo() {
        String gt = rdoNam.isSelected() ? "Nam" : "Nữ";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        String ns = txtNgaySinh.getDate() != null ? sdf.format(txtNgaySinh.getDate()) : "";
        return new NhanVien(txtMaNV.getText(), txtTenNV.getText(), ns, gt, txtChucVu.getText(), txtSDT.getText(), txtDiaChi.getText(), txtUsername.getText(), new String(txtPassword.getPassword()), cboRole.getSelectedItem().toString());
    }

    public void fillForm(NhanVien nv) {
        txtMaNV.setText(nv.getMaNV()); txtTenNV.setText(nv.getTenNV());
        try { txtNgaySinh.setDate(new java.text.SimpleDateFormat("dd/MM/yyyy").parse(nv.getNgaySinh())); } catch(Exception e) {}
        if ("Nam".equals(nv.getGioiTinh())) rdoNam.setSelected(true); else rdoNu.setSelected(true);
        txtChucVu.setText(nv.getChucVu()); txtSDT.setText(nv.getSoDienThoai()); txtDiaChi.setText(nv.getDiaChi());
        txtUsername.setText(nv.getUsername()); txtPassword.setText(""); cboRole.setSelectedItem(nv.getRole());
    }

    public void clearForm() {
        txtMaNV.setText("Tự động sinh"); txtTenNV.setText(""); txtNgaySinh.setDate(null);
        btnGroupGender.clearSelection(); txtChucVu.setText(""); txtSDT.setText(""); txtDiaChi.setText("");
        txtUsername.setText(""); txtPassword.setText("");
    }

    private void initHiddenButtons() { btnSua = new JButton(); btnXoa = new JButton(); }
    public JTable getTable() { return table; }
    public JTextField getTxtSearch() { return txtSearch; }
    public JButton getBtnLuu() { return btnLuu; }
    public JDialog getDialogForm() { return dialogForm; }
    public void addThemListener(ActionListener al) { btnThem.addActionListener(al); }
    public void addSuaListener(ActionListener al) { btnSua.addActionListener(al); }
    public void addXoaListener(ActionListener al) { btnXoa.addActionListener(al); }
    public void setMaNV(String ma) { txtMaNV.setText(ma); }
    public void clearTable() { model.setRowCount(0); }
    public void addRow(Object[] row) { model.addRow(row); }

    class ModernBorder extends AbstractBorder {
        private int r; public ModernBorder(int r) { this.r = r; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(220, 220, 220)); g2.drawRoundRect(x, y, w-1, h-1, r, r); g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(r/2, r/2, r/2, r/2); }
    }
}