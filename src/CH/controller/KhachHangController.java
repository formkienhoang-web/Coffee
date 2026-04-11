package CH.controller;

import CH.dao.KhachHangDAO;
import CH.model.KhachHang;
import CH.view.KhachHangView;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

public class KhachHangController {
    private KhachHangView view;
    private KhachHangDAO dao;
    private boolean isEdit = false; // Phân biệt Thêm/Sửa giống ThucDonController

    public KhachHangController(KhachHangView view) {
        this.view = view;
        this.dao = new KhachHangDAO();

        loadData(""); // Load dữ liệu mặc định

        // 1. Tìm kiếm thời gian thực (Live Search)
        view.getTxtSearch().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void changedUpdate(DocumentEvent e) { search(); }
            private void search() {
                String keyword = view.getTxtSearch().getText();
                // Bỏ qua nếu là placeholder
                if (keyword.contains("🔍")) loadData("");
                else loadData(keyword);
            }
        });

        // 2. Sự kiện nút Thêm (Mở Dialog)
        view.getBtnThem().addActionListener(e -> {
            isEdit = false;
            view.clearForm();
            view.getDialogForm().setTitle("Thêm Khách Hàng Mới");
            view.getDialogForm().setVisible(true);
        });

        // 3. Sự kiện nút Sửa (Gọi từ Icon trong Table)
        view.getBtnSua().addActionListener(e -> {
            int row = view.getTable().getSelectedRow();
            if (row >= 0) {
                isEdit = true;
                String maKH = view.getTable().getValueAt(row, 0).toString();
                KhachHang kh = dao.getById(maKH); // Giả định DAO có hàm getById
                if (kh != null) {
                    view.fillForm(kh);
                    view.getDialogForm().setTitle("Cập Nhật Thông Tin Khách Hàng");
                    view.getDialogForm().setVisible(true);
                }
            }
        });

        // 4. Sự kiện nút Xóa (Gọi từ Icon trong Table)
        view.getBtnXoa().addActionListener(e -> {
            int row = view.getTable().getSelectedRow();
            if (row >= 0) {
                String maKH = view.getTable().getValueAt(row, 0).toString();
                String tenKH = view.getTable().getValueAt(row, 1).toString();

                int confirm = JOptionPane.showConfirmDialog(view,
                        "Bạn có chắc chắn muốn xóa khách hàng [" + tenKH + "]?",
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (dao.delete(maKH)) {
                        JOptionPane.showMessageDialog(view, "Đã xóa khách hàng thành công!");
                        loadData("");
                    } else {
                        JOptionPane.showMessageDialog(view, "Lỗi: Không thể xóa khách hàng này!");
                    }
                }
            }
        });

        // 5. Nút Lưu (Nằm trong Dialog)
        view.getBtnLuu().addActionListener(e -> {
            KhachHang kh = view.getKhachHangInfo();

            // Validate sơ bộ
            if (kh.getTenKH().isEmpty() || kh.getSoDienThoai().isEmpty()) {
                JOptionPane.showMessageDialog(view.getDialogForm(), "Vui lòng nhập tên và số điện thoại!");
                return;
            }

            if (isEdit) {
                if (dao.update(kh)) {
                    JOptionPane.showMessageDialog(view.getDialogForm(), "Cập nhật thành công!");
                    view.getDialogForm().dispose();
                    loadData("");
                } else {
                    JOptionPane.showMessageDialog(view.getDialogForm(), "Cập nhật thất bại!");
                }
            } else {
                if (dao.insert(kh)) {
                    JOptionPane.showMessageDialog(view.getDialogForm(), "Thêm khách hàng thành công!");
                    view.getDialogForm().dispose();
                    loadData("");
                } else {
                    JOptionPane.showMessageDialog(view.getDialogForm(), "Lỗi: Số điện thoại hoặc Mã đã tồn tại!");
                }
            }
        });
    }

    // Hàm load dữ liệu và filter (Đồng bộ logic với ThucDonController)
    private void loadData(String keyword) {
        view.clearTable();
        String searchKey = removeAccent(keyword.toLowerCase().trim());
        List<KhachHang> list = dao.getAll();

        for (KhachHang kh : list) {
            String tenKoDau = removeAccent(kh.getTenKH().toLowerCase());
            String maKoDau = kh.getMaKH().toLowerCase();
            String sdt = kh.getSoDienThoai();

            if (searchKey.isEmpty() || tenKoDau.contains(searchKey) ||
                    maKoDau.contains(searchKey) || sdt.contains(searchKey)) {
                view.addRow(kh); // View đã có sẵn hàm addRow(KhachHang kh)
            }
        }
    }

    // Hàm loại bỏ dấu tiếng Việt
    private String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("")
                .replace('đ', 'd').replace('Đ', 'D');
    }
    public void loadData() {
        loadData(""); // Tự động gọi hàm có tham số với chuỗi rỗng
    }
}