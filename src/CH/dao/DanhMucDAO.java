package CH.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DanhMucDAO {

    // 1. Lấy tất cả danh mục
    public List<String[]> getAll() {
        List<String[]> list = new ArrayList<>();

        String sql = "SELECT * FROM DanhMuc";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("MaDanhMuc"),
                        rs.getString("TenDanhMuc")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 2. Thêm danh mục
    public boolean insert(String ma, String ten) {
        String sql = "INSERT INTO DanhMuc (MaDanhMuc, TenDanhMuc) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ma);
            ps.setString(2, ten);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Sửa danh mục
    public boolean update(String ma, String ten) {
        String sql = "UPDATE DanhMuc SET TenDanhMuc=? WHERE MaDanhMuc=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ten);
            ps.setString(2, ma);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Xóa danh mục
    public boolean delete(String ma) {
        String sql = "DELETE FROM DanhMuc WHERE MaDanhMuc=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ma);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. Lấy danh sách tên danh mục (dùng cho Combobox Thực đơn)
    public List<String> getAllTenDanhMuc() {
        List<String> list = new ArrayList<>();

        String sql = "SELECT TenDanhMuc FROM DanhMuc";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("TenDanhMuc"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<String> getTenDanhMuc() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT TenDanhMuc FROM DanhMuc";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("TenDanhMuc"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}