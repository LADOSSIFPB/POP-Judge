package br.edu.popjudge.bean;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.edu.popjudge.database.dao.UsuarioDAO;

@ManagedBean(name="usuario")
public class UsuarioBean {
	private String password;
	private String username;
	private int idUser;
	private String dir;
	
	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	
	public String entrar() throws IOException, SQLException{
		if(this.username.equals("Admin") && this.password.equals("admin123")){
			FacesContext context = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
            session.setAttribute("login", this.username);
	        session.setAttribute("password", this.password);
            this.username = null;
    		this.password = null;
    		return "/webapp/admin/indexAdmin.xhtml?faces-redirect=true";
		}
		
		UsuarioDAO ud = new UsuarioDAO();
		UsuarioBean usuario = ud.get(this.username);
		if(usuario.getUsername().equals(this.username) && usuario.getPassword().equals(this.password)){
			FacesContext context = FacesContext.getCurrentInstance();
	        HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
	        session.setAttribute("login", usuario.getUsername());
	        session.setAttribute("password", usuario.getPassword());
	        session.setAttribute("idUser", usuario.getIdUser());
	        session.setAttribute("dir", usuario.getDir());
	        this.username = null;
			this.password = null;
	        return "/webapp/user/indexUser.xhtml?faces-redirect=true";
		}
			
		this.username = null;
		this.password = null;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", " Usuário ou senha incorretos"));	
		return "";
	}
	
	public void sair() throws IOException{
		this.username = null;
		this.password = null;
		invalidateSession();
		FacesContext.getCurrentInstance().getExternalContext().redirect("/POP-Judge/");
	}
	
	public static void invalidateSession() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
        session.removeAttribute("usuario");
        session.invalidate();
	}
	
	public void newUser() throws SQLException, IOException{
		try {
			UsuarioBean u = new UsuarioBean();
			u.setUsername(this.username);
			u.setPassword(this.password);
			u.setDir(this.dir);
			u.setIdUser(this.idUser);
			UsuarioDAO ud = new UsuarioDAO();
			ud.insert(u);
			FacesContext.getCurrentInstance().getExternalContext().redirect("/POP-Judge/webapp/admin/newUser.xhtml");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuário criado com sucesso", ""));
		} catch(SQLException e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nome de login já utilizado", ""));
		}
	}
	
	public void mudarSenha() throws SQLException{
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
		UsuarioBean u = new UsuarioBean();
		u.setPassword(this.password);
		u.setIdUser((Integer)session.getAttribute("idUser"));
		UsuarioDAO ud = new UsuarioDAO();
		ud.update(u);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Senha modificada com sucesso", ""));
	}

}
