package CH.controller;

import CH.dao.DanhMucDAO;
import CH.view.DanhMucView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class DanhMucController {

    private DanhMucView view;
    private DanhMucDAO dao;
    private ThucDonController thucDonController;
    private DatMonController datMonController;

    public void setDatMonController(DatMonController controller){
        this.datMonController = controller;
    }

    public void setThucDonController(ThucDonController controller){
        this.thucDonController = controller;
    }

    public DanhMucController(DanhMucView view){
        this.view = view;
        this.dao = new DanhMucDAO();

        loadDataToView();

        // ✅ FIX DOUBLE LISTENER
        removeAllListeners();

        // CRUD
        view.getBtnThem().addActionListener(new AddListener());
        view.getBtnSua().addActionListener(new EditListener());
        view.getBtnXoa().addActionListener(new DeleteListener());
        view.getBtnReset().addActionListener(e -> view.clearForm());

        // 🔥 SEARCH BUTTON
        view.addTimKiemListener(e -> timKiem());

        // 🔥 REALTIME SEARCH
        view.addLiveSearchListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { timKiem(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { timKiem(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { timKiem(); }
        });

        // Click table → đổ lên form
        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = view.getTable().getSelectedRow();
                if (r >= 0) {
                    String ma = view.getTable().getValueAt(r,0).toString();
                    String ten = view.getTable().getValueAt(r,1).toString();
                    view.setForm(ma,ten);
                }
            }
        });
    }

    // ================= REMOVE LISTENER =================
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
        for (ActionListener al : view.getBtnReset().getActionListeners()) {
            view.getBtnReset().removeActionListener(al);
        }
    }

    // ================= TẠO MÃ =================
    private String taoMaDM() {
        int max = 0;

        for (String[] dm : dao.getAll()) {
            String ma = dm[0];

            if (ma.startsWith("DM")) {
                try {
                    int so = Integer.parseInt(ma.substring(2));
                    if (so > max) max = so;
                } catch (Exception e) {}
            }
        }

        return String.format("DM%02d", max + 1);
    }

    // ================= LOAD =================
    public void loadDataToView(){
        view.clearTable();
        List<String[]> list = dao.getAll();
        for(String[] dm : list){
            view.addRow(dm);
        }
    }

    // ================= 🔥 SEARCH =================
    private void timKiem() {
        String keyword = view.getTuKhoaTimKiem().toLowerCase();

        // 👉 nếu rỗng → load lại full
        if (keyword.isEmpty()) {
            loadDataToView();
            return;
        }

        view.clearTable();
        List<String[]> list = dao.getAll();

        for (String[] dm : list) {
            if (dm[1].toLowerCase().contains(keyword)) {
                view.addRow(dm);
            }
        }
    }

    // ================= VALIDATE =================
    private boolean validateForm(String ma, String ten){
        if(ma.trim().isEmpty()){
            JOptionPane.showMessageDialog(view,"Mã danh mục không được để trống!");
            return false;
        }

        if(ten.trim().isEmpty()){
            JOptionPane.showMessageDialog(view,"Tên danh mục không được để trống!");
            return false;
        }

        return true;
    }

    // ================= ADD =================
    class AddListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String ten = view.getTenDM();

                if (ten.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(view, "Tên danh mục không được để trống!");
                    return;
                }

                String maTuDong = taoMaDM();

                if (dao.insert(maTuDong, ten)) {
                    JOptionPane.showMessageDialog(view, "Thêm thành công!");
                    loadDataToView();
                    view.clearForm();

                    if(thucDonController != null){
                        thucDonController.loadDanhMucToComboBox();
                    }
                    if(datMonController != null){
                        datMonController.reloadDanhMuc();
                    }
                } else {
                    JOptionPane.showMessageDialog(view, "Thêm thất bại!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Lỗi: " + ex.getMessage());
            }
        }
    }

    // ================= EDIT =================
    class EditListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = view.getTable().getSelectedRow();

            if(row < 0){
                JOptionPane.showMessageDialog(view,"Vui lòng chọn danh mục để sửa!");
                return;
            }

            String ma = view.getMaDM();
            String ten = view.getTenDM();

            if(!validateForm(ma,ten)) return;

            if(dao.update(ma,ten)){
                JOptionPane.showMessageDialog(view,"Sửa thành công!");
                loadDataToView();
                view.clearForm();

                if(thucDonController != null){
                    thucDonController.loadDanhMucToComboBox();
                }
                if(datMonController != null){
                    datMonController.reloadDanhMuc();
                }
            } else {
                JOptionPane.showMessageDialog(view,"Sửa thất bại!");
            }
        }
    }

    // ================= DELETE =================
    class DeleteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = view.getTable().getSelectedRow();

            if (row >= 0) {
                String ma = view.getTable().getValueAt(row, 0).toString();

                int confirm = JOptionPane.showConfirmDialog(
                        view,
                        "Xóa danh mục " + ma + "?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    if (dao.delete(ma)) {
                        JOptionPane.showMessageDialog(view, "Xóa thành công!");
                        loadDataToView();
                        view.clearForm();

                        if(thucDonController != null){
                            thucDonController.loadDanhMucToComboBox();
                        }
                        if(datMonController != null){
                            datMonController.reloadDanhMuc();
                        }
                    } else {
                        JOptionPane.showMessageDialog(view, "Xóa thất bại!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn danh mục để xóa!");
            }
        }
    }

}