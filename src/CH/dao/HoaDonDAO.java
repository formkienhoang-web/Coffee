package CH.dao;

import CH.model.HoaDon;
import CH.model.ChiTietHoaDon;
import CH.model.ThongKeDoanhThu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    // 1. Lấy danh sách tất cả hóa đơn
    public List<HoaDon> getAll() {
        List<HoaDon> list = new ArrayList<>();
        try {
            Connection cons = DBConnection.getConnection();
            String sql = "SELECT * FROM HoaDon ORDER BY MaHD DESC";
            PreparedStatement ps = cons.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoaDon hd = new HoaDon(
                    rs.getString("MaHD"),
                    rs.getString("TenNV"),
                    rs.getString("TenKH"),
                    rs.getString("NgayLap"),
                    rs.getDouble("TongTien")
                );
                list.add(hd);
            }
            ps.close();
            cons.close();
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. Lấy chi tiết hóa đơn
    public List<ChiTietHoaDon> getChiTiet(String maHD) {
        List<ChiTietHoaDon> list = new ArrayList<>();
        try {
            Connection cons = DBConnection.getConnection();
            String sql = "SELECT * FROM ChiTietHoaDon WHERE MaHD = ?";
            PreparedStatement ps = cons.prepareStatement(sql);
            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon(
                    rs.getString("TenMon"),
                    rs.getString("Size"),
                    rs.getInt("SoLuong"),
                    rs.getDouble("DonGia")
                );
                list.add(ct);
            }
            ps.close();
            cons.close();
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 3 Thêm hóa đơn
    public boolean add(HoaDon hd) {
        try {
            Connection cons = DBConnection.getConnection();
            String sql = "INSERT INTO HoaDon(MaHD, TenNV, TenKH, NgayLap, TongTien) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = cons.prepareStatement(sql);
            ps.setString(1, hd.getMaHD());
            ps.setString(2, hd.getTenNV());
            ps.setString(3, hd.getTenKH());
            ps.setString(4, hd.getNgayLap());
            ps.setDouble(5, hd.getTongTien());
            
            int row = ps.executeUpdate();
            ps.close();
            cons.close();
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4.Sửa hóa đơn
    public boolean update(HoaDon hd) {
        try {
            Connection cons = DBConnection.getConnection();
            String sql = "UPDATE HoaDon SET TenNV=?, TenKH=?, NgayLap=? WHERE MaHD=?";
            PreparedStatement ps = cons.prepareStatement(sql);
            ps.setString(1, hd.getTenNV());
            ps.setString(2, hd.getTenKH());
            ps.setString(3, hd.getNgayLap());
            ps.setString(4, hd.getMaHD());
            
            int row = ps.executeUpdate();
            ps.close();
            cons.close();
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5.Xóa hóa đơn
    public boolean delete(String maHD) {
        try {
            Connection cons = DBConnection.getConnection();
            String sql = "DELETE FROM HoaDon WHERE MaHD=?";
            PreparedStatement ps = cons.prepareStatement(sql);
            ps.setString(1, maHD);
            
            int row = ps.executeUpdate();
            ps.close();
            cons.close();
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 6. Thêm dữ liệu mẫu 
    public void addSampleDataIfEmpty() {
        try {
            Connection cons = DBConnection.getConnection();
            ResultSet rs = cons.createStatement().executeQuery("SELECT COUNT(*) FROM HoaDon");
            if (rs.next() && rs.getInt(1) == 0) {
                // Thêm Hóa đơn 1
                cons.createStatement().executeUpdate(
                        "INSERT INTO HoaDon VALUES ('HD001', 'Nguyễn Văn A', 'Trần Thị B', '01/12/2025', 130000)"
                );

                cons.createStatement().executeUpdate(
                        "INSERT INTO HoaDon VALUES ('HD002', 'Lê Văn C', 'Khách vãng lai', '02/12/2025', 20000)"
                );
                cons.createStatement().executeUpdate(
                        "INSERT INTO ChiTietHoaDon(MaHD, TenMon, Size, SoLuong, DonGia) VALUES ('HD001', 'Gà rán', 'S', 2, 35000)"
                );

                cons.createStatement().executeUpdate(
                        "INSERT INTO ChiTietHoaDon(MaHD, TenMon, Size, SoLuong, DonGia) VALUES ('HD001', 'Khoai tây chiên', 'M', 1, 60000)"
                );

                cons.createStatement().executeUpdate(
                        "INSERT INTO ChiTietHoaDon(MaHD, TenMon, Size, SoLuong, DonGia) VALUES ('HD002', 'Pepsi', 'S', 2, 10000)"
                );
                
                System.out.println("Đã thêm dữ liệu mẫu cho Hóa đơn.");
            }
            cons.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
    public String getNewID() {
        String newID = "HD001"; // Mặc định nếu chưa có hóa đơn nào
        try {
            Connection cons = DBConnection.getConnection();
            // Lấy mã hóa đơn lớn nhất hiện tại
            String sql = "SELECT MaHD FROM HoaDon ORDER BY MaHD DESC LIMIT 1";
            PreparedStatement ps = cons.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String lastID = rs.getString("MaHD"); 
                if (lastID.length() >= 4) { 
                    String prefix = lastID.substring(0, 2); 
                    String numberPart = lastID.substring(2); 
                    
                    try {
                        int number = Integer.parseInt(numberPart);
                        number++; // Tăng lên 1 -> 10
                        newID = prefix + String.format("%03d", number); // -> "HD010"
                    } catch (NumberFormatException e) {
                        // Phòng trường hợp mã cũ không đúng định dạng số
                        System.out.println("Lỗi parse mã cũ: " + lastID);
                        newID = "HD" + System.currentTimeMillis(); // Fallback an toàn
                    }
                }
            }
            ps.close();
            cons.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newID;
    }

    public double sumAllTongTien() {
       double total = 0;
        try {
            Connection cons = DBConnection.getConnection();
            String sql = "SELECT SUM(TongTien) FROM HoaDon";
            PreparedStatement ps = cons.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
            ps.close();
            cons.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public int countAll() {
        int count = 0;
        try {
            Connection cons = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM HoaDon";
            PreparedStatement ps = cons.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            ps.close();
            cons.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
    public List<ThongKeDoanhThu> getDoanhThuTheoNgay(String dateFrom, String dateTo) {
        List<ThongKeDoanhThu> list = new ArrayList<>();
        try {
            Connection cons = DBConnection.getConnection();
            
            // SỬ DỤNG STR_TO_DATE VÀ GROUP BY ĐỂ GỘP NHÓM DOANH THU THEO NGÀY
            String sql = "SELECT NgayLap, SUM(TongTien) AS TongDoanhThu FROM HoaDon "
                       + "WHERE STR_TO_DATE(NgayLap, '%d/%m/%Y') BETWEEN " 
                       + "STR_TO_DATE(?, '%d/%m/%Y') AND STR_TO_DATE(?, '%d/%m/%Y') "
                       + "GROUP BY NgayLap ORDER BY STR_TO_DATE(NgayLap, '%d/%m/%Y')"; 

            PreparedStatement ps = cons.prepareStatement(sql);
            ps.setString(1, dateFrom);
            ps.setString(2, dateTo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ThongKeDoanhThu tk = new ThongKeDoanhThu(
                    rs.getString("NgayLap"), 
                    rs.getDouble("TongDoanhThu")
                );
                list.add(tk);
            }
            ps.close();
            cons.close();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return list;
    }



}


