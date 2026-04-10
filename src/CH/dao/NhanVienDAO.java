package CH.dao;

import CH.model.NhanVien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    // 1. Lấy danh sách nhân viên (Bao gồm cả Username, Role)
    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        // Sắp xếp theo MaNV giảm dần để thấy nhân viên mới nhất ở trên
        String sql = "SELECT * FROM NhanVien ORDER BY MaNV DESC"; 

        try {
            Connection cons = DBConnection.getConnection();
            PreparedStatement ps = cons.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
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

                list.add(nv);
            }
            ps.close();
            cons.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Thêm nhân viên mới (Cập nhật SQL đủ 10 tham số)
    public boolean add(NhanVien nv) {
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
            
            // --- Lưu Tài khoản, Mật khẩu, Quyền ---
            ps.setString(8, nv.getUsername());
            ps.setString(9, nv.getPassword()); // Nếu view chưa có ô nhập pass, Controller nên set mặc định là "123"
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

    // 3. Cập nhật thông tin (Bao gồm cập nhật cả Quyền và Pass nếu cần)
    public boolean update(NhanVien nv) {

        // Thêm "Password=?" vào câu lệnh SQL
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
            
            // Điều kiện WHERE
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

    // 4. Xóa nhân viên
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
    
    // 5. Hàm sinh mã tự động (NV01, NV02...)
    public String getNewID() {
        String newID = "NV01";
        try {
            Connection cons = DBConnection.getConnection();
            String sql = "SELECT MAX(MaNV) FROM NhanVien";
            PreparedStatement ps = cons.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String maxID = rs.getString(1);
                if (maxID != null) {
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

    public NhanVien loginNV(String username, String password) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM NhanVien WHERE Username=? AND Password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("MaNV"));
                nv.setTenNV(rs.getString("TenNV"));
                nv.setUsername(rs.getString("Username"));
                nv.setRole(rs.getString("Role"));
                return nv;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // Thêm vào NhanVienDAO
    public List<NhanVien> search(String keyword) {
        List<NhanVien> list = new ArrayList<>();
        // Tìm theo Tên hoặc Mã NV
        String sql = "SELECT * FROM NhanVien WHERE TenNV LIKE ? OR MaNV LIKE ?";
        try {
            Connection cons = DBConnection.getConnection();
            PreparedStatement ps = cons.prepareStatement(sql);
            String query = "%" + keyword + "%";
            ps.setString(1, query);
            ps.setString(2, query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
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
                list.add(nv);
            }
            ps.close();
            cons.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}