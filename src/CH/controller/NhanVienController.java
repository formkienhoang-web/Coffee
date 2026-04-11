package CH.controller;

import CH.dao.NhanVienDAO;
import CH.model.NhanVien;
import CH.view.NhanVienView;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;

public class NhanVienController {
    private NhanVienView view;
    private NhanVienDAO nhanVienDAO;
    private boolean isEdit = false; // Phân biệt chế độ Thêm và Sửa

    public NhanVienController(NhanVienView view) {
        this.view = view;
        this.nhanVienDAO = new NhanVienDAO();

        loadDataToView("");

        // 1. Sự kiện Tìm kiếm (Live Search)
        view.getTxtSearch().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { search(); }
            @Override
            public void removeUpdate(DocumentEvent e) { search(); }
            @Override
            public void changedUpdate(DocumentEvent e) { search(); }

            private void search() {
                String keyword = view.getTxtSearch().getText();
                // Bỏ qua nếu là placeholder mặc định
                if (keyword.contains("🔍")) {
                    loadDataToView("");
                } else {
                    loadDataToView(keyword.trim());
                }
            }
        });

        // 2. Sự kiện nút Thêm (Mở Dialog trống)
        view.addThemListener(e -> {
            isEdit = false;
            view.clearForm();
            view.getDialogForm().setTitle("Thêm nhân viên mới");
            view.getDialogForm().setVisible(true);
        });

        // 3. Sự kiện nút Sửa (Bắt sự kiện từ cột Hành động trong bảng)
        view.addSuaListener(e -> {
            int row = view.getTable().getSelectedRow();
            if (row != -1) {
                isEdit = true;
                String maNV = view.getTable().getValueAt(row, 0).toString();
                NhanVien nv = nhanVienDAO.getByID(maNV);
                if (nv != null) {
                    view.fillForm(nv);
                    view.getDialogForm().setTitle("Chỉnh sửa thông tin nhân viên");
                    view.getDialogForm().setVisible(true);
                }
            }
        });

        // 4. Sự kiện nút Xóa
        view.addXoaListener(e -> {
            int row = view.getTable().getSelectedRow();
            if (row != -1) {
                String maNV = view.getTable().getValueAt(row, 0).toString();
                int confirm = JOptionPane.showConfirmDialog(view,
                        "Bạn có chắc chắn muốn xóa nhân viên " + maNV + "?",
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (nhanVienDAO.delete(maNV)) {
                        JOptionPane.showMessageDialog(view, "Xóa thành công!");
                        loadDataToView("");
                    } else {
                        JOptionPane.showMessageDialog(view, "Xóa thất bại!");
                    }
                }
            }
        });

        // 5. Sự kiện nút Lưu (Trong Dialog)
        view.getBtnLuu().addActionListener(e -> {
            NhanVien nv = view.getNhanVienInfo();

            // Validate cơ bản
            if (nv.getTenNV().isEmpty() || nv.getSoDienThoai().isEmpty()) {
                JOptionPane.showMessageDialog(view.getDialogForm(), "Vui lòng nhập đầy đủ thông tin bắt buộc!");
                return;
            }

            boolean thanhCong;
            if (isEdit) {
                thanhCong = nhanVienDAO.update(nv);
            } else {
                thanhCong = nhanVienDAO.insert(nv);
            }

            if (thanhCong) {
                JOptionPane.showMessageDialog(view.getDialogForm(), (isEdit ? "Cập nhật" : "Thêm mới") + " thành công!");
                view.getDialogForm().dispose();
                loadDataToView("");
            } else {
                JOptionPane.showMessageDialog(view.getDialogForm(), "Thao tác thất bại. Vui lòng kiểm tra lại!");
            }
        });
    }

    // Load dữ liệu lên bảng (Có lọc theo từ khóa)
    private void loadDataToView(String keyword) {
        view.clearTable();
        List<NhanVien> list;

        if (keyword.isEmpty()) {
            list = nhanVienDAO.getAll();
        } else {
            list = nhanVienDAO.search(keyword);
        }

        for (NhanVien nv : list) {
            view.addRow(new Object[]{
                    nv.getMaNV(),
                    nv.getTenNV(),
                    nv.getNgaySinh(),
                    nv.getGioiTinh(),
                    nv.getChucVu(),
                    nv.getSoDienThoai(),
                    "" // Cột hành động (Renderers sẽ tự vẽ icon Sửa/Xóa)
            });
        }
    }
}