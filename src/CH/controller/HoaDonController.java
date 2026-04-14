package CH.controller;

import CH.dao.HoaDonDAO;
import CH.model.HoaDon;
import CH.model.ChiTietHoaDon;
import CH.view.HoaDonView;
import CH.view.ChiTietHoaDonView;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

public class HoaDonController {
    private HoaDonView view;
    private HoaDonDAO dao;

    public HoaDonController(HoaDonView view) {
        this.view = view;
        this.dao = new HoaDonDAO();

        // 1. Load dữ liệu ban đầu
        loadData("");

        // 2. Xử lý tìm kiếm Real-time (Đồng bộ với ThucDonController)
        view.getTxtSearch().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { search(); }
            @Override
            public void removeUpdate(DocumentEvent e) { search(); }
            @Override
            public void changedUpdate(DocumentEvent e) { search(); }

            private void search() {
                String keyword = view.getTxtSearch().getText();
                // Bỏ qua nếu là placeholder của thanh search
                if (keyword.contains("🔍")) {
                    loadData("");
                } else {
                    loadData(keyword);
                }
            }
        });

        // 3. Xử lý sự kiện khi nhấn vào icon con mắt 👁 trong bảng
        view.addXemChiTietListener(e -> showHoaDonChiTiet());
    }

    /**
     * Nạp dữ liệu vào bảng và lọc theo từ khóa (Tìm kiếm đa năng)
     */
    public void loadData(String keyword) {
        view.clearTable();
        String searchKey = removeAccent(keyword.toLowerCase().trim());
        List<HoaDon> list = dao.getAll();

        for (HoaDon hd : list) {
            // Lấy các trường thông tin muốn tìm kiếm
            String maHD = hd.getMaHD().toLowerCase();
            String tenKH = removeAccent(hd.getTenKH().toLowerCase());
            String tenNV = removeAccent(hd.getTenNV().toLowerCase());

            // Nếu từ khóa trống hoặc khớp với bất kỳ trường nào thì thêm vào bảng
            if (searchKey.isEmpty() || maHD.contains(searchKey)
                    || tenKH.contains(searchKey) || tenNV.contains(searchKey)) {
                view.addRow(hd);
            }
        }
    }

    /**
     * Logic hiển thị cửa sổ Chi tiết hóa đơn
     */
    private void showHoaDonChiTiet() {
        int row = view.getSelectedRow();
        if (row < 0) return;

        // Lấy mã hóa đơn từ cột đầu tiên của dòng đang chọn
        String maHD = view.getTable().getValueAt(row, 0).toString();
        String tenKH = view.getTable().getValueAt(row, 2).toString();

        // Lấy tổng tiền (Xử lý chuỗi "100.000 đ" về dạng double)
        String strTien = view.getTable().getValueAt(row, 4).toString().replaceAll("[^0-9]", "");
        double tongTien = Double.parseDouble(strTien);

        // Lấy danh sách chi tiết món từ DAO
        List<ChiTietHoaDon> details = dao.getChiTiet(maHD);

        // Hiển thị Dialog chi tiết
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        ChiTietHoaDonView dialog = new ChiTietHoaDonView(parentFrame);
        dialog.setDetails(maHD, tenKH, tongTien, details);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }

    private String removeAccent(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("")
                .replace('đ', 'd').replace('Đ', 'D');
    }
}