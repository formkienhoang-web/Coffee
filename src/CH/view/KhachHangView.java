package CH.view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import CH.model.KhachHang;

public class KhachHangView extends JPanel {
    private JTextField txtMaKH, txtTenKH, txtTheLoai, txtEmail, txtSDT, txtDiaChi, txtSearch;
    private JRadioButton rdoNam, rdoNu;
    private ButtonGroup btnGroupGender;
    private JTable table;
    private DefaultTableModel model;
    private JDialog dialogForm;
    private JButton btnThem, btnSua, btnXoa, btnLuu;

    private final Color COLOR_PRIMARY = new Color(38, 70, 83);
    private final Color COLOR_ACCENT = new Color(42, 157, 143);
    private final Color COLOR_BG = Color.WHITE;

    public KhachHangView() {
        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Quản Lý Khách Hàng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_PRIMARY);

        JLabel lblSubTitle = new JLabel("Theo dõi thông tin, phân loại và lịch sử khách hàng");
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubTitle.setForeground(Color.GRAY);
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSubTitle);

        // --- 2. ACTION BAR ---
        JPanel pnlAction = new JPanel(new BorderLayout(25, 0));
        pnlAction.setOpaque(false);

        txtSearch = createSearchField();
        btnThem = createPillButton("+  Thêm Khách Hàng", new Color(53, 79, 78));
        btnThem.setPreferredSize(new Dimension(180, 45));
        pnlAction.add(txtSearch, BorderLayout.WEST);
        pnlAction.add(btnThem, BorderLayout.EAST);

        JPanel pnlTop = new JPanel(new BorderLayout(0, 25));
        pnlTop.setOpaque(false);
        pnlTop.add(pnlHeader, BorderLayout.NORTH);
        pnlTop.add(pnlAction, BorderLayout.CENTER);

        // --- 3. TABLE VỚI HIỆU ỨNG NHÁY CHUỘT ---
        String[] cols = {"Mã KH", "Họ tên", "Loại KH", "Giới tính", "Email", "SĐT", "Địa chỉ", "Hành động"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return c == 7; }
        };

        table = new JTable(model) {
            private int hoveredRow = -1;
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                c.setBackground(Color.WHITE);

                if (row == hoveredRow) c.setBackground(new Color(245, 248, 250));

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

        // Khởi tạo các thành phần ẩn
        btnSua = new JButton(); btnXoa = new JButton();
        initDialogForm();
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

        // Cột Hành động
        table.getColumnModel().getColumn(7).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new ActionButtonEditor(new JCheckBox()));
    }

    private void initDialogForm() {
        dialogForm = new JDialog();
        dialogForm.setModal(true);
        dialogForm.setSize(450, 600);
        dialogForm.setLayout(new BorderLayout());
        dialogForm.getContentPane().setBackground(Color.WHITE);
        dialogForm.setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setBorder(new EmptyBorder(25, 35, 10, 35));
        pnlMain.setOpaque(false);

        txtMaKH = createModernTextField("Tự động", false);
        txtTenKH = createModernTextField("Nhập tên khách hàng...", true);
        txtTheLoai = createModernTextField("Loại khách hàng...", true);

        rdoNam = new JRadioButton("Nam"); rdoNu = new JRadioButton("Nữ");
        btnGroupGender = new ButtonGroup(); btnGroupGender.add(rdoNam); btnGroupGender.add(rdoNu);
        JPanel pnlGender = new JPanel(new FlowLayout(FlowLayout.LEFT)); pnlGender.setOpaque(false);
        pnlGender.add(rdoNam); pnlGender.add(rdoNu);

        txtEmail = createModernTextField("Email...", true);
        txtSDT = createModernTextField("Số điện thoại...", true);
        txtDiaChi = createModernTextField("Địa chỉ...", true);

        addModernInput(pnlMain, "Mã khách hàng", txtMaKH, false);
        addModernInput(pnlMain, "Họ và tên", txtTenKH, true);
        addModernInput(pnlMain, "Phân loại(Vip/ Khách vãng lai)", txtTheLoai, true);
        addModernInput(pnlMain, "Giới tính", pnlGender, true);
        addModernInput(pnlMain, "Email", txtEmail, true);
        addModernInput(pnlMain, "Số điện thoại", txtSDT, true);
        addModernInput(pnlMain, "Địa chỉ", txtDiaChi, true);

        btnLuu = createStyledButton("Xác nhận Lưu", COLOR_ACCENT);
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setOpaque(false); pnlFooter.setBorder(new EmptyBorder(10, 35, 30, 35));
        pnlFooter.add(btnLuu, BorderLayout.CENTER);

        dialogForm.add(pnlMain, BorderLayout.CENTER);
        dialogForm.add(pnlFooter, BorderLayout.SOUTH);
    }

    // --- RENDERER & EDITOR CHO ACTION COLUMN ---
    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        public ActionButtonRenderer() { setLayout(new FlowLayout(FlowLayout.LEFT, 10, 12)); setOpaque(true); }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            setBackground(t.prepareRenderer(t.getDefaultRenderer(Object.class), r, c).getBackground());
            removeAll();
            add(createIconBtn("✎", COLOR_ACCENT));
            add(createIconBtn("🗑", new Color(231, 76, 60)));
            return this;
        }
    }

    class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
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

    // --- CÁC PHƯƠNG THỨC HỖ TRỢ GIAO DIỆN ---
    private JTextField createSearchField() {
        JTextField tf = new JTextField("  🔍  Tìm tên hoặc mã khách hàng...") {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tf.setPreferredSize(new Dimension(780, 45));
        tf.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        tf.setForeground(Color.GRAY);
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if (tf.getText().contains("🔍")) { tf.setText(""); tf.setForeground(Color.BLACK); } }
            public void focusLost(FocusEvent e) { if (tf.getText().isEmpty()) { tf.setText("  🔍  Tìm tên hoặc mã khách hàng..."); tf.setForeground(Color.GRAY); } }
        });
        return tf;
    }

    private JButton createPillButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        b.setPreferredSize(new Dimension(200, 45)); b.setContentAreaFilled(false); b.setBorderPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton createIconBtn(String icon, Color c) {
        JButton b = new JButton(icon); b.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
        b.setForeground(c); b.setBorderPainted(false); b.setContentAreaFilled(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void addModernInput(JPanel container, String title, JComponent input, boolean req) {
        JPanel p = new JPanel(new BorderLayout(0, 5)); p.setOpaque(false); p.setBorder(new EmptyBorder(0, 0, 12, 0));
        JLabel l = new JLabel(req ? "<html>" + title + " <font color='red'>*</font></html>" : title);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13)); l.setForeground(new Color(80, 80, 80));
        p.add(l, BorderLayout.NORTH); p.add(input, BorderLayout.CENTER);
        container.add(p);
    }

    private JTextField createModernTextField(String hint, boolean edit) {
        JTextField tf = new JTextField(); tf.setEditable(edit);
        tf.setPreferredSize(new Dimension(0, 38));
        tf.setBorder(BorderFactory.createCompoundBorder(new ModernBorder(8), BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        if (!edit) tf.setBackground(new Color(248, 248, 248));
        return tf;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground()); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
                super.paintComponent(g); g2.dispose();
            }
        };
        b.setBackground(bg); b.setForeground(Color.WHITE); b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setContentAreaFilled(false); b.setBorderPainted(false); return b;
    }

    class ModernBorder extends AbstractBorder {
        private int r; public ModernBorder(int r) { this.r = r; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(220, 220, 220)); g2.drawRoundRect(x, y, w-1, h-1, r, r); g2.dispose();
        }
        public Insets getBorderInsets(Component c) { return new Insets(r/2, r/2, r/2, r/2); }
    }

    // --- GETTERS & LOGIC ---
    public JTable getTable() { return table; }
    public JDialog getDialogForm() { return dialogForm; }
    public JButton getBtnLuu() { return btnLuu; }
    public JButton getBtnThem() { return btnThem; }
    public JButton getBtnSua() { return btnSua; }
    public JButton getBtnXoa() { return btnXoa; }
    public JTextField getTxtSearch() { return txtSearch; }

    public void addRow(KhachHang kh) {
        model.addRow(new Object[]{
                kh.getMaKH(), kh.getTenKH(), kh.getTheLoai(), kh.getGioiTinh(),
                kh.getEmail(), kh.getSoDienThoai(), kh.getDiaChi(), ""
        });
    }

    public void fillForm(KhachHang kh) {
        txtMaKH.setText(kh.getMaKH()); txtTenKH.setText(kh.getTenKH());
        txtTheLoai.setText(kh.getTheLoai());
        if ("Nam".equalsIgnoreCase(kh.getGioiTinh())) rdoNam.setSelected(true); else rdoNu.setSelected(true);
        txtEmail.setText(kh.getEmail()); txtSDT.setText(kh.getSoDienThoai()); txtDiaChi.setText(kh.getDiaChi());
    }

    public void clearForm() {
        txtMaKH.setText("Tự động"); txtTenKH.setText(""); txtTheLoai.setText("");
        btnGroupGender.clearSelection(); txtEmail.setText(""); txtSDT.setText(""); txtDiaChi.setText("");
    }
    // 1. Lấy dữ liệu từ Form trong Dialog để tạo đối tượng KhachHang
    public KhachHang getKhachHangInfo() {
        String ma = txtMaKH.getText();
        String ten = txtTenKH.getText();
        String loai = txtTheLoai.getText();
        String gt = rdoNam.isSelected() ? "Nam" : "Nữ";
        String email = txtEmail.getText();
        String sdt = txtSDT.getText();
        String diaChi = txtDiaChi.getText();

        return new KhachHang(ma, ten, loai, gt, email, sdt, diaChi);
    }

    // 2. Xóa sạch bảng để load lại (tránh bị lặp dữ liệu khi search hoặc reload)
    public void clearTable() {
        model.setRowCount(0);
    }

    // 3. Hiển thị mã khách hàng lên Form (dùng khi bấm "Thêm")
    public void setMaKH(String ma) {
        txtMaKH.setText(ma);
    }
}