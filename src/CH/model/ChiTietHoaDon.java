package CH.model;

public class ChiTietHoaDon {
    private String tenMon;
    private String size;
    private int soLuong;
    private double donGia;
    private double thanhTien;

    public ChiTietHoaDon(String tenMon, String size, int soLuong, double donGia) {
        this.tenMon = tenMon;
        this.size=size;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = soLuong * donGia;
    }

    public Object[] toObjectArray() {
        return new Object[]{
                tenMon,
                size,
                soLuong,
                String.format("%,.0f", donGia),
                String.format("%,.0f", thanhTien)
        };
    }
}