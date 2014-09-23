package com.iizuka.dev;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UserAuthServlet extends HttpServlet {
	public void doGet(HttpServletRequest ret, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain;charset=UTF-8");
		 
		//ユーザーサービスの取得
		UserService us = UserServiceFactory.getUserService();
		User user = us.getCurrentUser();
 
		if(user == null){
			resp.getWriter().println("ログインしていません");
		}else{
			resp.getWriter().println("ログイン状態です");
			resp.getWriter().println("認証ドメイン:" + user.getAuthDomain());
			resp.getWriter().println("ニックネーム:" + user.getNickname());
			resp.getWriter().println("ユーザID	:" + user.getUserId());
			resp.getWriter().println("Eメール	 :" + user.getEmail());
		}
	}
}
