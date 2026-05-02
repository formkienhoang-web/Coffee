package CH.controller;

import CH.dao.*;
import CH.model.*;
import CH.view.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DatMonController {
    private DatMonView view;
    private ThucDonDAO menuDao;
    private HoaDonDAO hoaDonDao;
    private double currentTotal = 0;
    private HoaDonController hoaDonController;
    private KhachHangDAO khachHangDAO;
    private KhachHangController khachHangController;

    public DatMonController(DatMonView view, HoaDonController hoaDonController) {
        this.view = view;
        this.hoaDonController = hoaDonController;
        this.menuDao = new ThucDonDAO();
        this.hoaDonDao = new HoaDonDAO();
        this.khachHangDAO = new KhachHangDAO();

        view.clearCart();
        loadMenu();
        loadDanhMuc();

        // Gắn sự kiện cho các nút chức năng
        view.addXoaListener(e -> xoaHetGioHang()); // Đổi sang xóa hết vì mỗi dòng đã có nút xóa riêng
        view.addThanhToanListener(e -> moPopupThanhToan());

        // Lọc theo danh mục
        view.getCbDanhMuc().addActionListener(e -> loadMenu());

        // Tìm kiếm realtime
        view.getTxtSearch().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loadMenu(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loadMenu(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadMenu(); }
        });
    }

    private void loadDanhMuc() {
        JComboBox<String> cb = view.getCbDanhMuc();
        cb.removeAllItems();
        cb.addItem("Danh mục");
        cb.addItem("Tất cả");

        DanhMucDAO dao = new DanhMucDAO();
        for (String[] dm : dao.getAll()) {
            cb.addItem(dm[1]);
        }
        cb.setSelectedIndex(0);
    }

    private String removeAccent(String text) {
        if (text == null) return "";
        return java.text.Normalizer
                .normalize(text, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    public void loadMenu() {
        view.clearMenu();
        List<MonAn> list = menuDao.getAll();
        String keyword = view.getTxtSearch().getText().trim();

        if (keyword.equalsIgnoreCase("Tìm món...")) {
            keyword = "";
        }
        String danhMuc = view.getCbDanhMuc().getSelectedItem() != null
                ? view.getCbDanhMuc().getSelectedItem().toString()
                : "Danh mục";

        for (MonAn m : list) {
            boolean matchName = removeAccent(m.getTenMon()).contains(removeAccent(keyword));
            boolean matchDanhMuc = danhMuc.equals("Danh mục")
                    || danhMuc.equals("Tất cả")
                    || (m.getTenDanhMuc() != null &&
                    removeAccent(m.getTenDanhMuc())
                            .equals(removeAccent(danhMuc)));

            if (matchName && matchDanhMuc) {
                view.addMonCard(m.getMaMon(), m.getTenMon(), m.getDonGia(), m.getHinhAnh());
            }
        }
        view.revalidate();
        view.repaint();
    }

    // Xử lý nút xóa hết giỏ hàng
    private void xoaHetGioHang() {
        if (view.getCartData().isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có muốn làm trống giỏ hàng?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            view.clearCart();
            view.repaint();
        }
    }

    // Cập nhật tổng tiền dựa trên List dữ liệu
    private void updateTongTien() {
        currentTotal = 0;
        List<Object[]> data = view.getCartData();
        for (Object[] item : data) {
            int sl = Integer.parseInt(item[2].toString());
            double gia = Double.parseDouble(item[3].toString());
            currentTotal += (sl * gia);
        }
        view.setTongTien(currentTotal);
    }

    private void moPopupThanhToan() {
        List<Object[]> data = view.getCartData();
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Giỏ hàng trống!");
            return;
        }

        // Tính lại tiền lần cuối trước khi thanh toán
        updateTongTien();

        // Chuyển đổi List sang DefaultTableModel tạm thời để Dialog hiển thị (nếu Dialog dùng Table)
        String[] col = {"Tên món", "Size", "SL", "Thành tiền"};
        DefaultTableModel tempModel = new DefaultTableModel(col, 0);
        for (Object[] item : data) {
            double thanhTien = (int) item[2] * (double) item[3];
            tempModel.addRow(new Object[]{item[0], item[1], item[2], String.format("%,.0f", thanhTien)});
        }

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(view);
        XacNhanThanhToanDialog dialog = new XacNhanThanhToanDialog(parent, tempModel, currentTotal);

        dialog.addXacNhanListener(e -> {
            String tenKhach = dialog.getTenKhach();
            String sdt = dialog.getSDT();
            if (tenKhach.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nhập tên khách!");
                return;
            }
            luuHoaDonVaoDB(tenKhach, sdt);
            dialog.dispose();
        });
        dialog.setVisible(true);
    }

    private void luuHoaDonVaoDB(String tenKhach, String sdt) {
        java.sql.Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String maKH = null;
            if (!sdt.isEmpty()) {
                String sqlCheck = "SELECT MaKH FROM KhachHang WHERE SoDienThoai = ?";
                PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
                psCheck.setString(1, sdt);
                java.sql.ResultSet rsCheck = psCheck.executeQuery();

                if (rsCheck.next()) {
                    maKH = rsCheck.getString("MaKH");
                } else {
                    maKH = new KhachHangDAO().getNewID();
                    String sqlInsertKH = "INSERT INTO KhachHang(MaKH, TenKH, TheLoai, GioiTinh, SoDienThoai) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement psKH = conn.prepareStatement(sqlInsertKH);
                    psKH.setString(1, maKH);
                    psKH.setString(2, tenKhach);
                    psKH.setString(3, "Vãng lai");
                    psKH.setString(4, "Khác");
                    psKH.setString(5, sdt);
                    psKH.executeUpdate();
                }
            }

            String maHD = hoaDonDao.getNewID();
            String ngayLap = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

            String sqlHD = "INSERT INTO HoaDon(MaHD, TenNV, TenKH, NgayLap, TongTien) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psHD = conn.prepareStatement(sqlHD);
            psHD.setString(1, maHD);
            psHD.setString(2, Session.tenNV);
            psHD.setString(3, tenKhach);
            psHD.setString(4, ngayLap);
            psHD.setDouble(5, currentTotal);
            psHD.executeUpdate();

            String sqlCT = "INSERT INTO ChiTietHoaDon(MaHD, TenMon, Size, SoLuong, DonGia) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psCT = conn.prepareStatement(sqlCT);

            List<Object[]> data = view.getCartData();
            for (Object[] item : data) {
                psCT.setString(1, maHD);
                psCT.setString(2, item[0].toString());
                psCT.setString(3, item[1].toString());
                psCT.setInt(4, (int) item[2]);
                psCT.setDouble(5, (double) item[3]);
                psCT.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(view, "Thanh toán thành công! Mã HĐ: " + maHD);

            view.clearCart();
            // Gọi hàm render của View để xóa trắng giao diện giỏ hàng
            // (Trong DatMonView bạn nên có phương thức để làm điều này hoặc gọi renderCart công khai)
            loadMenu(); // Refresh lại menu nếu cần

            if (hoaDonController != null) hoaDonController.loadData("");
            if (khachHangController != null) khachHangController.loadData();

        } catch (Exception ex) {
            try { if (conn != null) conn.rollback(); } catch (Exception e) {}
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi thanh toán!");
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public void reloadDanhMuc() { loadDanhMuc(); }
    public void setKhachHangController(KhachHangController controller) { this.khachHangController = controller; }
}