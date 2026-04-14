package CH.view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import CH.model.HoaDon;

public class HoaDonView extends JPanel {
    private JTextField txtSearch;
    private JTable tableHoaDon;
    private DefaultTableModel tableModel;
    private JButton btnTriggerXem; // Nút ẩn để kích hoạt sự kiện cho Controller

    // Bảng màu đồng bộ với ThucDonView
    private final Color COLOR_PRIMARY = new Color(38, 70, 83);
    private final Color COLOR_ACCENT = new Color(42, 157, 143);
    private final Color COLOR_BG = Color.WHITE;

    public HoaDonView() {
        // Layout BorderLayout với khoảng cách giống ThucDonView để không bị nháy khi chuyển Tab
        setLayout(new BorderLayout(0, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        initUI();
    }

    private void initUI() {
        // --- 1. HEADER (Giống ThucDonView) ---
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Lịch Sử Hóa Đơn");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_PRIMARY);

        JLabel lblSubTitle = new JLabel("Tra cứu danh sách và chi tiết các hóa đơn ");
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubTitle.setForeground(Color.GRAY);
        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSubTitle);

        // --- 2. ACTION BAR (Thanh tìm kiếm) ---
        JPanel pnlAction = new JPanel(new BorderLayout());
        pnlAction.setOpaque(false);

        txtSearch = new JTextField("  🔍  Tìm kiếm mã hóa đơn hoặc tên khách...") {
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
        setupSearchPlaceholder();

        pnlAction.add(txtSearch, BorderLayout.WEST);

        JPanel pnlTop = new JPanel(new BorderLayout(0, 25));
        pnlTop.setOpaque(false);
        pnlTop.add(pnlHeader, BorderLayout.NORTH);
        pnlTop.add(pnlAction, BorderLayout.CENTER);

        // --- 3. TABLE VỚI HIỆU ỨNG HOVER ---
        String[] cols = {"Mã hóa đơn", "Nhân viên lập", "Khách hàng", "Ngày lập", "Tổng tiền", "Chi tiết"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return c == 5; } // Chỉ cho click cột Chi tiết
        };

        tableHoaDon = new JTable(tableModel) {
            private int hoveredRow = -1;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                c.setBackground(Color.WHITE);

                // Khử răng cưa cho chữ
                if (c instanceof JComponent) {
                    ((JComponent) c).putClientProperty(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                }

                // Hiệu ứng Hover (Nháy nhẹ giống ThucDonView)
                if (row == hoveredRow) {
                    c.setBackground(new Color(245, 248, 250));
                }

                // Hiệu ứng Selection
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

        JScrollPane scrollPane = new JScrollPane(tableHoaDon);
        scrollPane.setBorder(new LineBorder(new Color(245, 245, 245)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(pnlTop, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnTriggerXem = new JButton(); // Nút ẩn
    }

    private void setupTableStyle() {
        tableHoaDon.setRowHeight(60);
        tableHoaDon.setShowGrid(false);
        tableHoaDon.setIntercellSpacing(new Dimension(0, 0));
        tableHoaDon.setFocusable(false);
        tableHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Header Style (Không kẻ dọc)
        JTableHeader header = tableHoaDon.getTableHeader();
        header.setPreferredSize(new Dimension(0, 50));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setBackground(new Color(242, 245, 248));
                label.setForeground(Color.GRAY);
                if (column == 5) {
                    label.setHorizontalAlignment(JLabel.CENTER);
                } else {
                    label.setHorizontalAlignment(JLabel.LEFT);
                }
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));
                return label;
            }
        });

        // Cột Action: Chỉ có icon xem chi tiết
        tableHoaDon.getColumnModel().getColumn(5).setCellRenderer(new ActionRenderer());
        tableHoaDon.getColumnModel().getColumn(5).setCellEditor(new ActionEditor(new JCheckBox()));
        tableHoaDon.getColumnModel().getColumn(1).setPreferredWidth(180); // Họ tên
        tableHoaDon.getColumnModel().getColumn(2).setPreferredWidth(120); // Loại KH
    }

    private void setupSearchPlaceholder() {
        txtSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().contains("🔍")) { txtSearch.setText(""); txtSearch.setForeground(Color.BLACK); }
            }
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) { txtSearch.setText("  🔍  Tìm kiếm mã hóa đơn hoặc tên khách..."); txtSearch.setForeground(Color.GRAY); }
            }
        });
    }

    // --- Renderer cho nút Xem chi tiết (👁) ---
    class ActionRenderer extends JPanel implements TableCellRenderer {
        private JButton btnView = new JButton("👁");
        public ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
            setOpaque(true);
            btnView.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
            btnView.setForeground(COLOR_ACCENT);
            btnView.setBorderPainted(false);
            btnView.setContentAreaFilled(false);
            add(btnView);
        }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            setBackground(t.prepareRenderer(t.getDefaultRenderer(Object.class), r, c).getBackground());
            return this;
        }
    }

    // --- Editor cho nút Xem chi tiết (👁) ---
    class ActionEditor extends DefaultCellEditor {
        private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        private JButton btnView = new JButton("👁");
        public ActionEditor(JCheckBox cb) {
            super(cb);
            panel.setOpaque(true);
            btnView.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
            btnView.setForeground(COLOR_ACCENT);
            btnView.setBorderPainted(false);
            btnView.setContentAreaFilled(false);
            btnView.addActionListener(e -> {
                fireEditingStopped();
                btnTriggerXem.doClick(); // Kích hoạt sự kiện xem chi tiết cho Controller
            });
            panel.add(btnView);
        }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            panel.setBackground(t.getSelectionBackground());
            return panel;
        }
        @Override public Object getCellEditorValue() { return ""; }
    }

    // Các hàm công khai cho Controller
    public void addRow(HoaDon hd) {
        tableModel.addRow(new Object[]{
                hd.getMaHD(),
                hd.getTenNV(),
                hd.getTenKH(),
                hd.getNgayLap(),
                String.format("%,.0f đ", hd.getTongTien()),
                ""
        });
    }

    public void clearTable() { tableModel.setRowCount(0); }
    public int getSelectedRow() { return tableHoaDon.getSelectedRow(); }
    public JTable getTable() { return tableHoaDon; }
    public JTextField getTxtSearch() { return txtSearch; }
    public void addXemChiTietListener(ActionListener al) { btnTriggerXem.addActionListener(al); }
}