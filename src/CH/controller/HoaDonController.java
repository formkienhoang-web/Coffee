package CH.controller;

import CH.dao.HoaDonDAO;
import CH.model.HoaDon;
import CH.model.ChiTietHoaDon;
import CH.view.HoaDonView;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import CH.view.ChiTietHoaDonView;

public class HoaDonController {
    private HoaDonView view;
    private HoaDonDAO dao;

    public HoaDonController(HoaDonView view) {
        this.view = view;
        this.dao = new HoaDonDAO();
        
        dao.addSampleDataIfEmpty(); 

        loadData();

//        view.addThemListener(new AddListener());
        view.addSuaListener(new EditListener());
        view.addXoaListener(new DeleteListener());
        view.addResetListener(e -> view.clearForm());
        
        view.addXemChiTietListener(e -> showHoaDonChiTiet());
        
        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromTable();
            }
        });
    }

    public void loadData() {
        view.clearTable();
        List<HoaDon> list = dao.getAll();
        for (HoaDon hd : list) {
            view.addRow(hd);
        }
    }

    // Hàm lấy dữ liệu từ bảng đổ lên form nhập liệu
    private void fillFormFromTable() {
        int row = view.getSelectedRow();
        if (row >= 0) {
            String maHD = view.getTable().getValueAt(row, 0).toString();
            String tenNV = view.getTable().getValueAt(row, 1).toString();
            String tenKH = view.getTable().getValueAt(row, 2).toString();
            String ngayLap = view.getTable().getValueAt(row, 3).toString();
            
            String strTien = view.getTable().getValueAt(row, 4).toString();
            double tongTien = 0;
            try {
                strTien = strTien.replace(" VNĐ", "").replace(",", "").replace(".", "").trim();
                tongTien = Double.parseDouble(strTien);
            } catch (Exception ex) {}

            HoaDon hd = new HoaDon(maHD, tenNV, tenKH, ngayLap, tongTien);
            view.fillForm(hd);
        }
    }
    
    // Validate đơn giản
    private boolean validateForm(HoaDon hd) {
        if (hd.getTenNV().isEmpty() || hd.getNgayLap().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập Nhân viên và Ngày lập!");
            return false;
        }
        return true;
    }


    class AddListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            HoaDon hd = view.getHoaDonInfo();
            

            if (!validateForm(hd)) return;
            
            String newID = dao.getNewID();
            hd.setMaHD(newID); 
            

            if (dao.add(hd)) {
                JOptionPane.showMessageDialog(view, "Thêm hóa đơn thành công: " + newID);
                loadData();
                view.clearForm();
            } else {
                JOptionPane.showMessageDialog(view, "Thêm thất bại!");
            }
        }
    }

    class EditListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (view.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(view, "Chọn hóa đơn cần sửa!");
                return;
            }
            HoaDon hd = view.getHoaDonInfo();
            
            if (!validateForm(hd)) return;

            if (dao.update(hd)) {
                JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật thất bại!");
            }
        }
    }

    class DeleteListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (view.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(view, "Chọn hóa đơn cần xóa!");
                return;
            }
            int row = view.getSelectedRow();
            String maHD = view.getTable().getValueAt(row, 0).toString();
            
            int confirm = JOptionPane.showConfirmDialog(view, "Bạn chắc chắn muốn xóa HĐ " + maHD + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.delete(maHD)) {
                    JOptionPane.showMessageDialog(view, "Xóa thành công!");
                    loadData();
                    view.clearForm();
                } else {
                    JOptionPane.showMessageDialog(view, "Xóa thất bại!");
                }
            }
        }
    }

    private void showHoaDonChiTiet() {
        int row = view.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn hóa đơn để xem chi tiết!");
            return;
        }
        String maHD = view.getTable().getValueAt(row, 0).toString();
        String tenKH = view.getTable().getValueAt(row, 2).toString();
        String strTien = view.getTable().getValueAt(row, 4).toString();
        double tongTien = 0;
        try {
            strTien = strTien.replace(" VNĐ", "").replace(",", "").replace(".", "").trim();
            tongTien = Double.parseDouble(strTien);
        } catch (Exception e) { e.printStackTrace(); }

        List<ChiTietHoaDon> details = dao.getChiTiet(maHD);
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        
        
        ChiTietHoaDonView dialog = new ChiTietHoaDonView(parentFrame);
        dialog.setDetails(maHD, tenKH, tongTien, details);
        dialog.setVisible(true);
    }
}