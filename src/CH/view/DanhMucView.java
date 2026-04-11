package CH.view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class DanhMucView extends JPanel {
    private JTextField txtMaDM, txtTenDM, txtSearch;
    private JTable table;
    private DefaultTableModel model;
    private JDialog dialogForm;
    private JButton btnThem, btnSua, btnXoa, btnLuu;

    // Đồng bộ bảng màu từ ThucDonView
    private final Color COLOR_PRIMARY = new Color(38, 70, 83);
    private final Color COLOR_ACCENT = new Color(42, 157, 143);
    private final Color COLOR_BG = Color.WHITE;

    public DanhMucView() {
        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Quản Lý Danh Mục");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_PRIMARY);

        JLabel lblSubTitle = new JLabel("Quản lý các nhóm phân loại sản phẩm trong hệ thống");
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubTitle.setForeground(Color.GRAY);
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSubTitle);

        // --- 2. ACTION BAR ---
        JPanel pnlAction = new JPanel(new BorderLayout(25, 0));
        pnlAction.setOpaque(false);

        txtSearch = new JTextField("  🔍  Tìm kiếm danh mục...") {
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

        btnThem = new JButton("+  Thêm Danh Mục") {
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

        // --- 3. TABLE VỚI HIỆU ỨNG HOVER Y HỆT THUCDON ---
        String[] headers = {"Mã danh mục", "Tên danh mục", "Hành động"};
        model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return c == 2; }
        };
        table = new JTable(model) {
            private int hoveredRow = -1;
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(Color.WHITE);
                if (row == hoveredRow) c.setBackground(new Color(245, 248, 250)); // Màu nháy xanh nhạt
                if (isRowSelected(row)) {
                    c.setBackground(new Color(204, 243, 229));
                    c.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                    c.setForeground(new Color(0, 105, 92));
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
            {
                MouseAdapter ma = new MouseAdapter() {
                    @Override public void mouseMoved(MouseEvent e) {
                        int row = rowAtPoint(e.getPoint());
                        if (row != hoveredRow) { hoveredRow = row; repaint(); }
                    }
                    @Override public void mouseExited(MouseEvent e) { hoveredRow = -1; repaint(); }
                };
                addMouseMotionListener(ma); addMouseListener(ma);
            }
        };
        setupTableStyle();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(245, 245, 245)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(pnlTop, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        initDialogForm();
        btnSua = new JButton(); btnXoa = new JButton(); // Nút ẩn cho Controller
        setupSearchPlaceholder();
    }

    private void setupTableStyle() {
        table.setRowHeight(60);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFocusable(false);
        table.setFont(new Font("Segoe UI", Font.ITALIC, 14));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(Color.GRAY);
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));

        table.getColumnModel().getColumn(2).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(new ActionButtonEditor(new JCheckBox()));
    }

    private void initDialogForm() {
        dialogForm = new JDialog();
        dialogForm.setModal(true);
        dialogForm.setSize(400, 350);
        dialogForm.setLayout(new BorderLayout());
        dialogForm.getContentPane().setBackground(Color.WHITE);
        dialogForm.setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setBorder(new EmptyBorder(20, 25, 10, 25));
        pnlMain.setOpaque(false);

        txtMaDM = createModernTextField("Tự động sinh", false);
        txtTenDM = createModernTextField("Nhập tên danh mục...", true);

        addModernInput(pnlMain, "Mã danh mục", txtMaDM, false);
        addModernInput(pnlMain, "Tên danh mục", txtTenDM, true);

        btnLuu = createStyledButton("Xác nhận Lưu", COLOR_ACCENT);
        btnLuu.setPreferredSize(new Dimension(0, 45));

        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBorder(new EmptyBorder(10, 25, 20, 25));
        pnlFooter.setOpaque(false);
        pnlFooter.add(btnLuu, BorderLayout.CENTER);

        dialogForm.add(pnlMain, BorderLayout.CENTER);
        dialogForm.add(pnlFooter, BorderLayout.SOUTH);
    }

    // --- HELPER METHODS ---
    private void addModernInput(JPanel container, String title, JComponent input, boolean required) {
        JPanel pnlGroup = new JPanel(new BorderLayout(0, 5));
        pnlGroup.setOpaque(false);
        pnlGroup.setBorder(new EmptyBorder(0, 0, 15, 0));
        String text = required ? "<html>" + title + " <font color='red'>*</font></html>" : title;
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlGroup.add(lbl, BorderLayout.NORTH);
        pnlGroup.add(input, BorderLayout.CENTER);
        container.add(pnlGroup);
    }

    private JTextField createModernTextField(String hint, boolean editable) {
        JTextField tf = new JTextField();
        tf.setEditable(editable);
        tf.setPreferredSize(new Dimension(0, 38));
        tf.setBorder(BorderFactory.createCompoundBorder(new ModernBorder(8), BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        if (!editable) tf.setBackground(new Color(248, 248, 248));
        return tf;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
                super.paintComponent(g); g2.dispose();
            }
        };
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setContentAreaFilled(false); b.setBorderPainted(false);
        return b;
    }

    private void setupSearchPlaceholder() {
        txtSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().contains("🔍")) { txtSearch.setText(""); txtSearch.setForeground(Color.BLACK); }
            }
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) { txtSearch.setText("  🔍  Tìm kiếm danh mục..."); txtSearch.setForeground(Color.GRAY); }
            }
        });
    }

    // --- ICON BUTTONS (SỬA & XÓA) TO VÀ CÓ HIỆU ỨNG NHÁY ---
    private JButton createIconBtn(String icon, Color color) {
        JButton b = new JButton(icon);
        b.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18)); // Font to rõ
        b.setForeground(color);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setForeground(color.brighter()); } // Nháy sáng
            @Override public void mouseExited(MouseEvent e) { b.setForeground(color); }
        });
        return b;
    }

    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        public ActionButtonRenderer() { setLayout(new FlowLayout(FlowLayout.LEFT, 15, 12)); setOpaque(true); }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            removeAll();
            setBackground(s ? t.getSelectionBackground() : (r % 2 == 0 ? Color.WHITE : Color.WHITE));
            // Cập nhật background theo màu hover của row
            Component rowComp = t.prepareRenderer(t.getDefaultRenderer(Object.class), r, c);
            setBackground(rowComp.getBackground());

            add(createIconBtn("✎", COLOR_ACCENT));
            add(createIconBtn("🗑", new Color(231, 76, 60)));
            return this;
        }
    }

    class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        public ActionButtonEditor(JCheckBox cb) {
            super(cb); panel.setOpaque(true);
            JButton e = createIconBtn("✎", COLOR_ACCENT);
            JButton d = createIconBtn("🗑", new Color(231, 76, 60));
            e.addActionListener(al -> { fireEditingStopped(); btnSua.doClick(); });
            d.addActionListener(al -> { fireEditingStopped(); btnXoa.doClick(); });
            panel.add(e); panel.add(d);
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            panel.setBackground(t.getSelectionBackground()); return panel;
        }
        public Object getCellEditorValue() { return ""; }
    }

    // Modern Border
    class ModernBorder extends AbstractBorder {
        private int r; public ModernBorder(int r) { this.r = r; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(220, 220, 220)); g2.drawRoundRect(x, y, w-1, h-1, r, r); g2.dispose();
        }
        public Insets getBorderInsets(Component c) { return new Insets(r/2, r/2, r/2, r/2); }
    }

    // Getters
    public String getMaDM() { return txtMaDM.getText(); }
    public String getTenDM() { return txtTenDM.getText(); }
    public JTable getTable() { return table; }
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JButton getBtnLuu() { return btnLuu; }
    public JDialog getDialogForm() { return dialogForm; }
    public JTextField getTxtSearch() { return txtSearch; }
    public String getTuKhoaTimKiem() { return txtSearch.getText().replace("🔍", "").trim(); }
    public void addRow(Object[] row) { model.addRow(new Object[]{row[0], row[1], ""}); }
    public void clearTable() { model.setRowCount(0); }
    public void setForm(String ma, String ten) { txtMaDM.setText(ma); txtTenDM.setText(ten); }
    public void clearForm() { txtMaDM.setText("Tự động sinh"); txtTenDM.setText(""); }
}