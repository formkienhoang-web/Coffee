package CH.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.AbstractCellEditor;

public class DatMonView extends JPanel {

    private JTable tableGioHang;
    private DefaultTableModel modelGioHang;

    private JButton btnThemMon, btnXoaMon, btnThanhToan;
    private JLabel lblTongTien;

    private JPanel pnlMenuGrid;

    // 🔥 THÊM MỚI
    private JComboBox<String> cbDanhMuc;
    private JTextField txtSearch;


    class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        private final JSpinner spinner;

        public SpinnerEditor() {
            spinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        }

        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            spinner.setValue(value);
            return spinner;
        }
    }

    public DatMonView() {
        setLayout(new GridLayout(1, 2, 10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // ================= TRÁI: THỰC ĐƠN =================
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setBorder(new TitledBorder("THỰC ĐƠN"));

        // 🔥 PANEL TOP (Danh mục + tìm kiếm)
        JPanel pnlTop = new JPanel(new BorderLayout(5,5));

        cbDanhMuc = new JComboBox<>();

        cbDanhMuc.addItem("Danh mục"); // mặc định
        cbDanhMuc.addItem("Tất cả");
        cbDanhMuc.setPreferredSize(new Dimension(160, 35));

        pnlTop.add(cbDanhMuc, BorderLayout.WEST);

        txtSearch = new JTextField();
        txtSearch.setBorder(BorderFactory.createTitledBorder("Tìm món..."));
        pnlTop.add(txtSearch, BorderLayout.CENTER);

        pnlLeft.add(pnlTop, BorderLayout.NORTH);

        // ===== GRID MENU =====
        pnlMenuGrid = new JPanel();
        pnlMenuGrid.setLayout(new GridLayout(0, 3, 8, 8));
        pnlMenuGrid.setBackground(Color.WHITE);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(pnlMenuGrid);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(10);

        pnlLeft.add(scroll, BorderLayout.CENTER);

        // ================= PHẢI: GIỎ HÀNG =================
        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setBorder(new TitledBorder("GIỎ HÀNG"));

        String[] colGio = {"Mã món", "Tên món", "SL", "Đơn giá", "Thành tiền"};
        modelGioHang = new DefaultTableModel(colGio, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Integer.class;
                return String.class;
            }
        };

        tableGioHang = new JTable(modelGioHang);
        tableGioHang.getColumnModel().getColumn(2).setCellEditor(new SpinnerEditor());
        tableGioHang.setRowHeight(30);

        modelGioHang.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();

            if (col == 2) {
                try {
                    int sl = Integer.parseInt(modelGioHang.getValueAt(row, 2).toString());
                    double gia = Double.parseDouble(modelGioHang.getValueAt(row, 3).toString().replace(",", ""));

                    double thanhTien = sl * gia;
                    modelGioHang.setValueAt(String.format("%,.0f", thanhTien), row, 4);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        pnlRight.add(new JScrollPane(tableGioHang), BorderLayout.CENTER);

        // ================= FOOTER =================
        JPanel pnlFooter = new JPanel(new GridLayout(2, 1));

        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ", SwingConstants.RIGHT);
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongTien.setForeground(Color.RED);
        lblTongTien.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnXoaMon = new JButton("XOÁ MÓN");
        btnXoaMon.setFont(new Font("Arial", Font.BOLD, 12));
        btnThanhToan = new JButton("THANH TOÁN");
        btnThanhToan.setBackground(new Color(255, 77, 77));
        btnThanhToan.setForeground(Color.BLACK);
        btnThanhToan.setFont(new Font("Arial", Font.BOLD, 12));

        pnlBtns.add(btnXoaMon);
        pnlBtns.add(btnThanhToan);

        pnlFooter.add(lblTongTien);
        pnlFooter.add(pnlBtns);

        pnlRight.add(pnlFooter, BorderLayout.SOUTH);

        add(pnlLeft);
        add(pnlRight);
    }

    // ================= MENU CARD =================
    public void addMonCard(String maMon, String ten, double gia, String hinhAnh) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setPreferredSize(new Dimension(150, 180));

        JLabel lblImg = new JLabel();
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon icon;
        try {
            icon = (hinhAnh != null && !hinhAnh.isEmpty())
                    ? new ImageIcon(hinhAnh)
                    : new ImageIcon("src/images/default.png");
        } catch (Exception e) {
            icon = new ImageIcon("src/images/default.png");
        }

        Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        lblImg.setIcon(new ImageIcon(img));

        JLabel lblTen = new JLabel(ten, SwingConstants.CENTER);
        lblTen.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel lblGia = new JLabel(String.format("%,.0f VNĐ", gia), SwingConstants.CENTER);
        lblGia.setForeground(new Color(0, 102, 204));
        lblGia.setFont(new Font("Arial", Font.BOLD, 13));

        JPanel info = new JPanel(new GridLayout(2,1));
        info.setBackground(Color.WHITE);
        info.add(lblTen);
        info.add(lblGia);

        card.add(lblImg, BorderLayout.CENTER);
        card.add(info, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                addMonToGio(maMon, ten, gia);
            }
        });

        pnlMenuGrid.add(card);
    }

    public void clearMenu() {
        pnlMenuGrid.removeAll();
        pnlMenuGrid.repaint();
    }

    // ===== GET =====
    public DefaultTableModel getModelGioHang() { return modelGioHang; }
    public JTable getTableGioHang() { return tableGioHang; }

    public JComboBox<String> getCbDanhMuc() { return cbDanhMuc; }
    public JTextField getTxtSearch() { return txtSearch; }

    public void setTongTien(double tien) {
        lblTongTien.setText("Tổng tiền: " + String.format("%,.0f VNĐ", tien));
    }

    public void addXoaListener(ActionListener al) { btnXoaMon.addActionListener(al); }
    public void addThanhToanListener(ActionListener al) { btnThanhToan.addActionListener(al); }

    // ================= GIỎ HÀNG =================
    public void addMonToGio(String maMon, String ten, double gia) {
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            if (modelGioHang.getValueAt(i, 0).equals(maMon)) {
                int sl = Integer.parseInt(modelGioHang.getValueAt(i, 2).toString());
                int slMoi = sl + 1;

                modelGioHang.setValueAt(slMoi, i, 2);
                modelGioHang.setValueAt(String.format("%,.0f", slMoi * gia), i, 4);
                return;
            }
        }

        modelGioHang.addRow(new Object[]{
                maMon,
                ten,
                1,
                String.format("%,.0f", gia),
                String.format("%,.0f", gia)
        });
    }
}