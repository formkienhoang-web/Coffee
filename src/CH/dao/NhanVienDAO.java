package CH.dao;

import CH.model.NhanVien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    // 1. Lấy danh sách nhân viên
    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY MaNV ASC";
        try {
            Connection cons = DBConnection.getConnection();
            PreparedStatement ps = cons.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
            ps.close();
            cons.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Lấy thông tin 1 nhân viên theo ID (CẦN THIẾT CHO CHỨC NĂNG SỬA)
    public NhanVien getByID(String maNV) {
        String sql = "SELECT * FROM NhanVien WHERE MaNV = ?";
        try {
            Connection cons = DBConnection.getConnection();
            PreparedStatement ps = cons.prepareStatement(sql);
            ps.setString(1, maNV);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                NhanVien nv = mapResultSetToEntity(rs);
                ps.close();
                cons.close();
                return nv;
            }
            ps.close();
            cons.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 3. Thêm nhân viên mới (Đổi tên thành insert để khớp Controller)
    public boolean insert(NhanVien nv) {
        // Sinh mã mới nếu mã truyền vào là "Tự động sinh"
        if (nv.getMaNV() == null || nv.getMaNV().equals("Tự động sinh")) {
            nv.setMaNV(getNewID());
        }

        String sql = "INSERT INTO NhanVien(MaNV, TenNV, NgaySinh, GioiTinh, ChucVu, SoDienThoai, DiaChi, Username, Password, Role) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection cons = DBConnection.getConnection();
            PreparedStatement ps = cons.prepareStatement(sql);
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getTenNV());
            ps.setString(3, nv.getNgaySinh());
            ps.setString(4, nv.getGioiTinh());
            ps.setString(5, nv.getChucVu());
            ps.setString(6, nv.getSoDienThoai());
            ps.setString(7, nv.getDiaChi());
            ps.setString(8, nv.getUsername());
            ps.setString(9, (nv.getPassword() == null || nv.getPassword().isEmpty()) ? "123" : nv.getPassword());
            ps.setString(10, nv.getRole());

            int row = ps.executeUpdate();
            ps.close();
            cons.close();
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Cập nhật thông tin
    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET TenNV=?, NgaySinh=?, GioiTinh=?, ChucVu=?, SoDienThoai=?, DiaChi=?, Username=?, Role=?, Password=? WHERE MaNV=?";
        try {
            Connection cons = DBConnection.getConnection();
            PreparedStatement ps = cons.prepareStatement(sql);
            ps.setString(1, nv.getTenNV());
            ps.setString(2, nv.getNgaySinh());
            ps.setString(3, nv.getGioiTinh());
            ps.setString(4, nv.getChucVu());
            ps.setString(5, nv.getSoDienThoai());
            ps.setString(6, nv.getDiaChi());
            ps.setString(7, nv.getUsername());
            ps.setString(8, nv.getRole());
            ps.setString(9, nv.getPassword());
            ps.setString(10, nv.getMaNV());

            int row = ps.executeUpdate();
            ps.close();
            cons.close();
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. Xóa nhân viên
    public boolean delete(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE MaNV=?";
        try {
            Connection cons = DBConnection.getConnection();
            PreparedStatement ps = cons.prepareStatement(sql);
            ps.setString(1, maNV);
            int row = ps.executeUpdate();
            ps.close();
            cons.close();
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 6. Tìm kiếm
    public List<NhanVien> search(String keyword) {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE TenNV LIKE ? OR MaNV LIKE ? OR SoDienThoai LIKE ?";
        try {
            Connection cons = DBConnection.getConnection();
            PreparedStatement ps = cons.prepareStatement(sql);
            String query = "%" + keyword + "%";
            ps.setString(1, query);
            ps.setString(2, query);
            ps.setString(3, query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
            ps.close();
            cons.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 7. Sinh mã tự động
    public String getNewID() {
        String newID = "NV01";
        try {
            Connection cons = DBConnection.getConnection();
            String sql = "SELECT MAX(MaNV) FROM NhanVien";
            PreparedStatement ps = cons.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String maxID = rs.getString(1);
                if (maxID != null && maxID.startsWith("NV")) {
                    int so = Integer.parseInt(maxID.substring(2)) + 1;
                    newID = String.format("NV%02d", so);
                }
            }
            ps.close();
            cons.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newID;
    }

    // 8. Đăng nhập
    public NhanVien loginNV(String username, String password) {
        String sql = "SELECT * FROM NhanVien WHERE Username=? AND Password=?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm phụ trợ để map dữ liệu từ DB sang Object NhanVien
    private NhanVien mapResultSetToEntity(ResultSet rs) throws java.sql.SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNV(rs.getString("MaNV"));
        nv.setTenNV(rs.getString("TenNV"));
        nv.setNgaySinh(rs.getString("NgaySinh"));
        nv.setGioiTinh(rs.getString("GioiTinh"));
        nv.setChucVu(rs.getString("ChucVu"));
        nv.setSoDienThoai(rs.getString("SoDienThoai"));
        nv.setDiaChi(rs.getString("DiaChi"));
        nv.setUsername(rs.getString("Username"));
        nv.setPassword(rs.getString("Password"));
        nv.setRole(rs.getString("Role"));
        return nv;
    }
    // Thêm vào cuối file NhanVienDAO.java
    public boolean isExistsSdt(String sdt) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM NhanVien WHERE SoDienThoai = ?")) {
            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean isExistsUsername(String user) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM NhanVien WHERE Username = ?")) {
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}