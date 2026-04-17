package CH.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DoanhThuView extends JPanel {
    private JTable doanhThuTable;
    private DefaultTableModel tableModel;
    private JTextField txtTuNgay, txtDenNgay;
    private JButton btnXemBaoCao;
    private JLabel lblValueTong;

    // Hệ màu Dashboard chuyên nghiệp (Đồng bộ COLOR_PRIMARY từ HoaDonView)
    private final Color COLOR_PRIMARY = new Color(38, 70, 83);   // Xanh đen sâu
    private final Color COLOR_ACCENT = new Color(42, 157, 143);    // Xanh ngọc
    private final Color COLOR_BG = new Color(248, 249, 250);     // Xám trắng nhạt
    private final Color COLOR_HOVER = new Color(245, 248, 250);  // Màu nháy chuột
    private final Color COLOR_CARD = Color.WHITE;

    public DoanhThuView() {
        // Sử dụng BorderLayout với khoảng cách lề lớn để tạo độ thông thoáng
        setLayout(new BorderLayout(0, 25));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        initUI();
    }

    private void initUI() {
        // --- 1. HEADER & DASHBOARD CARDS ---
        JPanel pnlTop = new JPanel(new BorderLayout(0, 20));
        pnlTop.setOpaque(false);

        // Tiêu đề trang (Sử dụng Font Segoe UI hiện đại giống HoaDonView)
        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlTitle.setOpaque(false);
        JLabel lblTitle = new JLabel("Báo Cáo Doanh Thu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_PRIMARY);
        JLabel lblSub = new JLabel("Theo dõi hiệu quả kinh doanh và dòng tiền theo thời gian");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);

        // Thẻ thống kê (Stat Card) giúp con số quan trọng nổi bật nhất
        JPanel pnlSummary = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlSummary.setOpaque(false);
        pnlSummary.add(createStatCard("DOANH THU TRONG KỲ"));
        pnlTop.add(pnlTitle, BorderLayout.NORTH);
        pnlTop.add(pnlSummary, BorderLayout.CENTER);

        // --- 2. THANH CÔNG CỤ (FILTERS) ---
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlFilter.setOpaque(false);
        JLabel lblFilterTitle = new JLabel("Khoảng thời gian:");
// Sử dụng font Segoe UI, Plain, cỡ 14 (giống style Subtitle bạn đang dùng)
        lblFilterTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFilterTitle.setForeground(new Color(60, 60, 60)); // Màu xám đậm cho chuyên nghiệp

        txtTuNgay = createStyledTextField("01/01/2025");
        txtDenNgay = createStyledTextField("31/12/2025");
        btnXemBaoCao = new JButton("Xem báo cáo");
        stylePrimaryButton(btnXemBaoCao);

        pnlFilter.add(lblFilterTitle);
        pnlFilter.add(txtTuNgay);
        pnlFilter.add(txtDenNgay);
        pnlFilter.add(btnXemBaoCao);

        pnlTop.add(pnlFilter, BorderLayout.SOUTH);

        // --- 3. BẢNG DỮ LIỆU (ĐỒNG BỘ HIỆU ỨNG VỚI HOADONVIEW) ---
        String[] headers = {"Ngày Giao Dịch", "Doanh Thu Thu Về"};
        tableModel = new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        doanhThuTable = new JTable(tableModel) {
            private int hoveredRow = -1;
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(Color.WHITE);
                if (c instanceof JComponent) {
                    ((JComponent) c).putClientProperty(
                            RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                    );
                }

                // ✅ Đồng bộ font
                c.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                c.setBackground(Color.WHITE);

                // Hiệu ứng Hover nháy nhẹ giống HoaDonView
                if (row == hoveredRow) c.setBackground(COLOR_HOVER);

                // Hiệu ứng Selection màu xanh ngọc
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
                    @Override public void mouseMoved(MouseEvent e) {
                        int row = rowAtPoint(e.getPoint());
                        if (row != hoveredRow) { hoveredRow = row; repaint(); }
                    }
                    @Override public void mouseExited(MouseEvent e) { hoveredRow = -1; repaint(); }
                };
                addMouseMotionListener(ma);
                addMouseListener(ma);
            }
        };

        setupTableStyle();

        JScrollPane scrollPane = new JScrollPane(doanhThuTable);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(pnlTop, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Hàm tạo Stat Card chuyên nghiệp
    private JPanel createStatCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setPreferredSize(new Dimension(500, 110));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(Color.GRAY);

        lblValueTong = new JLabel("0 VNĐ");
        lblValueTong.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValueTong.setForeground(COLOR_ACCENT); // Sử dụng màu xanh ngọc làm điểm nhấn

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValueTong, BorderLayout.CENTER);
        return card;
    }

    private void setupTableStyle() {
        doanhThuTable.setRowHeight(50); // Cao hơn để bảng thoáng hơn
        doanhThuTable.setShowGrid(false);
        doanhThuTable.setIntercellSpacing(new Dimension(0, 0));
        doanhThuTable.setFocusable(false);
        doanhThuTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JTableHeader header = doanhThuTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 50));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setBackground(new Color(242, 245, 248));
                label.setForeground(Color.GRAY);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
                return label;
            }
        });
    }

    private JTextField createStyledTextField(String text) {
        JTextField tf = new JTextField(text, 12) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 230)); // Màu viền xám nhạt
                // Vẽ bo góc 15px để giống trang Hóa đơn
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        tf.setPreferredSize(new Dimension(150, 35));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Dùng EmptyBorder để chữ không dính sát viền bo
        tf.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        return tf;
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setPreferredSize(new Dimension(130, 35));
        btn.setBackground(new Color(53, 79, 78)); // COLOR_PRIMARY
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false); // Tắt nền mặc định để tự vẽ
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                // Bo góc 15px cho đồng bộ với ô nhập liệu
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 15, 15);
                g2.dispose();
                super.paint(g, c);
            }
        });
        btn.setBorder(BorderFactory.createEmptyBorder());
    }

    // Getters đồng bộ hoàn toàn với DoanhThuController
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTextField getTxtTuNgay() { return txtTuNgay; }
    public JTextField getTxtDenNgay() { return txtDenNgay; }
    public JButton getBtnXemBaoCao() { return btnXemBaoCao; }
    public JLabel getLblTongDoanhThu() { return lblValueTong; }
}