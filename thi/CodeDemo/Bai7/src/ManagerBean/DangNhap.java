package ManagerBean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import Model.DangKy;
import Model.TaiKhoan;
import Model.ThongTin;

@ManagedBean(name = "DN") 
@RequestScoped 
@SessionScoped
public class DangNhap {
  	public static Statement stmtObj;
    public static Connection connObj;
    public static ResultSet resultSetObj;
    public static PreparedStatement pstmt;
    
    public TaiKhoan tk = new TaiKhoan();
    public DangKy dk = new DangKy();
    public ThongTin tt = new ThongTin(); 
  
    

	public ThongTin getTt() {
		return tt;
	}

	public void setTt(ThongTin tt) {
		this.tt = tt;
	}

	public TaiKhoan getTk() {
		return tk;
	}

	public DangKy getDk() {
		return dk;
	}

	public void setDk(DangKy dk) {
		this.dk = dk;
	}

	public void setTk(TaiKhoan tk) {
		this.tk = tk;
	}

	public static Connection getConnection (){ 
	    
		try{  
	        Class.forName("com.mysql.jdbc.Driver");     
	        String db_url ="jdbc:mysql://localhost:3306/nguoidung",
	                db_userName = "root",
	                db_password = "";
	        connObj = DriverManager.getConnection(db_url,db_userName,db_password);  
	          
	    } catch(Exception sqlException) {  
	        sqlException.printStackTrace();
	    }  
	    return connObj;
		
	}
	
	// hàm sử lý dang nhap
	public String DangNhap(TaiKhoan tks) { 
		
		System.out.println("User: " + tks.getUser());
		System.out.println("Pass: " + tks.getPass());
        
        String navigationResult = "";
        
        try {      
        	stmtObj = getConnection().createStatement();   
            resultSetObj = stmtObj.executeQuery("select * from taikhoan where username = \'"+tks.getUser()+"\' and password = \'"+tks.getPass()+"\'");  
            if(resultSetObj.next()) {
            	navigationResult = "DanhSachNguoiDung.xhtml?faces-redirect=true";
            	System.out.println("Dang nhap thanh cong");
            	System.out.println("navigationResult: "+navigationResult);
            }else {
            	 navigationResult = "index.xhtml?faces-redirect=true";
            	 System.out.println("Dang nhap that bai");
			}
            connObj.close();
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
            System.out.println("----Loi: " + sqlException);
        }
        

        return navigationResult;      		
	}

	
	// ham dang ky thong tin 
	public String DangKy(DangKy dks) {
		
		System.out.println("UserName: " + dks.getUser());
		System.out.println("PassWord: " + dks.getPass());
		System.out.println("Email: " + dks.getEmail());
		System.out.println("Dia Chi: " + dks.getDiachi());
		
        int Res_TK = 0;
        int Res_TT = 0;
        String navigationResult =" ";
 
        try {      
            pstmt = getConnection().prepareStatement("insert into taikhoan (username, password) values (?, ?)");         
        
            pstmt.setString(1,  dks.getUser());
            pstmt.setString(2, dks.getPass());
            
            Res_TK = pstmt.executeUpdate();
            connObj.close();
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
            System.out.println("---- Loi tai khoan: " + sqlException);
        }
        if(Res_TK !=0) {
            try {      
                pstmt = getConnection().prepareStatement("insert into thongtin (email, diachi) values (?, ?)");         
            
                pstmt.setString(1,  dks.getEmail());
                pstmt.setString(2, dks.getDiachi());
                
                Res_TT = pstmt.executeUpdate();
                connObj.close();
            } catch(Exception sqlException) {
                sqlException.printStackTrace();
                System.out.println("----Loi thong tin: " + sqlException);
            } 
            
            if(Res_TT != 0) {
            	navigationResult ="index";
            }else {
            	System.out.println("---- Loi insearch thong tin: " );
            	navigationResult ="DangKy";
			}
        } else {
        	System.out.println("---- Loi insearch tai khoan: " );
        	navigationResult ="DangKy";
        }
        return navigationResult; 
	}

	
	// ham get danh sach nguoi dung
	public static ArrayList DanhSachNguoiDung() {
		ArrayList  list_TT = new ArrayList ();
	    try {
	        stmtObj = getConnection().createStatement();    
	        resultSetObj = stmtObj.executeQuery("select *from thongtin");    
	        while(resultSetObj.next()) {  
	            ThongTin tt = new ThongTin();  
	            tt.setId(resultSetObj.getInt("id"));
	            tt.setEmail(resultSetObj.getString("email"));  
	            tt.setDiachi(resultSetObj.getString("diachi"));  
	            System.out.println("id: " +tt.getId());
	            list_TT.add(tt);  
	        }   
	        System.out.println("so luong danh sach: " + list_TT.size());
	        
	        connObj.close();
	    } catch(Exception sqlException) {
	        sqlException.printStackTrace();
	    } 
		return list_TT;
	} 
	
	
	   public static String LoadThonngTin(int userID) {
	        ThongTin thongtin = null;
	        System.out.println("Thong ton nguoi dung co Id: " + userID);
	 
	        Map<String,Object> sessionMapObj = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
	        
	        try {
	            stmtObj = getConnection().createStatement();    
	             
	            resultSetObj = stmtObj.executeQuery("select * from thongtin where id = "+userID);   
	             
	            if(resultSetObj != null) {
	                resultSetObj.next();
	                thongtin = new ThongTin(); 
	                thongtin.setId(resultSetObj.getInt("id"));
	                thongtin.setEmail(resultSetObj.getString("email"));
	                thongtin.setDiachi(resultSetObj.getString("diachi"));
	                
	            }
	            System.out.println("email: "+thongtin.getEmail());
	            System.out.println("Dia chi: "+thongtin.getDiachi());
	            sessionMapObj.put("thongtin_edit", thongtin);
	            
	            connObj.close();
	        } catch(Exception sqlException) {
	            sqlException.printStackTrace();
	        }
	        return "/ThongTinEdit.xhtml?faces-redirect=true";
	    }

    public static String SuaThongTin(ThongTin TTObj) {
    	 System.out.println("----Sua thong tin nguoi dung co Id: " + TTObj.getId());
        try {
            pstmt = getConnection().prepareStatement("update thongtin set email=?, diachi=? where  id=?");    
            pstmt.setString(1,TTObj.getEmail());  
            pstmt.setString(2,TTObj.getDiachi());
            pstmt.setInt(3,TTObj.getId()); 
            pstmt.executeUpdate();
            connObj.close();            
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        return "/DanhSachNguoiDung.xhtml?faces-redirect=true";
    }
	
    public  String XoaNguoiDung(int UserId){
        System.out.println(" Xoa nguoi dung User Id: " + UserId);
        try {
            pstmt = getConnection().prepareStatement("delete from thongtin where id = "+UserId);  
            pstmt.executeUpdate();  
            connObj.close();
        } catch(Exception sqlException){
            sqlException.printStackTrace();
        }
        return "/DanhSachNguoiDung.xhtml?faces-redirect=true";
    }
	
    

	
}
