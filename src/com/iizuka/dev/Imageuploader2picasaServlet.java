package com.iizuka.dev;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.data.media.MediaStreamSource;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

@SuppressWarnings("serial")
public class Imageuploader2picasaServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		//ユーザーサービスの取得
		UserService us = UserServiceFactory.getUserService();
		User user = us.getCurrentUser();
		if (user == null) {
			resp.getWriter().println("ログインしていません");
			return;
		}

		ServletFileUpload upload = new ServletFileUpload();
		FileItemIterator iter;
		try {
			iter = upload.getItemIterator(req);
		} catch (FileUploadException e1) {
			e1.printStackTrace();
			return;
		}
		try {
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				String fileName = item.getName();
				InputStream stream = item.openStream();
				if (item.isFormField()) {
					continue;
				} else {
					MediaStreamSource mediaSource = new MediaStreamSource(stream, "image/jpeg");
					String iid = null;
					try {
						iid = uploadImage(mediaSource, fileName, "test");
						PersistenceManager pm = PMF.get().getPersistenceManager();
						ImageRecord record = new ImageRecord(iid, user);
						try {
							pm.makePersistent(record);
						} finally {
							pm.close();
						}
					} catch (ServiceException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void storeNewImageInfo(String iid, User user, String name, String fileName) {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	}
	
	private void uploadImage() throws IOException, ServiceException {
	    File imageFile = new File("himawari1204x800-5227.jpg");       //画像ファイル
	    String title = imageFile.getName();       //タイトル
	    String description = imageFile.getName(); //説明

		// Google AppsもしくはGoogleアカウントのメールアドレスとパスワードを設定
		String username = "google-email";
		String password = "google-password";

	    PicasawebService myService = new PicasawebService(Imageuploader2picasaServlet.class.getName());
	    myService.setUserCredentials(username, password);
	    
	    URL albumPostUrl = new URL("https://picasaweb.google.com/data/feed/api/user/" + username + "?kind=album");

	    PhotoEntry myPhoto = new PhotoEntry();
	    myPhoto.setTitle(new PlainTextConstruct(title));
	    myPhoto.setDescription(new PlainTextConstruct(description));
	    myPhoto.setClient(Imageuploader2picasaServlet.class.getName());

	    MediaFileSource myMedia = new MediaFileSource(imageFile, "image/jpeg");
	    myPhoto.setMediaSource(myMedia);

	    PhotoEntry returnedPhoto = myService.insert(albumPostUrl, myPhoto);
	    System.out.println("uploaded as "+returnedPhoto.getId());
	}

	private String uploadImage(MediaSource mediaSource, String title, String description) throws IOException, ServiceException {
		// Google AppsもしくはGoogleアカウントのメールアドレスとパスワードを設定
		String username = "google-user";
		String password = "google-pass";

	    PicasawebService myService = new PicasawebService(Imageuploader2picasaServlet.class.getName());
	    myService.setUserCredentials(username, password);
	    myService.setConnectTimeout(30000);
	    myService.setReadTimeout(30000);
	    
	    URL albumPostUrl = new URL("https://picasaweb.google.com/data/feed/api/user/" + username + "?kind=album");

	    PhotoEntry myPhoto = new PhotoEntry();
	    myPhoto.setTitle(new PlainTextConstruct(title));
	    myPhoto.setDescription(new PlainTextConstruct(description));
	    myPhoto.setClient(Imageuploader2picasaServlet.class.getName());

	    myPhoto.setMediaSource(mediaSource);

	    PhotoEntry returnedPhoto = myService.insert(albumPostUrl, myPhoto);
	    System.out.println("uploaded as "+returnedPhoto.getId());
	    return returnedPhoto.getId();
	}
}
