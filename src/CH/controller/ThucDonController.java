package CH.controller;

import CH.dao.DanhMucDAO;
import CH.dao.ThucDonDAO;
import CH.model.MonAn;
import CH.view.ThucDonView;
import javax.swing.*;

public class ThucDonController {
    private ThucDonView view;
    private ThucDonDAO dao;
    private DatMonController datMonController;

    public ThucDonController(ThucDonView view, DatMonController datMonController) {
        this.view = view;
        this.dao = new ThucDonDAO();
        this.datMonController = datMonController;

        loadData();
        loadDanhMucToComboBox();

        // ===== THÊM =====
        view.addThemListener(e -> {
            MonAn m = view.getMonAnInfo();

            if (m.getTenMon().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Tên món không được để trống!");
                return;
            }

            m.setMaMon(dao.getNewID());

            if (dao.add(m)) {
                reload();
                JOptionPane.showMessageDialog(view, "Thêm thành công!");
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ===== SỬA =====
        view.addSuaListener(e -> {
            int row = view.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(view, "Chọn món cần sửa!");
                return;
            }
            if (view.getSelectedRow() < 0) return;

            if (dao.update(view.getMonAnInfo())) {
                reload();
                JOptionPane.showMessageDialog(view, "Sửa thành công!");
            } else {
                JOptionPane.showMessageDialog(view, "Sửa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ===== XÓA =====
        view.addXoaListener(e -> {
            int row = view.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(view, "Chọn món cần xoá!");
                return;
            }
            if (view.getSelectedRow() < 0) return;

            String ma = view.getMonAnInfo().getMaMon();

            if (JOptionPane.showConfirmDialog(view, "Xóa " + ma + "?") == JOptionPane.YES_OPTION) {
                if (dao.delete(ma)) {
                    reload();
                    JOptionPane.showMessageDialog(view, "Xóa thành công!");
                }
            }
        });

        // ===== RESET =====
        view.addResetListener(e -> view.clearForm());

        // ===== CLICK TABLE =====
        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.getSelectedRow() >= 0) {
                int r = view.getSelectedRow();
                try {
                    String ma = view.getTable().getValueAt(r, 0).toString();
                    String ten = view.getTable().getValueAt(r, 1).toString();
                    String giaStr = view.getTable().getValueAt(r, 2).toString().replace(",", "");
                    String dvt = view.getTable().getValueAt(r, 3).toString();

                    String danhMuc = view.getTable().getValueAt(r, 4) != null
                            ? view.getTable().getValueAt(r, 4).toString()
                            : "";

                    String hinhAnh = view.getTable().getValueAt(r, 5) != null
                            ? view.getTable().getValueAt(r, 5).toString()
                            : "";

                    view.fillForm(new MonAn(
                            ma,
                            ten,
                            Double.parseDouble(giaStr),
                            dvt,
                            hinhAnh,
                            danhMuc
                    ));

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private DanhMucDAO danhMucDAO = new DanhMucDAO();

    public void loadDanhMucToComboBox() {
        JComboBox<String> cbo = view.getCboDanhMuc();

        cbo.removeAllItems();
        cbo.addItem(""); // mặc định trống

        for (String[] dm : danhMucDAO.getAll()) {
            cbo.addItem(dm[1]);
        }

        cbo.setSelectedIndex(0);
    }

    private void loadData() {
        view.clearTable();
        for (MonAn m : dao.getAll()) {
            view.addRow(m);
        }
    }

    private void reload() {
        loadData();
        view.clearForm();

        if (datMonController != null) {
            datMonController.loadMenu();
        }
    }
}