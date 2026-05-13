package CH.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnection {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB_NAME = "quanlycuahang1";
    private static final String USER = "root";
    private static final String PASS = "123456";

    private static final String SERVER_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/";
    private static final String DB_URL = SERVER_URL + DB_NAME;

    public static Connection getConnection() {
        Connection cons = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cons = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cons;
    }

    public static void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 1. Tạo Database
            Connection serverConn = DriverManager.getConnection(SERVER_URL, USER, PASS);
            Statement stmt = serverConn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("Kiem tra Database: " + DB_NAME);
            stmt.close();
            serverConn.close();

            // 2. Kết nối DB
            Connection dbConn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement dbStmt = dbConn.createStatement();

            // NhanVien
            dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS NhanVien ("
                    + "MaNV VARCHAR(20) PRIMARY KEY,"
                    + "TenNV VARCHAR(100),"
                    + "NgaySinh VARCHAR(20),"
                    + "GioiTinh VARCHAR(10),"
                    + "ChucVu VARCHAR(50),"
                    + "SoDienThoai VARCHAR(15),"
                    + "DiaChi VARCHAR(255),"
                    + "Username VARCHAR(50),"
                    + "Password VARCHAR(50),"
                    + "Role VARCHAR(20))");

            // KhachHang
            dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS KhachHang ("
                    + "MaKH VARCHAR(20) PRIMARY KEY,"
                    + "TenKH VARCHAR(100),"
                    + "TheLoai VARCHAR(20),"
                    + "GioiTinh VARCHAR(10),"
                    + "Email VARCHAR(100),"
                    + "SoDienThoai VARCHAR(15),"
                    + "DiaChi VARCHAR(255))");

            // Kho
            dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS Kho ("
                    + "MaHH VARCHAR(20) PRIMARY KEY,"
                    + "TenHH VARCHAR(100),"
                    + "SoLuong INT,"
                    + "GiaNhap DOUBLE,"
                    + "GiaBan DOUBLE)");

            // DanhMuc
            dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS DanhMuc ("
                    + "MaDanhMuc VARCHAR(20) PRIMARY KEY,"
                    + "TenDanhMuc VARCHAR(100))");

            // ThucDon (ĐÃ FIX)
            dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS ThucDon ("
                    + "MaMon VARCHAR(20) PRIMARY KEY,"
                    + "TenMon VARCHAR(100),"
                    + "DonGia DOUBLE,"
                    + "DonViTinh VARCHAR(20),"
                    + "HinhAnh VARCHAR(255),"
                    + "TenDanhMuc VARCHAR(100))");

            // HoaDon
            dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS HoaDon ("
                    + "MaHD VARCHAR(20) PRIMARY KEY,"
                    + "TenNV VARCHAR(100),"
                    + "TenKH VARCHAR(100),"
                    + "NgayLap VARCHAR(20),"
                    + "TongTien DOUBLE)");

            // ChiTietHoaDon
            dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS ChiTietHoaDon ("
                    + "ID INT AUTO_INCREMENT PRIMARY KEY,"
                    + "MaHD VARCHAR(20),"
                    + "TenMon VARCHAR(100),"
                    + "SoLuong INT,"
                    + "DonGia DOUBLE,"
                    + "Size VARCHAR(5),"
                    + "FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD) ON DELETE CASCADE)");

            // ====== DỮ LIỆU MẪU ======

            // NhanVien
            ResultSet rsNV = dbStmt.executeQuery("SELECT COUNT(*) FROM NhanVien");
            if (rsNV.next() && rsNV.getInt(1) == 0) {
                dbStmt.executeUpdate("INSERT INTO NhanVien VALUES "
                        + "('NV01','Admin','01/01/1990','Nam','Quan ly','0901','HN','admin','123','ADMIN'),"
                        + "('NV02','Staff','01/01/2000','Nu','Nhan vien','0902','HCM','staff','123','NHÂN VIÊN')");
            }
            rsNV.close();

            // Kho
            ResultSet rsKho = dbStmt.executeQuery("SELECT COUNT(*) FROM Kho");
            if (rsKho.next() && rsKho.getInt(1) == 0) {
                dbStmt.executeUpdate("INSERT INTO Kho VALUES "
                        + "('HH01','Ca phe',100,10000,0),"
                        + "('HH02','Tra sua',100,15000,0),"
                        + "('HH03','Banh',100,20000,0)");
            }
            rsKho.close();

            // ThucDon (ĐÃ FIX LỖI)
            ResultSet rsMenu = dbStmt.executeQuery("SELECT COUNT(*) FROM ThucDon");
            if (rsMenu.next() && rsMenu.getInt(1) == 0) {
                dbStmt.executeUpdate("INSERT INTO ThucDon "
                        + "(MaMon,TenMon,DonGia,DonViTinh,HinhAnh,TenDanhMuc) VALUES "
                        + "('M01','Ca phe den',20000,'Ly','','Nuoc'),"
                        + "('M02','Tra sua',30000,'Ly','','Nuoc'),"
                        + "('M03','Banh ngot',25000,'Cai','','Do an')");
            }
            rsMenu.close();

            System.out.println("Khoi tao Database thanh cong!");

            dbStmt.close();
            dbConn.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Loi ket noi hoac khoi tao Database!");
        }
    }


}