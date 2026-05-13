package CH.controller;

import CH.dao.DanhMucDAO;
import CH.dao.ThucDonDAO;
import CH.model.MonAn;
import CH.view.ThucDonView;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class ThucDonController {
    private ThucDonView view;
    private ThucDonDAO dao;
    private DatMonController datMonController;
    private DanhMucDAO danhMucDAO = new DanhMucDAO();
    private String currentMaMon = null;

    private boolean isEdit = false; // 🔥 phân biệt thêm / sửa

    public ThucDonController(ThucDonView view, DatMonController datMonController) {
        this.view = view;
        this.dao = new ThucDonDAO();
        this.datMonController = datMonController;

        loadData("");
        loadDanhMucToComboBox();

        // ===== SEARCH =====
        view.getTxtSearch().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void changedUpdate(DocumentEvent e) { search(); }

            private void search() {
                String keyword = view.getTxtSearch().getText();
                if (keyword.contains("🔍")) loadData("");
                else loadData(keyword.trim());
            }
        });

        // ===== THÊM (CHỈ MỞ FORM) =====
        view.addThemListener(e -> {
            isEdit = false;
            view.clearForm();
            view.getDialogForm().setTitle("Thêm món mới");
            view.getDialogForm().setLocationRelativeTo(null);
            view.getDialogForm().setVisible(true);
        });

        // ===== SỬA (MỞ FORM + LOAD DATA) =====
        view.addSuaListener(e -> {
            int row = view.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(view.getDialogForm(), "Vui lòng chọn món để sửa!");
                return;
            }

            isEdit = true;

            String ma = view.getTable().getValueAt(row, 0).toString();
            currentMaMon = ma; // 🔥 LƯU ID

            MonAn m = dao.getByID(ma);
            if (m != null) {
                view.fillForm(m);
                view.getDialogForm().setTitle("Chỉnh sửa món");
                view.getDialogForm().setLocationRelativeTo(null);
                view.getDialogForm().setVisible(true);
            }
        });

        // ===== NÚT LƯU (XỬ LÝ CHÍNH) =====
        view.getBtnLuu().addActionListener(e -> {
            MonAn m = view.getMonAnInfo();

            // 1. Validate Tên món
            if (m.getTenMon() == null || m.getTenMon().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view.getDialogForm(), "Tên món không được để trống!");
                return;
            }
            if (!m.getTenMon().matches("^[\\p{L} ]+$")) {
                JOptionPane.showMessageDialog(view.getDialogForm(),
                        "Tên món không được chứa ký tự đặc biệt!");
                return;
            }

            // 2. Validate Đơn giá (Phải là số và > 0)
            // Giả sử getMonAnInfo() trả về double, bạn nên validate ngay tại View hoặc ép kiểu tại đây
            if (m.getDonGia() <= 0) {
                JOptionPane.showMessageDialog(view.getDialogForm(), "Giá không được để trống và phải là số dương");
                return;
            }
            if (m.getDonViTinh() == null || m.getDonViTinh().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view.getDialogForm(), "Đơn vị tính không được để trống!");
                return;
            }

            // 3. Validate Danh mục (Nếu chưa chọn)
            if (view.getCboDanhMuc().getSelectedIndex() <= 0) { // Giả sử item 0 là rỗng ""
                JOptionPane.showMessageDialog(view.getDialogForm(), "Vui lòng chọn danh mục cho món ăn!");
                return;
            }


            if (!isEdit) {
                // Chế độ THÊM MỚI: Tên món không được trùng bất kỳ ai
                if (dao.isExistsTenMon(m.getTenMon().trim())) {
                    JOptionPane.showMessageDialog(view.getDialogForm(), "Tên món ăn này đã tồn tại trong thực đơn!");
                    return;
                }
            } else {
                // Chế độ SỬA: Chỉ báo trùng nếu đổi sang tên của một món KHÁC
                MonAn oldMon = dao.getByID(currentMaMon);
                if (oldMon != null && !oldMon.getTenMon().equalsIgnoreCase(m.getTenMon().trim())) {
                    if (dao.isExistsTenMon(m.getTenMon().trim())) {
                        JOptionPane.showMessageDialog(view.getDialogForm(), "Tên món mới bị trùng với một món khác đang có!");
                        return;
                    }
                }
            }

            // --- 5. Thực hiện Lưu ---
            boolean success;
            if (isEdit) {
                m.setMaMon(currentMaMon);
                success = dao.update(m);
            } else {
                m.setMaMon(dao.getNewID());
                success = dao.add(m);
            }

            if (success) {
                JOptionPane.showMessageDialog(view.getDialogForm(), (isEdit ? "Cập nhật" : "Thêm mới") + " món ăn thành công!");
                view.getDialogForm().dispose();
                loadData("");
                reload();
                currentMaMon = null;
            } else {
                JOptionPane.showMessageDialog(view.getDialogForm(), "Thao tác thất bại. Vui lòng kiểm tra lại!");
            }
        });

        // ===== XÓA =====
        view.addXoaListener(e -> {
            int row = view.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(view, "Chọn món cần xoá!");
                return;
            }

            String ma = view.getTable().getValueAt(row, 0).toString();

            if (JOptionPane.showConfirmDialog(view, "Xóa món " + ma + "?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                if (dao.delete(ma)) {
                    reload(); // 🔥 cập nhật bên đặt món luôn
                    JOptionPane.showMessageDialog(view, "Xóa thành công!");
                    loadData("");
                }
            }
        });

        // ===== CLICK TABLE =====
        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.getSelectedRow() >= 0) {
                int r = view.getSelectedRow();
                try {
                    String ma = view.getTable().getValueAt(r, 0).toString();
                    MonAn m = dao.getByID(ma);
                    if (m != null) view.fillForm(m);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // ===== REMOVE ACCENT =====
    private String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    // ===== LOAD DANH MỤC =====
    public void loadDanhMucToComboBox() {
        JComboBox<String> cbo = view.getCboDanhMuc();
        cbo.removeAllItems();
        cbo.addItem("");
        for (String[] dm : danhMucDAO.getAll()) {
            cbo.addItem(dm[1]);
        }
    }

    // ===== LOAD DATA =====
    private void loadData(String keyword) {
        view.clearTable();
        String searchKey = removeAccent(keyword.toLowerCase());

        for (MonAn m : dao.getAll()) {
            String tenMonKoDau = removeAccent(m.getTenMon().toLowerCase());
            if (searchKey.isEmpty() || tenMonKoDau.contains(searchKey)) {
                view.addRow(m);
            }
        }
    }

    // ===== RELOAD (QUAN TRỌNG) =====
    private void reload() {
        loadData("");
        view.clearForm();

        // 🔥 CẬP NHẬT BÊN ĐẶT MÓN
        if (datMonController != null) {
            datMonController.loadMenu();
        }
    }
}