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

        loadMenu();
        loadDanhMuc();

        view.addXoaListener(e -> xoaKhoiGio());
        view.addThanhToanListener(e -> moPopupThanhToan());
        view.getModelGioHang().addTableModelListener(e -> updateTongTien());


        // 🔥 lọc theo danh mục
        view.getCbDanhMuc().addActionListener(e -> loadMenu());

// 🔥 tìm kiếm realtime
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
    // ================= LOAD MENU CARD =================
    public void loadMenu() {
        view.clearMenu();

        List<MonAn> list = menuDao.getAll();

        String keyword = view.getTxtSearch().getText().trim();
        String danhMuc = view.getCbDanhMuc().getSelectedItem() != null
                ? view.getCbDanhMuc().getSelectedItem().toString()
                : "Danh mục";

        for (MonAn m : list) {

            // 🔍 LỌC THEO TÊN
            boolean matchName =
                    removeAccent(m.getTenMon())
                            .contains(removeAccent(keyword));

            // 📂 LỌC THEO DANH MỤC
            boolean matchDanhMuc =
                    danhMuc.equals("Danh mục")
                            || danhMuc.equals("Tất cả")
                            || (m.getTenDanhMuc() != null
                            && m.getTenDanhMuc().equalsIgnoreCase(danhMuc));

            if (matchName && matchDanhMuc) {
                view.addMonCard(
                        m.getMaMon(),
                        m.getTenMon(),
                        m.getDonGia(),
                        m.getHinhAnh()
                );
            }
        }

        view.revalidate();
        view.repaint();
    }

    // ================= XÓA =================
    private void xoaKhoiGio() {
        int row = view.getTableGioHang().getSelectedRow();
        if (row >= 0) {
            ((DefaultTableModel)view.getTableGioHang().getModel()).removeRow(row);
            updateTongTien();
        }else {
            JOptionPane.showMessageDialog(view, "Chọn món cần xoá!");
            return;
        }
    }

    // ================= TỔNG TIỀN =================
    private void updateTongTien() {
        currentTotal = 0;
        DefaultTableModel model = view.getModelGioHang();

        for (int i = 0; i < model.getRowCount(); i++) {
            String tienStr = model.getValueAt(i, 4).toString().replace(",", "");
            currentTotal += Double.parseDouble(tienStr);
        }

        view.setTongTien(currentTotal);
    }

    // ================= THANH TOÁN =================
    private void moPopupThanhToan() {
        if (view.getModelGioHang().getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Giỏ hàng trống!");
            return;
        }

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(view);
        XacNhanThanhToanDialog dialog =
                new XacNhanThanhToanDialog(parent, view.getModelGioHang(), currentTotal);

        dialog.addXacNhanListener(e -> {
            String tenKhach = dialog.getTenKhach();
            String sdt = dialog.getSDT();

            if (tenKhach.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nhập tên khách!");
                return;
            }

            luuHoaDonVaoDB(tenKhach,sdt);
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    // ================= LƯU DB (FIX CHUẨN) =================
    private void luuHoaDonVaoDB(String tenKhach, String sdt) {
        java.sql.Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            // 1. LOGIC TỰ ĐỘNG LƯU KHÁCH HÀNG
            // =================================================================
            String maKH = null;

            if (!sdt.isEmpty()) {
                // A. Kiểm tra xem SĐT này đã có chưa
                String sqlCheck = "SELECT MaKH, TenKH FROM KhachHang WHERE SoDienThoai = ?";
                PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
                psCheck.setString(1, sdt);
                java.sql.ResultSet rsCheck = psCheck.executeQuery();

                if (rsCheck.next()) {
                    // Đã tồn tại -> Lấy MaKH cũ
                    maKH = rsCheck.getString("MaKH");
                    // (Tùy chọn: Có thể cập nhật lại tên khách nếu muốn)
                } else {
                    // Chưa tồn tại -> Thêm mới vào bảng KhachHang

                    // Sinh mã KH mới (Gọi DAO hoặc tự sinh trong transaction)
                    // Ở đây gọi DAO để lấy ID mới nhất cho an toàn
                    String newMaKH = new KhachHangDAO().getNewID();

                    String sqlInsertKH = "INSERT INTO KhachHang(MaKH, TenKH, TheLoai, GioiTinh, Email, SoDienThoai, DiaChi) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement psKH = conn.prepareStatement(sqlInsertKH);
                    psKH.setString(1, newMaKH);
                    psKH.setString(2, tenKhach);
                    psKH.setString(3, "Vãng lai"); // Mặc định
                    psKH.setString(4, "Khác");     // Mặc định
                    psKH.setString(5, "");         // Email trống
                    psKH.setString(6, sdt);
                    psKH.setString(7, "");         // Địa chỉ trống

                    psKH.executeUpdate();
                    maKH = newMaKH;
                }
            }

            String maHD = hoaDonDao.getNewID();
            String ngayLap = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

            // 🔥 LƯU HÓA ĐƠN (CÙNG CONNECTION)
            String sqlHD = "INSERT INTO HoaDon(MaHD, TenNV, TenKH, NgayLap, TongTien) VALUES (?, ?, ?, ?, ?)";
            java.sql.PreparedStatement psHD = conn.prepareStatement(sqlHD);

            psHD.setString(1, maHD);
            psHD.setString(2, Session.tenNV);
            psHD.setString(3, tenKhach);
            psHD.setString(4, ngayLap);
            psHD.setDouble(5, currentTotal);

            psHD.executeUpdate();

            // 🔥 LƯU CHI TIẾT
            String sql = "INSERT INTO ChiTietHoaDon(MaHD, TenMon, Size, SoLuong, DonGia) VALUES (?, ?, ?, ?, ?)";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);

            DefaultTableModel model = view.getModelGioHang();

            for (int i = 0; i < model.getRowCount(); i++) {
                String tenMon = model.getValueAt(i, 0).toString();
                String size = model.getValueAt(i, 1).toString();
                int soLuong = Integer.parseInt(model.getValueAt(i, 2).toString());
                double donGia = Double.parseDouble(
                        model.getValueAt(i, 3).toString().replace(",", "")
                );

                ps.setString(1, maHD);
                ps.setString(2, tenMon);
                ps.setString(3, size);
                ps.setInt(4, soLuong);
                ps.setDouble(5, donGia);
                ps.executeUpdate();
            }

            conn.commit();

            JOptionPane.showMessageDialog(view, "Thanh toán thành công! Mã HĐ: " + maHD);

            view.getModelGioHang().setRowCount(0);
            updateTongTien();

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

    // ================= SỬA SL =================
    private void tinhLaiTienMotDong(int row) {
        if (row < 0) return;

        try {
            DefaultTableModel model = view.getModelGioHang();
            int sl = Integer.parseInt(model.getValueAt(row, 2).toString());

            if (sl <= 0) {
                sl = 1;
                model.setValueAt(1, row, 2);
            }

            double gia = Double.parseDouble(
                    model.getValueAt(row, 3).toString().replace(",", "")
            );

            model.setValueAt(String.format("%,.0f", sl * gia), row, 4);
            updateTongTien();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Nhập số nguyên!");
        }
    }
    public void reloadDanhMuc() {
        loadDanhMuc();
    }
    public void setKhachHangController(KhachHangController controller) {
        this.khachHangController = controller;
    }
}