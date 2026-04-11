package CH.controller;

import CH.dao.DanhMucDAO;
import CH.view.DanhMucView;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DanhMucController {

    private DanhMucView view;
    private DanhMucDAO dao;
    private ThucDonController thucDonController;
    private DatMonController datMonController;
    private boolean isEdit = false;

    public void setDatMonController(DatMonController controller) {
        this.datMonController = controller;
    }

    public void setThucDonController(ThucDonController controller) {
        this.thucDonController = controller;
    }

    public DanhMucController(DanhMucView view) {
        this.view = view;
        this.dao = new DanhMucDAO();

        // 🔥 FIX DOUBLE LISTENER
        removeAllListeners();

        loadDataToView();

        // ===== THÊM =====
        view.getBtnThem().addActionListener(e -> {
            isEdit = false;
            view.clearForm();
            view.getDialogForm().setTitle("Thêm Danh Mục Mới");
            view.getDialogForm().setVisible(true);
        });

        // ===== SỬA =====
        view.getBtnSua().addActionListener(e -> {
            int row = view.getTable().getSelectedRow();
            if (row >= 0) {
                isEdit = true;
                String ma = view.getTable().getValueAt(row, 0).toString();
                String ten = view.getTable().getValueAt(row, 1).toString();
                view.setForm(ma, ten);
                view.getDialogForm().setTitle("Chỉnh Sửa Danh Mục");
                view.getDialogForm().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn danh mục trên bảng để sửa!");
            }
        });

        // ===== LƯU =====
        view.getBtnLuu().addActionListener(new SaveListener());

        // ===== XÓA =====
        view.getBtnXoa().addActionListener(new DeleteListener());

        // ===== SEARCH KHÔNG DẤU =====
        view.getTxtSearch().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { search(); }
            @Override
            public void removeUpdate(DocumentEvent e) { search(); }
            @Override
            public void changedUpdate(DocumentEvent e) { search(); }

            private void search() {
                String keyword = view.getTuKhoaTimKiem();
                loadDataWithFilter(keyword);
            }
        });
    }

    // 🔥 REMOVE LISTENER
    private void removeAllListeners() {
        for (ActionListener al : view.getBtnThem().getActionListeners()) {
            view.getBtnThem().removeActionListener(al);
        }
        for (ActionListener al : view.getBtnSua().getActionListeners()) {
            view.getBtnSua().removeActionListener(al);
        }
        for (ActionListener al : view.getBtnXoa().getActionListeners()) {
            view.getBtnXoa().removeActionListener(al);
        }
        for (ActionListener al : view.getBtnLuu().getActionListeners()) {
            view.getBtnLuu().removeActionListener(al);
        }
    }

    // 🔥 HÀM BỎ DẤU (GIỐNG THỰC ĐƠN)
    private String removeAccent(String s) {
        String temp = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        return temp.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D');
    }

    // ===== SAVE =====
    class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String ten = view.getTenDM().trim();

            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Tên danh mục không được để trống!");
                return;
            }

            boolean success;
            if (!isEdit) {
                String maMoi = taoMaTuDong();
                success = dao.insert(maMoi, ten);
            } else {
                String maHienTai = view.getMaDM();
                success = dao.update(maHienTai, ten);
            }

            if (success) {
                JOptionPane.showMessageDialog(view, "Cập nhật dữ liệu thành công!");
                loadDataToView();
                view.getDialogForm().dispose();
                refreshOtherScreens();
            } else {
                JOptionPane.showMessageDialog(view, "Thao tác thất bại!");
            }
        }
    }

    // ===== DELETE =====
    class DeleteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = view.getTable().getSelectedRow();
            if (row >= 0) {
                String ma = view.getTable().getValueAt(row, 0).toString();
                int confirm = JOptionPane.showConfirmDialog(
                        view,
                        "Bạn có chắc muốn xóa danh mục " + ma + "?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    if (dao.delete(ma)) {
                        JOptionPane.showMessageDialog(view, "Xóa thành công!");
                        loadDataToView();
                        refreshOtherScreens();
                    } else {
                        JOptionPane.showMessageDialog(view, "Không thể xóa danh mục đang có món ăn!");
                    }
                }
            }
        }
    }

    // ===== LOAD =====
    private void loadDataToView() {
        view.clearTable();
        List<String[]> list = dao.getAll();
        for (String[] dm : list) {
            view.addRow(dm);
        }
    }

    // 🔥 SEARCH KHÔNG DẤU (ĐÃ FIX)
    private void loadDataWithFilter(String keyword) {
        view.clearTable();

        String searchKey = removeAccent(keyword.toLowerCase());

        List<String[]> list = dao.getAll();
        for (String[] dm : list) {
            String tenKoDau = removeAccent(dm[1].toLowerCase());

            if (searchKey.isEmpty() || tenKoDau.contains(searchKey)) {
                view.addRow(dm);
            }
        }
    }

    private String taoMaTuDong() {
        List<String[]> list = dao.getAll();
        if (list.isEmpty()) return "DM01";

        int max = 0;
        for (String[] dm : list) {
            try {
                int so = Integer.parseInt(dm[0].substring(2));
                if (so > max) max = so;
            } catch (Exception e) {}
        }
        return String.format("DM%02d", max + 1);
    }

    private void refreshOtherScreens() {
        if (thucDonController != null) {
            thucDonController.loadDanhMucToComboBox();
        }
        if (datMonController != null) {
            datMonController.reloadDanhMuc();
        }
    }
}