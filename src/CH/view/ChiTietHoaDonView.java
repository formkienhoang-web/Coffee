package CH.view;

import CH.model.ChiTietHoaDon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ChiTietHoaDonView extends JDialog {

    private JTable tableChiTiet;
    private DefaultTableModel tableModel;
    private JLabel lblMaHD, lblKhachHang, lblTongTien;

    public ChiTietHoaDonView(JFrame parent) {
        super(parent, "Chi tiết hóa đơn", true); // true = Modal (bắt buộc đóng mới thao tác tiếp đc)

        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Chi tiết hóa đơn", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(0, 77, 77));

        lblMaHD = new JLabel("Mã HĐ: ...");
        lblMaHD.setFont(new Font("Segoe UI", Font.ITALIC, 14));

        pnlHeader.add(lblTitle, BorderLayout.CENTER);
        pnlHeader.add(lblMaHD, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // --- TABLE ---
        String[] columns = {"Tên món ăn", "Size", "Số lượng", "Đơn giá", "Thành tiền"};
        tableModel = new DefaultTableModel(columns, 0);

        tableChiTiet = new JTable(tableModel);
        tableChiTiet.setRowHeight(30);
        tableChiTiet.getTableHeader().setBackground(new Color(230, 230, 230));
        tableChiTiet.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(tableChiTiet);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new EmptyBorder(10, 20, 10, 20));

        add(scrollPane, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel pnlFooter = new JPanel(new GridLayout(1, 2));
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(new EmptyBorder(15, 20, 15, 20));

        lblKhachHang = new JLabel("Khách hàng: ...");

        lblTongTien = new JLabel("Tổng tiền: ...", SwingConstants.RIGHT);
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 14));

        pnlFooter.add(lblKhachHang);
        pnlFooter.add(lblTongTien);

        add(pnlFooter, BorderLayout.SOUTH);
    }

    // Hàm để Controller gọi hiển thị dữ liệu
    public void setDetails(String maHD, String tenKH, double tongTien, List<ChiTietHoaDon> listChiTiet) {

        lblMaHD.setText("Mã hóa đơn: " + maHD);
        lblKhachHang.setText("Khách hàng: " + tenKH);

        tableModel.setRowCount(0);

        double tongTienTinhToan = 0;

        for (ChiTietHoaDon item : listChiTiet) {

            Object[] row = item.toObjectArray(); // 🔥 chỉ gọi 1 lần

            tableModel.addRow(row);

            try {
                double thanhTien = Double.parseDouble(
                        row[4].toString().replace(",", "")
                );
                tongTienTinhToan += thanhTien;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        lblTongTien.setText("Tổng tiền: " + String.format("%,.0f VNĐ", tongTienTinhToan));
    }
}