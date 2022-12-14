package dao;

import java.util.ArrayList;
import java.util.List;

import model.Customer;
import model.Member;
import model.Order;
import model.OrderDetail;
import model.Product;
import model.Shipper;
import model.WareHouse;

public class OrderDAO extends DAO{
    public OrderDAO() {
        
    }
    
    public void newCart (Order o) {
        String sql = "insert into tblOrder (statusOrder, tblCustomerid)"
                + " values (?, ?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, o.getStatusOrder());
            ps.setInt(2, o.getCustomer().getId());
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    
    public List<Order> getListOrderByOrder (Order o) {
        List <Order> listOrder = new ArrayList<>();
//        String sql = "call getListOrderByOrder(?, ?, ?, ?)";
        String sql = "select * from tblorder\r\n"
                + "where id = ? or\r\n"
                + "    tblCustomerid = ?";
        
        try {
            ps= con.prepareStatement(sql);
            ps.setInt(1, o.getId());
            ps.setInt(2, o.getCustomer().getId());
//            ps.setString(3, o.getStatusDelivery());
//            ps.setString(4, o.getStatusOrder());
            rs = ps.executeQuery();
            while (rs.next()) {
               Order order = new Order();
               order.setId(rs.getInt("id"));
               order.setPaymentType(rs.getString("paymentType"));
               
               order.setOrderDate(rs.getDate("orderDate"));
               order.setPaymentDate(rs.getDate("paymentDate"));
               order.setCancelDate(rs.getDate("cancelDate"));
               order.setDeliveryDate(rs.getDate("deliveryDate"));

               order.setReasonCancel(rs.getString("reasonCancel"));      
               order.setStatusDelivery(rs.getString("statusDelivery"));
               order.setStatusOrder(rs.getString("statusOrder"));
               order.setNote(rs.getString("note"));
               
               //customer
               MemberDAO memberDAO = new MemberDAO();
               CustomerDAO customerDAO = new CustomerDAO();
               Customer customer = customerDAO.getCustomerByID(memberDAO.getMemberByID(rs.getInt("tblCustomerid")));
               order.setCustomer(customer);
               

               
               //list OrderDetail
               OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
               List<OrderDetail> list = orderDetailDAO.getOrderDetailByOrder(order);
               order.setListOrderDetail(list);
               //total
               order.setTotalAmount(order.totalAmountOrder(list));
               
               listOrder.add(order);
              
            }
        } catch (Exception e) {
        }
        return listOrder;
    }
    
    public void updateOrder (Order order) { //ham update cancel ben duoi
        String sql = "update tblorder\r\n"
                + "set \r\n"
                + "    paymentType = ?,\r\n"
                + "    orderDate = ?,\r\n"
                + "    paymentDate = ?,\r\n"
                + "    cancelDate = null,\r\n"
                + "    reasonCancel = null,\r\n"
                + "    statusDelivery = ?,\r\n"
                + "    statusOrder = ?\r\n"
                + "where id = ?";
        
        try {
            ps = con.prepareStatement(sql);
            
            ps.setString(1, order.getPaymentType());
            ps.setDate(2, order.getOrderDate());
            ps.setDate(3, order.getPaymentDate());

            ps.setString(4, order.getStatusDelivery());
            ps.setString(5, order.getStatusOrder());
            
            ps.setInt(6, order.getId());
            ps.executeUpdate();
                
        } catch (Exception e) {
        }
    }

    
    public Order getCartByCustomer (Customer customer) {
        String sql = "select * from tblOrder"
                + " where statusOrder = 'cart' and tblCustomerid = ? limit 1";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, customer.getId());
            rs = ps.executeQuery();
            while (rs.next()) {

                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setStatusOrder(rs.getString("statusOrder"));
                order.setNote(rs.getString("note"));
                order.setCustomer(customer);
                
                OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
                List<OrderDetail> list = orderDetailDAO.getOrderDetailByOrder(order);
                order.setListOrderDetail(list);
                
                order.setTotalAmount(order.totalAmountOrder(list));
                
                return order;
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    public Order getOrderByID (int id) {
        String sql = "select * from tblorder\r\n"
                + "where id = ?";
        try {
            ps= con.prepareStatement(sql);
            ps.setInt(1, id); String a; 
            rs = ps.executeQuery();
            while (rs.next()) {
               Order order = new Order();
               order.setId(rs.getInt("id"));
               order.setPaymentType(rs.getString("paymentType"));
               
               order.setOrderDate(rs.getDate("orderDate"));
               order.setPaymentDate(rs.getDate("paymentDate"));
               order.setCancelDate(rs.getDate("cancelDate"));
               order.setDeliveryDate(rs.getDate("deliveryDate"));

               order.setReasonCancel(rs.getString("reasonCancel"));      
               order.setStatusDelivery(rs.getString("statusDelivery"));
               order.setStatusOrder(rs.getString("statusOrder"));
               order.setNote(rs.getString("note"));
               
               //customer
               MemberDAO memberDAO = new MemberDAO();
               CustomerDAO customerDAO = new CustomerDAO();
               Customer customer = customerDAO.getCustomerByID(memberDAO.getMemberByID(rs.getInt("tblCustomerid")));
               order.setCustomer(customer);
               
               //shipper
                        
//                   Shipper shipper = new Shipper(memberDAO.getMemberByID(rs.getInt("tblShipper")));
//                   order.setShipper(shipper); 
               
               //list OrderDetail
               OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
               List<OrderDetail> list = orderDetailDAO.getOrderDetailByOrder(order);
               order.setListOrderDetail(list);
               //total
               order.setTotalAmount(order.totalAmountOrder(list));
               
               return order;
              
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    public void cancelOrder (Order order) {
        String sql = "update tblorder\r\n"
                + "set \r\n"
                + "    cancelDate = ?,\r\n"
                + "    reasonCancel = ?,\r\n"
                + "    statusDelivery = ?\r\n"
                + "where id = ?";
        
        try {
            ps = con.prepareStatement(sql);
            

            ps.setDate(1, order.getCancelDate());
            ps.setString(2, order.getReasonCancel());
            ps.setString(3, order.getStatusDelivery());
            
            ps.setInt(4, order.getId());
            ps.executeUpdate();
                
        } catch (Exception e) {
        }
    }
    
    public void selectShipper (Order order) {
        String sql = "update tblorder\r\n"
                + "set \r\n"
                + "    tblShipperid = ?\r\n"
                + "where id = ?";
        
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, order.getShipper().getId());
            ps.setInt(2, order.getId());
            ps.executeUpdate();
                
        } catch (Exception e) {
        }
    }
    
    
}
