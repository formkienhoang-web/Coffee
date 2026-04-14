package CH.view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import CH.model.MonAn;

public class ThucDonView extends JPanel {
    private JTextField txtMaMon, txtTenMon, txtDonGia, txtDVT, txtSearch;
    private JComboBox<String> cboDanhMuc;
    private JLabel lblHinhAnh;
    private JButton btnChonAnh, btnThem, btnSua, btnXoa, btnReset;
    private String duongDanAnh = "";
    private JTable table;
    private DefaultTableModel model;
    private JDialog dialogForm;
    private JButton btnLuu;


    private final Color COLOR_PRIMARY = new Color(38, 70, 83);
    private final Color COLOR_ACCENT = new Color(42, 157, 143);
    private final Color COLOR_BG = Color.WHITE;

    public ThucDonView() {
        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Quản Lý Menu Cà Phê");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_PRIMARY);

        JLabel lblSubTitle = new JLabel("Quản lý danh sách các loại cà phê và đồ uống trong quán");
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubTitle.setForeground(Color.GRAY);
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSubTitle);

        // --- 2. ACTION BAR ---
        JPanel pnlAction = new JPanel(new BorderLayout(25,0));
        pnlAction.setOpaque(false);

        txtSearch = new JTextField("  🔍  Tìm kiếm theo tên món...") {
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

        btnThem = new JButton("+  Thêm Món") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(53, 79, 78));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Hình viên thuốc chuẩn
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

        // --- 3. TABLE VỚI HIỆU ỨNG HOVER ---
        String[] cols = {"Mã món", "Ảnh", "Tên món", "Giá", "ĐVT", "Danh mục", "Hành động"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int c) { return (c == 1) ? Icon.class : Object.class; }
            @Override
            public boolean isCellEditable(int r, int c) { return c == 6; }
        };

        table = new JTable(model) {
            private int hoveredRow = -1;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                // ✅ Anti-aliasing giống HoaDonView
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
                } else {
                    c.setForeground(new Color(60, 60, 60));
                }
                return c;
            }

            {
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
                }else if (column==1){
                    label.setHorizontalAlignment(JLabel.CENTER);
                }
                else {
                    label.setHorizontalAlignment(JLabel.LEFT);
                }

                // chỉ giữ 1 đường kẻ dưới
                label.setBorder(BorderFactory.createMatteBorder(
                        0, 0, 1, 0, new Color(240, 240, 240)));

                return label;
            }
        });

        table.getColumnModel().getColumn(6)
                .setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(6)
                .setCellEditor(new ActionButtonEditor(new JCheckBox()));
        table.getColumnModel().getColumn(0).setPreferredWidth(20);   // Mã món
        table.getColumnModel().getColumn(1).setPreferredWidth(80);   // Ảnh
        table.getColumnModel().getColumn(2).setPreferredWidth(160);  // Tên món
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Giá
        table.getColumnModel().getColumn(4).setPreferredWidth(60);   // ĐVT
        table.getColumnModel().getColumn(5).setPreferredWidth(100);  // Danh mục
        table.getColumnModel().getColumn(6).setPreferredWidth(90);   // Hành động
    }

    private void initDialogForm() {
        dialogForm = new JDialog();
        dialogForm.setModal(true);
        // Thu nhỏ chiều ngang xuống 400 và chiều cao vừa đủ 620
        dialogForm.setSize(400, 620);
        dialogForm.setLayout(new BorderLayout());
        dialogForm.getContentPane().setBackground(Color.WHITE);
        dialogForm.setLocationRelativeTo(null);

        // Header thu nhỏ lại
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 15));
        pnlHeader.setOpaque(false);
        JLabel lblHeaderTitle = new JLabel("Thêm Món Mới");
        lblHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlHeader.add(lblHeaderTitle);

        // Panel chính chứa Form
        JPanel pnlMainForm = new JPanel();
        pnlMainForm.setLayout(new BoxLayout(pnlMainForm, BoxLayout.Y_AXIS));
        pnlMainForm.setOpaque(false);
        // Giảm padding trái phải xuống 25 để form cân đối khi thu nhỏ
        pnlMainForm.setBorder(new EmptyBorder(5, 25, 10, 25));

        // Khởi tạo Component (Vẫn dùng các hàm helper bên dưới)
        txtMaMon = createModernTextField("Tự động", false);
        txtTenMon = createModernTextField("Nhập tên món...", true);
        txtDonGia = createModernTextField("0", true);
        txtDVT = createModernTextField("Ly, Cái...", true);
        cboDanhMuc = createModernComboBox(new String[]{"Cà phê", "Trà", "Bánh ngọt"});

        // Thêm các cụm Input (Khoảng cách giữa các cụm giảm xuống)
        addModernInput(pnlMainForm, "Mã món", txtMaMon, false);
        addModernInput(pnlMainForm, "Tên món", txtTenMon, true);
        addModernInput(pnlMainForm, "Giá (VND)", txtDonGia, true);
        addModernInput(pnlMainForm, "Đơn vị tính", txtDVT, true);
        addModernInput(pnlMainForm, "Danh mục", cboDanhMuc, true);

        // Phần chọn ảnh gọn hơn
        JPanel pnlUpload = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        pnlUpload.setOpaque(false);
        lblHinhAnh = new JLabel("Ảnh", SwingConstants.CENTER);
        lblHinhAnh.setPreferredSize(new Dimension(80, 80)); // Thu nhỏ khung ảnh
        lblHinhAnh.setBorder(new ModernBorder(8));

        btnChonAnh = new JButton("Chọn ảnh");
        btnChonAnh.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        pnlUpload.add(lblHinhAnh);
        pnlUpload.add(Box.createHorizontalStrut(15));
        pnlUpload.add(btnChonAnh);
        pnlMainForm.add(pnlUpload);

        // Nút Lưu ôm sát hơn
        btnLuu = createStyledButton("Xác nhận Lưu", COLOR_ACCENT);
        btnLuu.setPreferredSize(new Dimension(0, 45));

        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setOpaque(false);
        pnlFooter.setBorder(new EmptyBorder(10, 25, 20, 25));
        pnlFooter.add(btnLuu, BorderLayout.CENTER);

        dialogForm.add(pnlHeader, BorderLayout.NORTH);
        dialogForm.add(pnlMainForm, BorderLayout.CENTER);
        dialogForm.add(pnlFooter, BorderLayout.SOUTH);

        btnChonAnh.addActionListener(e -> chonAnh());
    }

    private void addInput(JPanel p, GridBagConstraints gbc, int y, String lbl, Component c) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; p.add(new JLabel(lbl), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.insets = new Insets(10, 10, 10, 0); p.add(c, gbc);
        gbc.insets = new Insets(10, 0, 10, 0);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setOpaque(false);
        return b;
    }

    private void setupSearchPlaceholder() {
        txtSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().contains("🔍")) { txtSearch.setText(""); txtSearch.setForeground(Color.BLACK); }
            }
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) { txtSearch.setText("  🔍  Tìm kiếm theo tên cà phê..."); txtSearch.setForeground(Color.GRAY); }
            }
        });
    }

    // --- RENDERER ĐỒNG BỘ MÀU HOVER ---
    // Thay thế Class ActionButtonRenderer
    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnEditIcon = new JButton("✎");
        private JButton btnDeleteIcon = new JButton("🗑");

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
            setOpaque(true);
            // Định dạng nhanh cho icon
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

    // Thay thế Class ActionButtonEditor để bắt được đúng nút nào được nhấn
    class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        private JButton btnEdit = new JButton("✎");
        private JButton btnDelete = new JButton("🗑");

        public ActionButtonEditor(JCheckBox cb) {
            super(cb);
            panel.setOpaque(true);
            setupIcon(btnEdit, COLOR_ACCENT);
            setupIcon(btnDelete, new Color(231, 76, 60));

            // Sự kiện Sửa
            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                btnSua.doClick(); // 🔥 GỌI CONTROLLER
            });

            // Sự kiện Xoá: Kích hoạt nút ẩn để Controller nhận được
            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                btnXoa.doClick(); // Kích hoạt sự kiện cho Controller
            });

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

    public MonAn getMonAnInfo() {
        double gia = 0;
        try { gia = Double.parseDouble(txtDonGia.getText().replace(",", "")); } catch(Exception e) {}
        return new MonAn(txtMaMon.getText(), txtTenMon.getText(), gia, txtDVT.getText(), duongDanAnh, cboDanhMuc.getSelectedItem().toString());
    }

    public void fillForm(MonAn m) {
        txtMaMon.setText(m.getMaMon()); txtTenMon.setText(m.getTenMon());
        txtDonGia.setText(String.format("%.0f", m.getDonGia()));
        txtDVT.setText(m.getDonViTinh());
        if (m.getTenDanhMuc() != null) cboDanhMuc.setSelectedItem(m.getTenDanhMuc());
        duongDanAnh = m.getHinhAnh();
        setHinhAnhToLabel(duongDanAnh);
    }

    public void clearForm() {
        txtMaMon.setText("Tự động"); txtTenMon.setText(""); txtDonGia.setText(""); txtDVT.setText("");
        duongDanAnh = ""; lblHinhAnh.setIcon(null); lblHinhAnh.setText("Chưa có ảnh");
    }

    private void chonAnh() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            duongDanAnh = fc.getSelectedFile().getAbsolutePath();
            setHinhAnhToLabel(duongDanAnh);
        }
    }

    public void setHinhAnhToLabel(String path) {
        if (path == null || path.isEmpty()) return;
        try {
            Image img = new ImageIcon(path).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            lblHinhAnh.setIcon(new ImageIcon(img)); lblHinhAnh.setText("");
        } catch(Exception e) { lblHinhAnh.setText("Lỗi ảnh"); }
    }

    public void addRow(MonAn m) {
        ImageIcon icon = null;
        if (m.getHinhAnh() != null && !m.getHinhAnh().isEmpty()) {
            Image img = new ImageIcon(m.getHinhAnh()).getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
        }
        model.addRow(new Object[]{m.getMaMon(), icon, m.getTenMon(), String.format("%,.0f đ", m.getDonGia()), m.getDonViTinh(), m.getTenDanhMuc(), ""});
    }

    private void initHiddenButtons() { btnSua = new JButton(); btnXoa = new JButton(); btnReset = new JButton(); }
    public JTable getTable() { return table; }
    public JComboBox<String> getCboDanhMuc() { return cboDanhMuc; }
    public void addThemListener(ActionListener al) { btnThem.addActionListener(al); }
    public void addSuaListener(ActionListener al) { btnSua.addActionListener(al); }
    public void addXoaListener(ActionListener al) { btnXoa.addActionListener(al); }
    public int getSelectedRow() { return table.getSelectedRow(); }
    public JDialog getDialogForm() {
        return dialogForm;
    }

    public JButton getBtnLuu() {
        return btnLuu;
    }
    public JTextField getTxtSearch() { return txtSearch; }
    public void clearTable() { model.setRowCount(0); }
    // Hàm tạo Label và Input xếp chồng
    // Hàm tạo cụm Label + Input
    private void addModernInput(JPanel container, String title, JComponent input, boolean required) {
        JPanel pnlGroup = new JPanel(new BorderLayout(0, 5)); // Giảm khoảng cách nhãn và ô nhập
        pnlGroup.setOpaque(false);
        pnlGroup.setBorder(new EmptyBorder(0, 0, 10, 0)); // Giảm khoảng cách giữa các cụm

        String labelText = required ? "<html>" + title + " <font color='red'>*</font></html>" : title;
        JLabel lblTitle = new JLabel(labelText);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Giảm font nhãn 1 tí
        lblTitle.setForeground(new Color(80, 80, 80));

        pnlGroup.add(lblTitle, BorderLayout.NORTH);
        pnlGroup.add(input, BorderLayout.CENTER);
        container.add(pnlGroup);
    }

    // Tạo TextField
    private JTextField createModernTextField(String hint, boolean editable) {
        JTextField tf = new JTextField();
        tf.setEditable(editable);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(0, 38)); // Giảm chiều cao xuống 38px cho gọn
        tf.setBorder(BorderFactory.createCompoundBorder(
                new ModernBorder(8),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        if (!editable) tf.setBackground(new Color(248, 248, 248));
        return tf;
    }

    // Tạo ComboBox
    private JComboBox createModernComboBox(String[] items) {
        JComboBox cb = new JComboBox(items);
        cb.setPreferredSize(new Dimension(0, 38)); // Chiều cao 38px khớp với TextField
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setBorder(new ModernBorder(8));
        return cb;
    }
    class ModernBorder extends AbstractBorder {
        private int radius;
        public ModernBorder(int radius) { this.radius = radius; }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }
    }
}