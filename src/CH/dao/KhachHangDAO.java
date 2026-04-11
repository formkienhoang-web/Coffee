package CH.dao;

import CH.model.KhachHang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    public List<KhachHang> getAll() {
        List<KhachHang> list = new ArrayList<>();
        try {
            Connection cons = DBConnection.getConnection();
            String sql = "SELECT * FROM KhachHang";
            PreparedStatement ps = cons.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMaKH(rs.getString("MaKH"));
                kh.setTenKH(rs.getString("TenKH"));
                kh.setTheLoai(rs.getString("TheLoai"));
                kh.setGioiTinh(rs.getString("GioiTinh"));
                kh.setEmail(rs.getString("Email"));
                kh.setSoDienThoai(rs.getString("SoDienThoai"));
                kh.setDiaChi(rs.getString("DiaChi"));
                list.add(kh);
            }
            ps.close();
            cons.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 1. Thêm hàm getById (Controller cần để fill dữ liệu khi Sửa)
    public KhachHang getById(String maKH) {
        try {
            Connection cons = DBConnection.getConnection();
            String sql = "SELECT * FROM KhachHang WHERE MaKH = ?";
            PreparedStatement ps = cons.prepareStatement(sql);
            ps.setString(1, maKH);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMaKH(rs.getString("MaKH"));
                kh.setTenKH(rs.getString("TenKH"));
                kh.setTheLoai(rs.getString("TheLoai"));
                kh.setGioiTinh(rs.getString("GioiTinh"));
                kh.setEmail(rs.getString("Email"));
                kh.setSoDienThoai(rs.getString("SoDienThoai"));
                kh.setDiaChi(rs.getString("DiaChi"));
                return kh;
            }
            ps.close();
            cons.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. Đổi tên từ add() thành insert() để giống ThucDonDAO
    public boolean insert(KhachHang kh) {
        try {
            Connection cons = DBConnection.getConnection();
            // Tự động lấy mã mới trước khi chèn nếu mã đang là "Tự động"
            if (kh.getMaKH() == null || kh.getMaKH().equals("Tự động")) {
                kh.setMaKH(getNewID());
            }

            String sql = "INSERT INTO KhachHang(MaKH, TenKH, TheLoai, GioiTinh, Email, SoDienThoai, DiaChi) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = cons.prepareStatement(sql);
            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getTenKH());
            ps.setString(3, kh.getTheLoai());
            ps.setString(4, kh.getGioiTinh());
            ps.setString(5, kh.getEmail());
            ps.setString(6, kh.getSoDienThoai());
            ps.setString(7, kh.getDiaChi());

            int row = ps.executeUpdate();
            ps.close();
            cons.close();
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(KhachHang kh) {
        try {
            Connection cons = DBConnection.getConnection();
            String sql = "UPDATE KhachHang SET TenKH=?, TheLoai=?, GioiTinh=?, Email=?, SoDienThoai=?, DiaChi=? WHERE MaKH=?";
            PreparedStatement ps = cons.prepareStatement(sql);
            ps.setString(1, kh.getTenKH());
            ps.setString(2, kh.getTheLoai());
            ps.setString(3, kh.getGioiTinh());
            ps.setString(4, kh.getEmail());
            ps.setString(5, kh.getSoDienThoai());
            ps.setString(6, kh.getDiaChi());
            ps.setString(7, kh.getMaKH());
            int row = ps.executeUpdate();
            ps.close();
            cons.close();
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maKH) {
        try {
            Connection cons = DBConnection.getConnection();
            String sql = "DELETE FROM KhachHang WHERE MaKH=?";
            PreparedStatement ps = cons.prepareStatement(sql);
            ps.setString(1, maKH);
            int row = ps.executeUpdate();
            ps.close();
            cons.close();
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getNewID() {
        String newID = "KH001";
        try {
            Connection cons = DBConnection.getConnection();
            // Lấy mã lớn nhất hiện tại
            String sql = "SELECT MaKH FROM KhachHang ORDER BY MaKH DESC LIMIT 1";
            PreparedStatement ps = cons.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String lastID = rs.getString("MaKH"); // Ví dụ: KH005
                String numberPart = lastID.substring(2); // "005"
                int number = Integer.parseInt(numberPart);
                number++; // 6
                newID = String.format("KH%03d", number); // KH006
            }
            ps.close();
            cons.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newID;
    }

    // Các hàm phụ trợ khác giữ nguyên...
    public int countAll() {
        int count = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM KhachHang")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return count;
    }
}