package CH.model;

public class MonAn {
    private String maMon;
    private String tenMon;
    private double donGia;
    private String donViTinh;
    private String hinhAnh;
    private String tenDanhMuc;

    public MonAn() {
    }

    // ✅ Constructor KHÔNG còn maHH
    public MonAn(String maMon, String tenMon, double donGia,
                 String donViTinh, String hinhAnh, String tenDanhMuc) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.donGia = donGia;
        this.donViTinh = donViTinh;
        this.hinhAnh = hinhAnh;
        this.tenDanhMuc = tenDanhMuc;
    }

    // --- GETTERS & SETTERS ---
    public String getMaMon() { return maMon; }
    public void setMaMon(String maMon) { this.maMon = maMon; }

    public String getTenMon() { return tenMon; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getTenDanhMuc() { return tenDanhMuc; }
    public void setTenDanhMuc(String tenDanhMuc) { this.tenDanhMuc = tenDanhMuc; }

    // ✅ Không còn maHH trong bảng
    public Object[] toObjectArray() {
        return new Object[]{
                maMon,
                tenMon,
                String.format("%,.0f", donGia),
                donViTinh,
                tenDanhMuc,
                hinhAnh
        };
    }
}